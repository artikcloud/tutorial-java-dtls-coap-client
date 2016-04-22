package cloud.artik.demo;

import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.elements.UDPConnector;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.ScandiumLogger;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DTLSClient {

    private static final String TRUST_STORE_LOCATION = "cacerts";
    private static final String TRUST_STORE_PASSWORD = "changeit";
    private static final String KEY_STORE_LOCATION = "clientKeyStore.jks";
    private final static String KEY_STORE_PASSWORD = "clientPass";
    private final static String KEY_STORE_ALIAS = "client";


    public static void main(String[] args) throws InterruptedException {
        boolean verbose = false;
        boolean doGet = true;
        String postJSON = null;
        String URI = null;
        int timeout = 10;

        // Parse input
        if (args.length > 0) {
            int index = 0;
            while (index < args.length) {
                String arg = args[index];
                if ("-usage".equals(arg) || "-help".equals(arg) || "-h".equals(arg) || "-?".equals(arg)) {
                    printUsage();
                } else if ("-X".equals(arg)) {
                    if (index + 1 >= args.length) {
                        printUsage();
                    } else {
                        String operation = args[index + 1];
                        if ("GET".equals(operation)) {
                            doGet = true;
                        } else if ("POST".equals(operation)) {
                            doGet = false;
                        } else {
                            printUsage();
                        }
                    }
                    index += 2;
                } else if ("-d".equals(arg)) {
                    if (index + 1 >= args.length) {
                        printUsage();
                    } else {
                        postJSON = args[index + 1];
                    }
                    index += 2;
                } else if ("-t".equals(arg)) {
                    if (index + 1 >= args.length) {
                        printUsage();
                    } else {
                        timeout = Integer.parseInt(args[index + 1]);
                    }
                    index += 2;
                } else if ("-v".equals(arg)) {
                    verbose = true;
                    index++;
                } else if (arg.startsWith("coaps://")) {
                    URI = arg;
                    index++;
                } else {
                    System.err.println("Unknown arg " + arg);
                    printUsage();
                }
            }
        }

        if (verbose) {
            CaliforniumLogger.initialize();
            CaliforniumLogger.setLevel(Level.FINE);
            ScandiumLogger.initialize();
            ScandiumLogger.setLevel(Level.FINE);
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.ALL);
            for (Handler h : Logger.getLogger("").getHandlers())
                h.setLevel(Level.ALL);
            Logger.getLogger(UDPConnector.class.toString()).setLevel(Level.ALL);

        } else {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.SEVERE);
            Logger.getLogger("").setLevel(Level.SEVERE);
        }

        if (URI == null || (!doGet && postJSON == null)) {
            printUsage();
        }

        if (doGet) {
            doGet(URI, timeout);
        } else {
            doPost(URI, postJSON);
        }

        System.exit(0);
    }

    private static void doPost(String postUri, String postJSON) {
        URI uri = null;

        try {
            uri = new URI(postUri);
        } catch (URISyntaxException e) {
            System.err.println("Invalid URI: " + e.getMessage());
            System.exit(-1);
        }

        DTLSConnector dtlsConnector = createDTLSConnector();

        CoapClient client = new CoapClient(uri);
        client.setEndpoint(new CoapEndpoint(dtlsConnector, NetworkConfig.getStandard())).setTimeout(0).useCONs();
        CoapResponse response = client.post(postJSON, MediaTypeRegistry.APPLICATION_JSON);

        if (response != null) {
            // System.out.println(response.getCode());
            // System.out.println(response.getOptions());
            // System.out.println(response.getResponseText());
            System.out.println(Utils.prettyPrint(response));
        } else {
            System.out.println("No response received.");
        }
    }

    private static void doGet(String getURI, int timeout) {
        URI uri = null;

        try {
            uri = new URI(getURI);
        } catch (URISyntaxException e) {
            System.err.println("Invalid URI: " + e.getMessage());
            System.exit(-1);
        }

        DTLSConnector dtlsConnector = createDTLSConnector();

        CoapClient client = new CoapClient(uri);
        client.setEndpoint(new CoapEndpoint(dtlsConnector, NetworkConfig.getStandard())).setTimeout(0).useCONs();
        CoapObserveRelation observeRelation = client.observe(
                new CoapHandler() {
                    @Override
                    public void onLoad(CoapResponse response) {
                        System.out.println(Utils.prettyPrint(response));
                    }

                    @Override
                    public void onError() {
                        System.err.println("Observe GET Failed");
                        System.exit(-1);
                    }
                });

        try {
            Thread.sleep(timeout * 1000);
        } catch (InterruptedException e) {
            //
        }

        observeRelation.proactiveCancel();
    }

    private static DTLSConnector createDTLSConnector() {
        DTLSConnector dtlsConnector = null;

        try {
            // load Java trust store
            Certificate[] trustedCertificates = loadTrustStore();

            // load client key store
            KeyStore keyStore = KeyStore.getInstance("JKS");
            InputStream in = DTLSClient.class.getClassLoader().getResourceAsStream(KEY_STORE_LOCATION);
            keyStore.load(in, KEY_STORE_PASSWORD.toCharArray());

            // Build DTLS config
            DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder(new InetSocketAddress(0));
            builder.setIdentity((PrivateKey) keyStore.getKey(KEY_STORE_ALIAS, KEY_STORE_PASSWORD.toCharArray()),
                    keyStore.getCertificateChain(KEY_STORE_ALIAS), false);
            builder.setTrustStore(trustedCertificates);

            // Create DTLS endpoint
            dtlsConnector = new DTLSConnector(builder.build());
        } catch (Exception e) {
            System.err.println("Error creating DTLS endpoint");
            e.printStackTrace();
            System.exit(-1);
        }

        return dtlsConnector;
    }

    private static Certificate[] loadTrustStore() throws Exception {
        // load client key store
        KeyStore trustStore = KeyStore.getInstance("JKS");
        InputStream in = DTLSClient.class.getClassLoader().getResourceAsStream(TRUST_STORE_LOCATION);
        trustStore.load(in, TRUST_STORE_PASSWORD.toCharArray());

        // load trustStore containing Artik Verisign intermediary
        TrustManagerFactory trustMgrFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustMgrFactory.init(trustStore);
        TrustManager trustManagers[] = trustMgrFactory.getTrustManagers();
        X509TrustManager defaultTrustManager = null;

        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                defaultTrustManager = (X509TrustManager) trustManager;
            }
        }

        return (defaultTrustManager == null) ? null : defaultTrustManager.getAcceptedIssuers();
    }


    private static void printUsage() {
        System.out.println("Usage: " + DTLSClient.class.getSimpleName() + " [-v] -X POST -d 'JSON-Payload' URI");
        System.out.println("Usage: " + DTLSClient.class.getSimpleName() + " [-v] -X GET [-t <sec>] URI");
        System.out.println("\nURI must be an absolute secure coap URI");
        System.out.println("  -d <data>   JSON data for POST request");
        System.out.println("  -t <sec>    Seconds to wait for Observe GET request (def: 10)");
        System.out.println("  -X GET      Perform an Observe GET request");
        System.out.println("  -X POST     Perform a POST request with data");
        System.out.println("  -v          Verbose logging");
        System.exit(-1);
    }
}

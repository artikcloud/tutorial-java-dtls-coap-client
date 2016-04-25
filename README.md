# DTLS CoAP client app

A tuorial DTLS CoAP client communicates with the ARTIK Cloud CoAP server.

Consult [ARTIK Cloud CoAP](https://developer.artik.cloud/documentation/connect-the-data/coap.html) for the explanation of the app usage and implementation.

Prerequisites
-------------

 - Java JDK 1.7 or above
 - Apache Maven 3.0.5 or above

Installation
---------------------

Run "mvn clean package" at the top of the source directory.
The executable jar `dtlsclient` is created in the target directory.

Usage
------

 - Post a data-only message

~~~shell 
java -jar target/dtlsclient-x.x.jar [-v] -X POST -d 'Message-JSON-Payload' URI
~~~

 - Get (Observe) actions

~~~shell 
java -jar target/dtlsclient-x.x.jar [-v] -X GET URI
~~~

The input arguments are explained below:

 - URI         an absolute secure coap URI (consult [Artik Cloud CoAP example](https://developer.artik.cloud/documentation/connect-the-data/coap.html) for URI examples)
 - -v          verbose logging
 - -X GET      perform an Observe GET request
 - -X POST     perform an POST request
 - -d <data>   POST request JSON data, which is a Artik Cloud message.
 - -t <sec>    Seconds to wait for Observe GET request (def: 10)

More about ARTIK Cloud
----------------------

If you are not familiar with ARTIK Cloud, we have extensive documentation at https://developer.artik.cloud/documentation

The full ARTIK Cloud API specification can be found at https://developer.artik.cloud/documentation/api-reference/

Check out advanced sample applications at https://developer.artik.cloud/documentation/samples/

To create and manage your services and devices on ARTIK Cloud, create an account at https://developer.artik.cloud

Also see the ARTIK Cloud blog for tutorials, updates, and more: http://artik.io/blog/cloud

License and Copyright
---------------------

Licensed under the Apache License. See [LICENSE](https://github.com/artikcloud/tutorial-java-dtls-coap-client/blob/master/LICENSE).

Copyright (c) 2016 Samsung Electronics Co., Ltd.

# DTLS CoAP client 

A tuorial DTLS CoAP client communicates with ARTIK Cloud CoAP server.

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

java -jar target/dtlsclient-1.0.jar

Usage: SecureClient [-v] -X POST -d 'JSON-Payload' URI

Usage: SecureClient [-v] -X GET URI

 - URI can be an absolute secure coap URI
 - -v          verbose logging
 - -X GET      perform an Observe GET request
 - -X POST     perform an POST request with data
 - -d <data>   JSON data for POST request
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

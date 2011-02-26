Application Name: RedmineJConnector
Author: Mohit Gupta 
Email: mohit.gupta@vedantatree.com
Web: http://www.vedantatree.com/

How to run:
Set the properties in redmine-connector.properties file for server address and security key
Build the System using build.xml. It is ANT based build
Use javadoc target in build file to generate the Java Documentation
Refer to documentation of RedmineConnector class for details of API
Refer to RCTestCase class for samples
Use the API as per your requirement

Points to Consider:
Application is current tested on Java 6. 
If you are trying to build it on Java 5, it may require some more jar files for XML parsing like Stax and Pull parsers.
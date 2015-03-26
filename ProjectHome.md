**RedmineJConnector** is a Redmine Java API (client) to interact with Redmine Server (http://www.redmine.org/) using its Rest API. Redmine provides Rest Services for various CRUD operations for data managed by it, like Projects, Issues, and Users etc. This connector helps to consume these Rest Services and hence enables to interact with Redmine Server for various CRUD operations on this data. It provides an abstraction layer over Redmine Rest Services and hence avoid to go into details of Rest implementation.

System picks its various configuration like Server Address, and Security Key etc from configuration file. Its build is based on ANT.


## Various Releases ##

### Release 1.1.0 beta ###
  * Generic Component to Build the URL for all requests
  * Support filtering parameter
  * Support Includes parameter
  * Work to support Users API
  * More test cases are in progress

### Release 1.0 ###
  * Get all Projects and Issues
    1. It is supported with Data Paginator feature. It is an 'iterator' based implementation.
    1. This feature will facilitate the user to fetch all data objects from Redmine in specific size pages.
    1. It should help in memory and performance management, which may get critical in case if we fetch all objects in one go.
  * Improved Exception Handling
  * Improved Java Doc
  * Improved build with inclusion of Java Doc generation and JUnit Test cases
  * JUnit test cases for all new implementation

### Release 0.7 ###
  * Basic CRUD support for Projects
    1. Create Project
    1. Update Project
    1. Delete Project
    1. Get Project by Id
  * Get all Projects is not supported
  * Basic CRUD support for Issues
    1. Create Issue
    1. Update Issue
    1. Delete Issue
    1. Get Issue by Id
  * Get all Issues was not supported
  * System picks the Redmine Server address, and security key from 'redmine-connector.properties' configuration file
  * Ant based build system
  * JUnit Test Case

## Sample Code ##
You can find the sample code with Test Cases


## Documentation ##
Run the build with 'javadoc' target, and you will get the documentation in 'javadocs' folder. Other documents can be found in docs folder. Refer to 'readme.txt' for initial information.


## Dependencies ##
  * Redmine - Version 1.4
  * ANT - For building the application
  * JIBX - For Java to XML and XML to Java conversion
  * Restlet - For accessing Rest API of Redmine
  * Commons Logging and Log4J - For Logging purpose
  * Junit - For test cases


## Platform Compatibility ##
System is developed on Java, hence should be able to run on all platforms. It is tested on Windows environment with Java 6.


## Price ##
It is a free software, however you can contribute
  * By using the software and report back any issue, or any enhancement required
  * By submitting back any enhancements, or documentation done by you
  * By Providing a back link to  http://www.vedantatree.com/ with the reference of RedmineJConnector on your website.
  * By connecting with us on Facebook at https://www.facebook.com/VedantaTree and on Twitter at [@VedantaTree](https://twitter.com/VedantaTree)

Thank  you!


---

### ~A product developed by [VedantaTree](http://www.vedantatree.com) ###
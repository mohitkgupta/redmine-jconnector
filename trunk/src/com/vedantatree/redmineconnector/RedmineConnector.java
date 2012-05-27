package com.vedantatree.redmineconnector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vedantatree.redmineconnector.bdo.Issue;
import com.vedantatree.redmineconnector.bdo.IssuesContainer;
import com.vedantatree.redmineconnector.bdo.Project;
import com.vedantatree.redmineconnector.bdo.ProjectsContainer;
import com.vedantatree.redmineconnector.utils.ConfigurationManager;
import com.vedantatree.redmineconnector.utils.Utilities;


/**
 * RedmineConnector acts as client side interface to interact with Redmine Server. It provides functions to perform
 * various CRUD operations on Redmine Server for different data objects.
 * 
 * <p>
 * RedmineConnector picks the Redmine server address and security key from configuration file.
 * 
 * <p>
 * RedmineConnector is designed using singleton pattern, so that single instance can serve multiple clients and multiple
 * requests.
 * 
 * <p>
 * RedmineConnector uses 'Restlet' API for consuming the Rest Services Interface exposed by Redmine. RedmineConnector
 * uses 'JIBX' API for performing Java to XML and XML to Java transformations.
 * 
 * <p>
 * Note: Redmine Server return Unprocessable Entity - 422 code if any object is already existed, or if passed
 * information is not correct. Problem is that it does not specify the details that what information is missing.
 * 
 * <p>
 * TODO: -Put validations on inputs -Do extensive java documentation
 * 
 * @author Mohit Gupta [mohit.gupta@vedantatree.com]
 */
public class RedmineConnector
{

	private static Log				LOGGER					= LogFactory.getLog( RedmineConnector.class );
	private static String			SECURITY_KEY			= "security.key";
	private static String			REDMINE_SERVER_ADDRESS	= "redmine.server";

	/**
	 * Shared Singleton instance of Redmine Connector
	 */
	private static RedmineConnector	SHARED_INSTANCE			= new RedmineConnector();

	/**
	 * Client for consuming the Redmine Rest services. It is developed using 'Restlet' API
	 */
	private RestletClient			restletClient;

	/**
	 * Object which is used to perform Java to XML and XML to Java transformations. It is developed using 'JIBX' API
	 */
	private JIBXXMLJavaConvertor	xmlJavaConvertor;

	/**
	 * Security Key to access the Redmine. It is read from configuration file.
	 */
	private String					securityKey;

	private RedmineConnector()
	{
		// instantiate restlet client, picking redmine server address from configuration file
		restletClient = new RestletClient( ConfigurationManager.getSharedInstance().getPropertyValue(
				REDMINE_SERVER_ADDRESS ) );

		// instantiate XML 2 Java / Java 2 XML converter
		xmlJavaConvertor = new JIBXXMLJavaConvertor();

		// pick security key from configuration file
		securityKey = ConfigurationManager.getSharedInstance().getPropertyValue( SECURITY_KEY );
	}

	/**
	 * It returns the shared instance of RedmineConnector
	 * 
	 * @return Share Redmine Connector
	 */
	public static RedmineConnector getSharedInstance()
	{
		return SHARED_INSTANCE;
	}

	/**
	 * It is used to create the Redmine Project in Redmine Server.
	 * 
	 * @param newProject New Redmine Project Object to save
	 * @return Newly created Redmine Project Object from Redmine Server
	 * @throws RCException if there is any problem
	 */
	public Project createProject( Project newProject ) throws RCException
	{
		LOGGER.trace( "createProject: newProject[" + newProject + "]" );
		Utilities.assertNotNullArgument( newProject, "newProject" );

		try
		{
			String requestURL = "/projects.xml?key=" + getSecurityKey();
			return (Project) createOrUpdateRedmineObject( requestURL, newProject, true );
		}
		catch( Exception ex )
		{
			throw handleException( ex );
		}
	}

	/**
	 * This method updates a Redmine Project to Redmine Server
	 * 
	 * @param updatedProject Changed Redmine Project to update in Redmine Server
	 * @return Saved Redmine Project from Redmine Server
	 * @throws RCException if there is any problem
	 */
	public void updateProject( Project updatedProject ) throws RCException
	{
		LOGGER.trace( "updateProject: chanegdProject[" + updatedProject + "]" );
		Utilities.assertNotNullArgument( updatedProject, "updatedProject" );

		try
		{
			String requestURL = "/projects/" + updatedProject.getId() + ".xml?key=" + getSecurityKey();
			createOrUpdateRedmineObject( requestURL, updatedProject, false );
		}
		catch( Exception ex )
		{
			throw handleException( ex );
		}
	}

	/**
	 * This method deletes an existing Project from Redmine Server
	 * 
	 * @param projectId Id of the project to delete
	 * @return True if deleted, false otherwise
	 * @throws RCException If there is any problem
	 */
	public boolean deleteProject( long projectId ) throws RCException
	{
		LOGGER.trace( "deleteProject: projectId[" + projectId + "]" );

		try
		{
			String requestURL = "/projects/" + projectId + ".xml?key=" + getSecurityKey();
			return deleteRedmineObject( requestURL );
		}
		catch( Exception ex )
		{
			throw handleException( ex );
		}
	}

	/**
	 * This method fetches the Project Object from Redmine Server for specified project id
	 * 
	 * @param projectId id of the project to fetch
	 * @return Project Object if found
	 * @throws RCException If there is any problem
	 */
	public Project getProjectById( long projectId ) throws RCException
	{
		LOGGER.trace( "getProjectById: projectId[" + projectId + "]" );
		try
		{
			String requestURL = "/projects/" + projectId + ".xml?include=trackers";
			return (Project) getRedmineObject( requestURL, Project.class );
		}
		catch( Exception ex )
		{
			throw handleException( ex );
		}
	}

	/**
	 * This method helps to access all projects exist on Redmine server with the help of an iterator. Please refer to
	 * documentation of 'getIssues()' for more detail.
	 * 
	 * @return Data Paginator which will help to access all projects from Redmine Server
	 * @throws RCException If there is any problem
	 */
	public RedmineDataPaginator getProjects() throws RCException
	{
		LOGGER.trace( "getProjects" );
		try
		{
			return new DefaultDataPaginator( ProjectsContainer.class, "/projects.xml", 0, 50 );
		}
		catch( Exception ex )
		{
			throw handleException( ex );
		}
	}

	/**
	 * It is used to create the Redmine Issue.
	 * 
	 * @param newIssue New Redmine Issue Object to save
	 * @return Newly created Redmine Issue Object from Redmine Server
	 * @throws RCException if there is any problem
	 */
	public Issue createIssue( Issue newIssue ) throws RCException
	{
		LOGGER.trace( "createIssue: newIssue[" + newIssue + "]" );
		Utilities.assertNotNullArgument( newIssue, "newIssue" );
		try
		{
			String requestURL = "/issues.xml?key=" + getSecurityKey();
			return (Issue) createOrUpdateRedmineObject( requestURL, newIssue, true );
		}
		catch( Exception ex )
		{
			throw handleException( ex );
		}
	}

	/**
	 * This method updates a Redmine Issue to Redmine Server
	 * 
	 * @param updatedIssue Updated Redmine Issue
	 * @return Updated Redmine Issue from Redmine Server
	 * @throws RCException if there is any problem
	 */
	public void updateIssue( Issue updatedIssue ) throws RCException
	{
		LOGGER.trace( "updateIssue: updatedIssue[" + updatedIssue + "]" );
		Utilities.assertNotNullArgument( updatedIssue, "updatedIssue" );
		try
		{
			String requestURL = "/issues/" + updatedIssue.getId() + ".xml?key=" + getSecurityKey();
			createOrUpdateRedmineObject( requestURL, updatedIssue, false );
		}
		catch( Exception ex )
		{
			throw handleException( ex );
		}
	}

	/**
	 * This method deletes an existing Issue from Redmine Server
	 * 
	 * @param issueId Id of the issue to delete
	 * @return True if deleted, false otherwise
	 * @throws RCException If there is any problem
	 */
	public boolean deleteIssue( long issueId ) throws RCException
	{
		try
		{
			String requestURL = "/issues/" + issueId + ".xml?key=" + getSecurityKey();
			return deleteRedmineObject( requestURL );
		}
		catch( Exception ex )
		{
			throw handleException( ex );
		}
	}

	/**
	 * This method fetches the Issue object from Redmine Server for specified issue id
	 * 
	 * @param issueId Id of the issue to fetch
	 * @return Issue object if found
	 * @throws RCException if there is any problem
	 */
	public Issue getIssueById( long issueId ) throws RCException
	{
		try
		{
			String requestURL = "/issues/" + issueId + ".xml";
			return (Issue) getRedmineObject( requestURL, Issue.class );
		}
		catch( Exception ex )
		{
			throw handleException( ex );
		}
	}

	/**
	 * This method facilitates to fetch all issues exist in Redmine.
	 * 
	 * Issues can be in hundreds, thousands or more. So to avoid pressure on memory and CPU, this method will return a
	 * Data Paginator. This pagintor API can be used to check whether server has more objects or not, and if it has, its
	 * method can be called to retrieve next page of records. This way, it helps to iterate over the available records
	 * in small chunks and hence helps to avoid memory issues.
	 * 
	 * However, if user wants, she can call 'getAllRecords' method of paginator to access all objects in one call.
	 * Please take a informed decision, considering that it will put a lot of pressure on memory and CPU if number of
	 * objects are big in numbers.
	 * 
	 * Paginator will start from 0th index of records, and will return 50 records with each page.
	 * 
	 * @return A data paginator which will help to iterate over list of issues
	 * @throws RCException If there is any problem
	 */
	public RedmineDataPaginator getIssues() throws RCException
	{
		try
		{
			return new DefaultDataPaginator( IssuesContainer.class, "/issues.xml", 0, 50 );
		}
		catch( Exception ex )
		{
			throw handleException( ex );
		}
	}

	private Object createOrUpdateRedmineObject( String requestURL, Object redmineObject, boolean create )
			throws RCException
	{
		LOGGER.trace( "createOrUpdateRedmineObject: requestURL[" + requestURL + "] redmineObject[" + redmineObject
				+ "] create[" + create + "]" );

		String contents = null;
		if( redmineObject instanceof Issue )
		{
			contents = xmlJavaConvertor.issueXMLToCreateIssue( (Issue) redmineObject );
		}
		else
		{
			contents = xmlJavaConvertor.javaToXML( redmineObject );
		}

		String redmineResponse = create ? restletClient.executePostRequest( requestURL, contents ) : restletClient
				.executePutRequest( requestURL, contents );

		// update does not return any value
		Object newRedmineObject = create ? xmlJavaConvertor.xmlToJava( redmineResponse, redmineObject.getClass() )
				: null;
		LOGGER.debug( "response-object-from-redmine[" + newRedmineObject + "]" );
		return newRedmineObject;
	}

	private boolean deleteRedmineObject( String requestURL ) throws RCException
	{
		LOGGER.trace( "deleteRedmineObject: requestURL[" + requestURL + "]" );

		String redmineResponse = restletClient.executeDeleteRequest( requestURL );
		LOGGER.debug( "response-from-server[" + redmineResponse + "]" );
		// TODO: handle response
		return true;
	}

	Object getRedmineObject( String requestURL, Class objectType ) throws RCException
	{
		LOGGER.trace( "getRedmineObject: requestURL[" + requestURL + "] objectType[" + objectType + "]" );

		String redmineResponse = restletClient.executeGetRequest( requestURL );
		Object redmineResponseObject = xmlJavaConvertor.xmlToJava( redmineResponse, objectType );

		LOGGER.debug( "object-from-server[" + redmineResponseObject + "]" );
		return redmineResponseObject;
	}

	private RCException handleException( Exception ex )
	{
		LOGGER.error( ex );
		if( ex instanceof RCException )
		{
			return (RCException) ex;
		}
		return new RCException( RCException.ILLEGAL_STATE, ex );
	}

	private String getSecurityKey()
	{
		return securityKey;
	}

	public static void main( String[] args ) throws Exception
	{
		ProjectsContainer container = (ProjectsContainer) new RedmineConnector().getProjects();
		System.out.println( "----------------" + container.getRedmineDataObjects() );
	}

}

// ---------------------------------------------------------------------------------------------------------------------

// public static byte OPEARTION_TYPE_CREATE = 0;
// public static byte OPEARTION_TYPE_UPDATE = 1;
// public static byte OPEARTION_TYPE_DELETE = 2;
// public static byte OPEARTION_TYPE_READ = 3;


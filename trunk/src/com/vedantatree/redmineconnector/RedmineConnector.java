package com.vedantatree.redmineconnector;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vedantatree.redmineconnector.bdo.Issue;
import com.vedantatree.redmineconnector.bdo.IssuesContainer;
import com.vedantatree.redmineconnector.bdo.Project;
import com.vedantatree.redmineconnector.bdo.ProjectsContainer;
import com.vedantatree.redmineconnector.bdo.User;
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
	 * 
	 * @deprecated RedmineConnector has been upgraded from Singleton pattern to normal object pattern. Use Constructor
	 *             to create a new instance with parameters.
	 */
	private static RedmineConnector	SHARED_INSTANCE			= new RedmineConnector();

	/**
	 * Component to build the URL for various kind of requests, for different objects
	 */
	private URLBuilder				urlBuilder;

	/**
	 * Client for consuming the Redmine Rest services. It is developed using 'Restlet' API
	 */
	private RestletClient			restletClient;

	/**
	 * Object which is used to perform Java to XML and XML to Java transformations. It is developed using 'JIBX' API
	 */
	private JIBXXMLJavaConvertor	xmlJavaConvertor;

	/**
	 * @deprecated RedmineConnector has been upgraded from Singleton pattern to normal object pattern. Use Constructor
	 *             to create a new instance with parameters.
	 */
	private RedmineConnector()
	{
		// pick server host address from configuration file
		String confServerAddress = ConfigurationManager.getSharedInstance().getPropertyValue( REDMINE_SERVER_ADDRESS );

		// pick security key from configuration file
		String confSecurityKey = ConfigurationManager.getSharedInstance().getPropertyValue( SECURITY_KEY );

		initialize( confServerAddress, confSecurityKey );
	}

	public RedmineConnector( String redmineServerHost, String apiAccessKey )
	{
		Utilities.assertQualifiedString( redmineServerHost, "Redmine Server Host" );
		Utilities.assertQualifiedString( apiAccessKey, "API Access Key" );

		initialize( redmineServerHost, apiAccessKey );
	}

	/**
	 * It returns the shared instance of RedmineConnector
	 * 
	 * @return Share Redmine Connector
	 * 
	 * @deprecated RedmineConnector has been upgraded from Singleton pattern to normal object pattern. Use Constructor
	 *             to create a new instance with parameters. This method can be removed anytime with later releases.
	 */
	public static RedmineConnector getSharedInstance()
	{
		return SHARED_INSTANCE;
	}

	/**
	 * Initialize the RedmineConnector object by creating internal components
	 * 
	 * @param serverHostAddress Address of the server
	 * @param securityKey API Access Key for Redmine
	 */
	private void initialize( String serverHostAddress, String securityKey )
	{
		// instantiate restlet client, picking redmine server address from configuration file
		restletClient = new RestletClient();

		// instantiate XML 2 Java / Java 2 XML converter
		xmlJavaConvertor = new JIBXXMLJavaConvertor();

		// instantiate URL Builder
		urlBuilder = new URLBuilder( serverHostAddress, securityKey );
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
			String requestURL = urlBuilder.buildURLToCreateObject( Project.class );
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
			String requestURL = urlBuilder.buildURLToUpdateObject( Project.class, updatedProject.getId() );
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
			String requestURL = urlBuilder.buildURLToDeleteObject( Project.class, projectId );
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
	 * 
	 * @deprecated since 1.1. Use getProjectById (projectId, includes) instead. This method will be removed with future
	 *             releases.
	 */
	public Project getProjectById( long projectId ) throws RCException
	{
		return getProjectById( projectId, null );
	}

	/**
	 * This method fetches the Project Object from Redmine Server for specified project id
	 * 
	 * @param projectId id of the project to fetch
	 * @param includes Collection of 'include' criteria, based on which sub-objects will be included in project
	 * @return Project Object if found
	 * @throws RCException If there is any problem
	 */
	public Project getProjectById( long projectId, Collection<String> includes ) throws RCException
	{
		LOGGER.trace( "getProjectById: projectId[" + projectId + "]" );

		Utilities.assertNotNullArgument( projectId, "Project Id" );

		try
		{
			String requestURL = urlBuilder.buildURLToGetObjectById( Project.class, projectId, includes );
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
	 * 
	 * @deprecated since 1.1. Use getProjectsIterator() instead. This method will be removed with future versions.
	 */
	public RedmineDataPaginator getProjects() throws RCException
	{
		LOGGER.trace( "getProjects" );
		return getProjectsIterator( 0, 25, null, null );
	}

	/**
	 * This method helps to access all projects exist on Redmine server with the help of an iterator. Please refer to
	 * documentation of 'getIssues()' for more detail.
	 * 
	 * @return Data Paginator which will help to access all projects from Redmine Server
	 * @throws RCException If there is any problem
	 */
	public RedmineDataPaginator getProjectsIterator( long startRecordIndex, int pageSize, Collection<String> includes,
			Map<String, String> filterCritera ) throws RCException
	{
		LOGGER.trace( "getProjects" );

		try
		{
			if( pageSize > RedmineDataPaginator.REDMINE_MAX_PAGE_SIZE )
			{
				throw new RCException( RCException.ILLEGAL_ARGUMENT,
						"Specified Page size is greater than the supported maximum page size by Redmine. supported-Size["
								+ RedmineDataPaginator.REDMINE_MAX_PAGE_SIZE + "]" );
			}

			return new DefaultDataPaginator( ProjectsContainer.class, urlBuilder.buildURLToGetObjectsList(
					Project.class, includes, filterCritera ), startRecordIndex, pageSize );
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
			String requestURL = urlBuilder.buildURLToCreateObject( Issue.class );
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
			String requestURL = urlBuilder.buildURLToUpdateObject( Issue.class, updatedIssue.getId() );
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
			String requestURL = urlBuilder.buildURLToDeleteObject( Issue.class, issueId );
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
	 * 
	 * @deprecated since 1.1. Use getIssueById (projectId, includes) instead. This method will be removed with future
	 *             releases.
	 */
	public Issue getIssueById( long issueId ) throws RCException
	{
		return getIssueById( issueId, null );
	}

	/**
	 * This method fetches the Issue Object from Redmine Server for specified issue id
	 * 
	 * @param issueId id of the Issue to fetch
	 * @param includes Collection of 'include' criteria, based on which sub-objects will be included in Issue
	 * @return Issue Object if found
	 * @throws RCException If there is any problem
	 */
	public Issue getIssueById( long issueId, Collection<String> includes ) throws RCException
	{
		LOGGER.trace( "getIssueById: issueId[" + issueId + "] includes[" + includes + "]" );

		Utilities.assertNotNullArgument( issueId, "Issue Id" );

		try
		{
			String requestURL = urlBuilder.buildURLToGetObjectById( Issue.class, issueId, includes );
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
	 * 
	 * @deprecated since 1.1. Use getIssuesIterator() instead. This method will be removed with future versions.
	 */
	public RedmineDataPaginator getIssues() throws RCException
	{
		return getIssuesIterator( 0, 25, null, null );
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
	 * @param startRecordIndex Index of record start record
	 * @param pageSize size of one page fetched by Iterator in one request to Redmine Server. Iterator will fetch the
	 *        records in pages, to avoid burden on server and client both due to large amount of data in transfer
	 * @param includes Collection of 'include' criteria, based on this, sub-objects will be included with returned Issue
	 *        objects
	 * @param filterCriteria Various possible filter criteria in key<>value form. These will be applied to data set
	 *        while querying the Redmine Data to fetch the records
	 * @return A data paginator which will help to iterate over list of issues
	 * @throws RCException If there is any problem
	 */
	public RedmineDataPaginator getIssuesIterator( long startRecordIndex, int pageSize, Collection<String> includes,
			Map<String, String> filterCritera ) throws RCException
	{
		LOGGER.trace( "getProjects" );

		try
		{
			if( pageSize > RedmineDataPaginator.REDMINE_MAX_PAGE_SIZE )
			{
				throw new RCException( RCException.ILLEGAL_ARGUMENT,
						"Specified Page size is greater than the supported maximum page size by Redmine. supported-Size["
								+ RedmineDataPaginator.REDMINE_MAX_PAGE_SIZE + "]" );
			}

			return new DefaultDataPaginator( IssuesContainer.class, urlBuilder.buildURLToGetObjectsList( Issue.class,
					includes, filterCritera ), startRecordIndex, pageSize );
		}
		catch( Exception ex )
		{
			throw handleException( ex );
		}
	}

	/**
	 * It is used to create the Redmine User in Redmine Server.
	 * 
	 * @param newProject New Redmine User Object to save
	 * @return Newly created Redmine User Object from Redmine Server
	 * @throws RCException if there is any problem
	 * @since 1.1.0
	 */
	public User createUser( User newUser ) throws RCException
	{
		LOGGER.trace( "createUser: newUser[" + newUser + "]" );
		Utilities.assertNotNullArgument( newUser, "newUser" );

		try
		{
			String requestURL = urlBuilder.buildURLToCreateObject( User.class );
			return (User) createOrUpdateRedmineObject( requestURL, newUser, true );
		}
		catch( Exception ex )
		{
			throw handleException( ex );
		}
	}

	/**
	 * This method updates a Redmine User to Redmine Server
	 * 
	 * @param updatedUser Changed Redmine User to update in Redmine Server
	 * @return Saved Redmine User object from Redmine Server
	 * @throws RCException if there is any problem
	 * @since 1.1.0
	 */
	public void updateUser( Project updatedUser ) throws RCException
	{
		LOGGER.trace( "updateUser: chanegdUser[" + updatedUser + "]" );
		Utilities.assertNotNullArgument( updatedUser, "updatedUser" );

		try
		{
			String requestURL = urlBuilder.buildURLToUpdateObject( User.class, updatedUser.getId() );
			createOrUpdateRedmineObject( requestURL, updatedUser, false );
		}
		catch( Exception ex )
		{
			throw handleException( ex );
		}
	}

	/**
	 * This method deletes an existing User from Redmine Server
	 * 
	 * @param userId Id of the User to delete
	 * @return True if deleted, false otherwise
	 * @throws RCException If there is any problem
	 * @since 1.1.0
	 */
	public boolean deleteUser( long userId ) throws RCException
	{
		LOGGER.trace( "deleteUser: userId[" + userId + "]" );

		try
		{
			String requestURL = urlBuilder.buildURLToDeleteObject( User.class, userId );
			return deleteRedmineObject( requestURL );
		}
		catch( Exception ex )
		{
			throw handleException( ex );
		}
	}

	/**
	 * This method fetches the User Object from Redmine Server for specified user id
	 * 
	 * @param userId id of the user to fetch
	 * @param includes Collection of 'include' criteria, based on which sub-objects will be included in User
	 * @return User Object if found
	 * @throws RCException If there is any problem
	 * @since 1.1.0
	 */
	public Project getUserById( long userId, Collection<String> includes ) throws RCException
	{
		LOGGER.trace( "getUserById: userId[" + userId + "]" );

		Utilities.assertNotNullArgument( userId, "User Id" );

		try
		{
			String requestURL = urlBuilder.buildURLToGetObjectById( User.class, userId, includes );
			return (Project) getRedmineObject( requestURL, User.class );
		}
		catch( Exception ex )
		{
			throw handleException( ex );
		}
	}

	/**
	 * This method helps to access all users exist on Redmine server with the help of an iterator. Please refer to
	 * documentation of 'getIssues()' for more detail.
	 * 
	 * @return Data Paginator which will help to access all users from Redmine Server
	 * @throws RCException If there is any problem
	 * @since 1.1.0
	 */
	public RedmineDataPaginator getUsersIterator( long startRecordIndex, int pageSize, Collection<String> includes,
			Map<String, String> filterCritera ) throws RCException
	{
		LOGGER.trace( "getProjects" );

		try
		{
			if( pageSize > RedmineDataPaginator.REDMINE_MAX_PAGE_SIZE )
			{
				throw new RCException( RCException.ILLEGAL_ARGUMENT,
						"Specified Page size is greater than the supported maximum page size by Redmine. supported-Size["
								+ RedmineDataPaginator.REDMINE_MAX_PAGE_SIZE + "]" );
			}

			return new DefaultDataPaginator( ProjectsContainer.class, urlBuilder.buildURLToGetObjectsList(
					User.class, includes, filterCritera ), startRecordIndex, pageSize );
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

	public static void main( String[] args ) throws Exception
	{
		ProjectsContainer container = (ProjectsContainer) new RedmineConnector().getProjects();
		System.out.println( "----------------" + container.getRedmineDataObjects() );
	}

}
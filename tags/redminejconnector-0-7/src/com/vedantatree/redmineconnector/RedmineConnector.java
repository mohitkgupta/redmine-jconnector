package com.vedantatree.redmineconnector;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vedantatree.redmineconnector.bdo.Issue;
import com.vedantatree.redmineconnector.bdo.Priority;
import com.vedantatree.redmineconnector.bdo.Project;
import com.vedantatree.redmineconnector.utils.ConfigurationManager;


/**
 * This object acts as client side delegate to interact with Redmine Server. It provides functions to perform various
 * operations on Redmine Server.
 * 
 * TODO: Get issues list and get projects
 * 
 * Note: Redmine Server return Unprocessable Entity - 422 code if any object is already existed, or if passed
 * information is not correct. Problem is that it does not specify the details that what information is missing or so.
 * 
 * @author Mohit Gupta <mohit.gupta@vedantatree.com>
 */
public class RedmineConnector
{

	private static Log				LOGGER					= LogFactory.getLog( RedmineConnector.class );
	private static String			SECURITY_KEY			= "security.key";
	private static String			REDMINE_SERVER_ADDRESS	= "redmine.server";
	private static RedmineConnector	SHARED_INSTANCE			= new RedmineConnector();

	private RestletClient			restletClient;
	private JIBXXMLJavaConvertor	xmlJavaConvertor;
	private String					securityKey;

	private RedmineConnector()
	{
		// instantiate restlet client, pick redmine server address from configuration file
		restletClient = new RestletClient( ConfigurationManager.getSharedInstance().getPropertyValue(
				REDMINE_SERVER_ADDRESS ) );

		// instantiate XML2Java/Java2XML converter
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
	 * It is used to create the Redmine Issue.
	 * 
	 * @param newIssue New Redmine Issue Object to save
	 * @return Newly created Redmine Issue Object from Redmine Server
	 * @throws RCException if there is any problem
	 */
	public Issue createIssue( Issue newIssue ) throws RCException
	{
		String requestURL = "/issues.xml?key=" + getSecurityKey();
		return (Issue) createOrUpdateRedmineObject( requestURL, newIssue, true );
	}

	/**
	 * This method updates a Redmine Issue to Redmine Server
	 * 
	 * @param changedIssue Updated Redmine Issue
	 * @return Updated Redmine Issue from Redmine Server
	 * @throws RCException if there is any problem
	 */
	public Issue updateIssue( Issue changedIssue ) throws RCException
	{
		String requestURL = "/issues.xml?key=" + getSecurityKey();
		return (Issue) createOrUpdateRedmineObject( requestURL, changedIssue, false );
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
		String requestURL = "/projects.xml?key=" + getSecurityKey();
		return (Project) createOrUpdateRedmineObject( requestURL, newProject, true );
	}

	/**
	 * This method updates a Redmine Project to Redmine Server
	 * 
	 * @param changedProject Changed Redmine Project to update in Redmine Server
	 * @return Saved Redmine Project from Redmine Server
	 * @throws RCException if there is any problem
	 */
	public Project updateProject( Project changedProject ) throws RCException
	{
		String requestURL = "/projects.xml?key=" + getSecurityKey();
		return (Project) createOrUpdateRedmineObject( requestURL, changedProject, false );
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
		Object newRedmineObject = xmlJavaConvertor.xmlToJava( redmineResponse, redmineObject.getClass() );
		LOGGER.debug( "new-created-object[" + newRedmineObject + "]" );
		return newRedmineObject;
	}

	/**
	 * This method deletes an existing Project from Redmine Server
	 * 
	 * @param projectId Id of the project to delete
	 * @return True if deleted, false otherwise
	 * @throws RCException If there is any problem
	 */
	public boolean deleteProject( String projectId ) throws RCException
	{
		LOGGER.trace( "deleteProject: projectId[" + projectId + "]" );
		String requestURL = "/projects/" + projectId + ".xml?key=" + getSecurityKey();
		return deleteRedmineObject( requestURL );
	}

	/**
	 * This method deletes an existing Issue from Redmine Server
	 * 
	 * @param issueId Id of the issue to delete
	 * @return True if deleted, false otherwise
	 * @throws RCException If there is any problem
	 */
	public boolean deleteIssue( String issueId ) throws RCException
	{
		String requestURL = "/issues/" + issueId + ".xml?key=" + getSecurityKey();
		return deleteRedmineObject( requestURL );
	}

	private boolean deleteRedmineObject( String requestURL ) throws RCException
	{
		LOGGER.trace( "deleteRedmineObject: requestURL[" + requestURL + "]" );

		String redmineResponse = restletClient.executeDeleteRequest( requestURL );
		LOGGER.debug( "response-from-server[" + redmineResponse + "]" );
		// TODO: handle response
		return true;
	}

	// TODO should be like paginator or iterator
	public List<Issue> getIssues() throws RCException
	{
		throw new UnsupportedOperationException(
				"This method is not impletemented yet. It will be provided with next release." );
	}

	// TODO should be like paginator or iterator
	public List<Project> getProjects() throws RCException
	{
		throw new UnsupportedOperationException(
				"This method is not impletemented yet. It will be provided with next release." );
	}

	/**
	 * This method fetches the Issue object from Redmine Server for specified issue id
	 * 
	 * @param issueId Id of the issue to fetch
	 * @return Issue object if found
	 * @throws RCException if there is any problem
	 */
	public Issue getIssueById( String issueId ) throws RCException
	{
		String requestURL = "/issues/" + issueId + ".xml";
		return (Issue) getRedmineObject( requestURL, Issue.class );
	}

	/**
	 * This method fetches the Project Object from Redmine Server for specified project id
	 * 
	 * @param projectId id of the project to fetch
	 * @return Project Object if found
	 * @throws RCException If there is any problem
	 */
	public Project getProjectById( String projectId ) throws RCException
	{
		String requestURL = "/projects/" + projectId + ".xml";
		return (Project) getRedmineObject( requestURL, Project.class );
	}

	private Object getRedmineObject( String requestURL, Class objectType ) throws RCException
	{
		LOGGER.trace( "getRedmineObject: requestURL[" + requestURL + "] objectType[" + objectType + "]" );

		String redmineResponse = restletClient.executeGetRequest( requestURL );
		Object redmineResponseObject = xmlJavaConvertor.xmlToJava( redmineResponse, objectType );
		LOGGER.debug( "object-from-server[" + redmineResponseObject + "]" );
		return redmineResponseObject;
	}

	private String getSecurityKey()
	{
		return securityKey;
	}

}

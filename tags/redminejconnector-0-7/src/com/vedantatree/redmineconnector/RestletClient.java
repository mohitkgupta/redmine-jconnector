package com.vedantatree.redmineconnector;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.representation.Representation;

import com.vedantatree.redmineconnector.utils.ConfigurationManager;


/**
 * This object works as client for Rest API. It is specifically designed for consuming the Redmine Rest API. For
 * implementation, it is using Restlet API.
 * 
 * @author Mohit Gupta <mohit.gupta@vedantatree.com>
 */
public class RestletClient
{

	private static Log	LOGGER	= LogFactory.getLog( RestletClient.class );

	/**
	 * Address of the Redmine Server
	 */
	private String		serverAddress;

	/**
	 * Constructor accepting Redmine Server Address
	 * 
	 * @param serverAddress Address of Redmine Server
	 */
	RestletClient( String serverAddress )
	{
		this.serverAddress = serverAddress;
	}

	/**
	 * This method executes the get request for Rest API with given URL. It is generally used to retrieve any existing
	 * information
	 * 
	 * @param URL The URL for getting an information from server. It will be invoked with HTTP GET operation
	 * @return Information returned from Server
	 * @throws RCException Throws Exception if there is any problem
	 */
	public String executeGetRequest( String URL ) throws RCException
	{
		return executeRequest( Method.GET, URL, null );
	}

	/**
	 * This method executes the put request for Rest API with given URL. It is generally used to updated any existing
	 * information on server
	 * 
	 * @param URL The URL for putting an information to server. It will be invoked with HTTP PUT operation
	 * @param contents to send to server
	 * @return Information returned from Server
	 * @throws RCException Throws Exception if there is any problem
	 */
	public String executePutRequest( String URL, String contents ) throws RCException
	{
		return executeRequest( Method.PUT, URL, contents );
	}

	/**
	 * This method executes the post request for Rest API with given URL. It is generally used to create a new
	 * information at server.
	 * 
	 * @param URL The URL for posting an information to server. It will be invoked with HTTP POST operation
	 * @param contents to send to server
	 * @return Information returned from Server
	 * @throws RCException Throws Exception if there is any problem
	 */
	public String executePostRequest( String URL, String contents ) throws RCException
	{
		return executeRequest( Method.POST, URL, contents );
	}

	/**
	 * This method executes the delete request for Rest API with given URL. It is generally used to delete any existing
	 * information from server.
	 * 
	 * @param URL The URL for deleting any information from server. It will be invoked with HTTP DELETE operation
	 * @return Information returned from Server
	 * @throws RCException Throws Exception if there is any problem
	 */
	public String executeDeleteRequest( String URL ) throws RCException
	{
		return executeRequest( Method.DELETE, URL, null );
	}

	private String executeRequest( Method operationMethod, String URL, String contents ) throws RCException
	{
		LOGGER.trace( "executeRequest: operationMethod[" + operationMethod.getName() + "] URL[" + URL + "] host["
				+ getServerAddress() + "]" );

		URL = getServerAddress() + URL;
		LOGGER.debug( "URL-with-host[" + URL + "]" );

		Request request = new Request( operationMethod, URL );

		// set the contents, specifically in case of POST, PUT, and DELETE
		if( contents != null && contents.length() > 0 )
		{
			if( operationMethod == Method.GET || operationMethod == Method.DELETE )
			{
				throw new IllegalStateException( "No content is required to send to server for GET or DELETE request." );
			}
			// Setting entity using String Reader is not working. Probably media type is not going their correct in that
			// case.

			// Representation contentRepresentation = new ReaderRepresentation( new StringReader( contents ),
			// MediaType.TEXT_XML);
			// contentRepresentation.setCharacterSet( CharacterSet.UTF_8 );
			// request.setEntity( contentRepresentation );
			LOGGER.debug( "request-contents[" + contents + "]" );
			request.setEntity( contents, MediaType.TEXT_XML );
		}

		Client client = new Client( Protocol.HTTP );
		Response response = client.handle( request );

		LOGGER.debug( "status[" + response.getStatus() + "] isSuccessCode["
				+ Status.isSuccess( response.getStatus().getCode() ) + "]" );
		handleStatus( response.getStatus() );

		// TODO: handle status, like authorization failure, server not working
		Representation output = response.getEntity();

		// if you call getText on output, it wipes out the contents from output. Hence second call will return null
		String outputText;
		try
		{
			outputText = output.getText();
		}
		catch( IOException e )
		{
			LOGGER.error( e );
			throw new RCException( RCException.IO_ERROR, "Error while getting output response text " );
		}
		LOGGER.debug( "response-RestService[" + outputText + "]" );

		return outputText;
	}

	/**
	 * This method handle the response code from Rest API. Depending upon the response code, it set the right error code
	 * to Exception if there is any problem.
	 * 
	 * @param restRequestStatus Status returned from Server
	 * @throws RCException Throws Exception if response code is not favorable
	 */
	private void handleStatus( Status restRequestStatus ) throws RCException
	{
		int statusCode = restRequestStatus.getCode();
		if( Status.isSuccess( statusCode ) )
		{
			LOGGER.debug( "Success Code received from Redmine Server[" + statusCode + "]" );
			return;
		}
		RCException rce = null;
		if( statusCode == RCException.UNPROCESSABLE_ENTITY )
		{
			rce = new RCException(
					statusCode,
					"Error Status Unprocessable_Entity 422 is returned from Redmine Server. Redmine server returns this when some data is not as per expected format. For example, any specified relational data is not found. Or you have used a Priority ID, which is not there in Redmine Database. Or if any mandatory field is not specified. Or date format is not correct. Or date data is not correct" );
		}
		else
		{
			rce = new RCException( statusCode, "Error Status return from Redmine Server. status[" + restRequestStatus
					+ "] statusCode[" + statusCode + "]" );
		}
		LOGGER.error( rce );
		throw rce;
	}

	private String getServerAddress()
	{
		return serverAddress;
	}

	public static void main( String[] args ) throws Exception
	{
		RestletClient rc = new RestletClient( ConfigurationManager.getSharedInstance().getPropertyValue(
				"redmine.server" ) );
		rc.executeGetRequest( "/issues.xml" );
	}

}

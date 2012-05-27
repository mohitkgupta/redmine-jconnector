package com.vedantatree.redmineconnector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vedantatree.redmineconnector.bdo.Error;
import com.vedantatree.redmineconnector.bdo.ErrorsContainer;
import com.vedantatree.redmineconnector.utils.Utilities;


/**
 * This object represents the Exception state when it is thrown from any operation. It is a customized Exception made to
 * accomodate some custom information like error code etc.
 * 
 * @author Mohit Gupta <mohit.gupta@vedantatree.com>
 */

public class RCException extends Exception
{

	public static int											IO_ERROR				= 0;
	public static int											RESOURCE_NOT_FOUND		= 1;
	public static int											DATA_CONVERSION_ERROR	= 2;
	public static int											ILLEGAL_ARGUMENT		= 3;
	public static int											ILLEGAL_STATE			= 4;
	public static int											OBJECT_NOT_FOUND		= 404;
	public static int											UNPROCESSABLE_ENTITY	= 422;

	/**
	 * The code represents the specified error state
	 */
	private int													errorCode;

	private String												responseXML;
	private List<com.vedantatree.redmineconnector.bdo.Error>	errors;

	public RCException( int errorCode, String message )
	{
		super( message );
		this.errorCode = errorCode;
	}

	public RCException( int errorCode, String message, String responseXML )
	{
		super( message );
		this.errorCode = errorCode;
		this.responseXML = responseXML;
	}

	public RCException( int errorCode, Throwable th )
	{
		super( th );
		this.errorCode = errorCode;
	}

	public RCException( int errorCode, String message, Throwable th )
	{
		super( message, th );
		this.errorCode = errorCode;
	}

	public int getErrorCode()
	{
		return errorCode;
	}

	public String getResponseXML()
	{
		return responseXML;
	}

	public List<com.vedantatree.redmineconnector.bdo.Error> getErrors() throws RCException
	{
		if( errors == null )
		{
			if( Utilities.isQualifiedString( this.responseXML ) )
			{
				ErrorsContainer errorsContainer = (ErrorsContainer) new JIBXXMLJavaConvertor().xmlToJava(
						this.responseXML, ErrorsContainer.class );
				errors = errorsContainer.getErrors();
			}
			// to avoid retry for creating errors from xml
			if( errors == null )
			{
				errors = new ArrayList<com.vedantatree.redmineconnector.bdo.Error>();
			}
		}
		return errors;
	}

	public String getMessage()
	{
		StringBuffer sb = new StringBuffer( super.getMessage() );
		sb.append( "--errorCode[" );
		sb.append( errorCode );
		if( errors != null && errors.size() > 0 )
		{
			sb.append( "] errors[" );
			for( Iterator<Error> iterator = errors.iterator(); iterator.hasNext(); )
			{
				com.vedantatree.redmineconnector.bdo.Error type = (com.vedantatree.redmineconnector.bdo.Error) iterator
						.next();
				sb.append( type.getDescription() );
				sb.append( " :: " );
			}
		}
		else if( Utilities.isQualifiedString( responseXML ) )
		{
			sb.append( "] " );
			sb.append( responseXML );
			sb.append( "]" );
		}
		sb.append( "]" );
		return sb.toString();
	}

}

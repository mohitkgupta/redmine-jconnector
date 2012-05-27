package com.vedantatree.redmineconnector.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.vedantatree.redmineconnector.RCRuntimeException;
import com.vedantatree.redmineconnector.bdo.Issue;
import com.vedantatree.redmineconnector.bdo.Priority;
import com.vedantatree.redmineconnector.bdo.Project;
import com.vedantatree.redmineconnector.bdo.Status;
import com.vedantatree.redmineconnector.bdo.Tracker;
import com.vedantatree.redmineconnector.bdo.User;


/**
 * This class provides various Utility methods
 * 
 * @author Mohit Gupta <mohit.gupta@vedantatree.com>
 */

public class Utilities
{

	public static void assertNotNullArgument( Object obj, String argName )
	{
		if( obj == null )
		{
			throw new IllegalArgumentException( "Null Object specified as argument. argumentName[" + argName + "]" );
		}
		if( obj instanceof String )
		{
			assertQualifiedString( (String) obj, argName );
		}
	}

	public static void assertQualifiedString( String str, String argName )
	{
		if( !isQualifiedString( str ) )
		{
			throw new IllegalArgumentException( "Null or zero length string found. str[" + str + "] argumentName["
					+ argName + "]" );
		}
	}

	/**
	 * This method will check for the null string
	 * 
	 * @param str to be check
	 * @return true if string is not null
	 */
	public static boolean isQualifiedString( String str )
	{
		return str != null && str.trim().length() > 0;
	}

	public static List<String> getTokenizedString( String strToTokenize, String delimiter )
	{
		StringTokenizer st = new StringTokenizer( strToTokenize, delimiter );
		if( st.countTokens() <= 0 )
		{
			return null;
		}
		List<String> tokens = new ArrayList<String>( st.countTokens() );
		while( st.hasMoreTokens() )
		{
			tokens.add( st.nextToken() );
		}
		return tokens;
	}

	// TODO this method can be used later for creating URL for all restlet requests
	public static String prepareRedmineRequestURL( Class objectType, byte operationType, String id,
			Map<String, Object> parameters )
	{
		StringBuffer requestURL = new StringBuffer();
		String objectURL = null;
		if( objectType == Project.class )
		{
			objectURL = "projects";
		}
		else if( objectType == Issue.class )
		{
			objectURL = "issues";
		}
		else if( objectType == Priority.class )
		{
			objectURL = "priorities";
		}
		else if( objectType == Status.class )
		{
			objectURL = "statuses";
		}
		else if( objectType == Tracker.class )
		{
			objectURL = "trackers";
		}
		else if( objectType == User.class )
		{
			objectURL = "users";
		}
		else
		{
			RCRuntimeException rcrException = new RCRuntimeException( RCRuntimeException.ILLEGAL_STATE,
					"Prepare-request-url does not recognized specified object type. type[" + objectType + "]" );
			throw rcrException;
		}

		// if (operationType == RedmineConnector.OPEARTION_TYPE_CREATE || operationType ==
		// RedmineConnector.OPEARTION_TYPE_UPDATE)
		// {
		//			
		// }

		return null;
	}

}

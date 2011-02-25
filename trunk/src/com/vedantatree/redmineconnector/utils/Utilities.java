package com.vedantatree.redmineconnector.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


/**
 * This class provides various Utility methods
 * 
 * @author Mohit Gupta <mohit.gupta@vedantatree.com>
 */

public class Utilities
{

	public static void assertNotNullArgument( Object obj )
	{
		if( obj == null )
		{
			throw new IllegalArgumentException( "Null Object specified as argument" );
		}
		if( obj instanceof String )
		{
			assertQualifiedString( (String) obj );
		}
	}

	public static void assertQualifiedString( String str )
	{
		if( !isQualifiedString( str ) )
		{
			throw new IllegalArgumentException( "Null or zero length string found. str[" + str + "]" );
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

}

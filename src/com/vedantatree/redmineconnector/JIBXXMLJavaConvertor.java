package com.vedantatree.redmineconnector;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.Utility;

import com.vedantatree.redmineconnector.bdo.Issue;
import com.vedantatree.redmineconnector.bdo.Project;
import com.vedantatree.redmineconnector.bdo.User;
import com.vedantatree.redmineconnector.utils.Utilities;


/**
 * This object is used to convert the Java Object to XML format and vice versa. For implementation, it is using JIBX
 * library. The mapping for conversion can be found in 'binding.xml' file
 * 
 * @author Mohit Gupta <mohit.gupta@vedantatree.com>
 */

public class JIBXXMLJavaConvertor
{

	private static Log				LOGGER				= LogFactory.getLog( JIBXXMLJavaConvertor.class );
	private static SimpleDateFormat	dateFormat_simple	= new SimpleDateFormat( "yyyy-MM-dd" );
	private static SimpleDateFormat	dateFormat_Redmine	= new SimpleDateFormat( "EEE MMM dd HH:mm:ss Z yyyy" );

	JIBXXMLJavaConvertor()
	{
	}

	/**
	 * This method converts the specified XML to Issue object. It is assumed that specified XML will be for Issue Object
	 * only, otherwise there will be an exception.
	 * 
	 * @param XML It is the XML representation of Issue Object
	 * @return Converted Issue Object
	 * @throws RCException Throws error if there is any problem during conversion
	 */
	public Issue getIssueFromXML( String XML ) throws RCException
	{
		return (Issue) xmlToJava( XML, Issue.class );
	}

	/**
	 * This method converts the specified XML to List of Issue objects. It is assumed that specified XML will be for
	 * Issue Objects only, otherwise there will be an exception.
	 * 
	 * @param XML It is the XML representation of collection of Issue Object
	 * @return List of converted Issue Object
	 * @throws RCException Throws error if there is any problem during conversion
	 */
	public List<Issue> getIssuesFromXML( String XML ) throws RCException
	{
		return (List<Issue>) xmlToJava( XML, Issue.class );
	}

	/**
	 * This method converts the specified XML to Project object. It is assumed that specified XML will be for Project
	 * Object only, otherwise there will be an exception.
	 * 
	 * @param XML It is the XML representation of Project Object
	 * @return Converted Project Object
	 * @throws RCException Throws error if there is any problem during conversion
	 */
	public Project getProjectFromXML( String XML ) throws RCException
	{
		return (Project) xmlToJava( XML, Project.class );
	}

	/**
	 * This method converts the specified XML to List of Project objects. It is assumed that specified XML will be for
	 * Project Objects only, otherwise there will be an exception.
	 * 
	 * @param XML It is the XML representation of collection of Project Object
	 * @return List of converted Project Object
	 * @throws RCException Throws error if there is any problem during conversion
	 */
	public List<Project> getProjectsFromXML( String XML ) throws RCException
	{
		return (List<Project>) xmlToJava( XML, Project.class );
	}

	/**
	 * This method converts the given XML to object of specified class.
	 * 
	 * @param XML It is the XML representation of object/s
	 * @param clazz Type of object/s
	 * @return Converted Object/s from XML
	 * @throws RCException Throw Exception if there is any problem during conversion
	 */
	public Object xmlToJava( String XML, Class clazz ) throws RCException
	{
		LOGGER.trace( "xmlToJava: XMLLength[" + ( XML != null ? XML.length() : -1 ) + "] clazz[" + clazz + "]" );
		try
		{
			// note that you can use multiple bindings with the same class, in
			// which case you need to use the getFactory() call that takes the
			// binding name as the first parameter

			// TODO: need to handle create issue case using a different binding name. XML to create the issue is
			// different
			IBindingFactory bfact = BindingDirectory.getFactory( clazz );

			// unmarshal customer information from file
			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();

			StringReader sr = new StringReader( XML );
			Object convertedObject = uctx.unmarshalDocument( sr, null );
			LOGGER.debug( "XML-Java-Object[" + convertedObject + "]" );
			return convertedObject;
		}
		catch( JiBXException e )
		{
			RCException ex = new RCException( RCException.DATA_CONVERSION_ERROR,
					"Problem while converting XML data from Redmine Server to Java Object.", e );
			LOGGER.error( ex );
			throw ex;
		}
	}

	/**
	 * This method converts the given Java object to corresponding XML format. The format has been specified with
	 * binding.xml file.
	 * 
	 * @param javaObject It is the Object to convert to XML
	 * @return Object in XML format
	 * @throws RCException Throw Exception if there is any problem during conversion
	 */
	public String javaToXML( Object javaObject ) throws RCException
	{
		LOGGER.trace( "javaToXML: javaObject[" + javaObject + "]" );
		try
		{

			// note that you can use multiple bindings with the same class, in
			// which case you need to use the getFactory() call that takes the
			// binding name as the first parameter
			IBindingFactory bfact = BindingDirectory.getFactory( javaObject.getClass() );

			// marshal object back out to file (with nice indentation, as UTF-8)
			IMarshallingContext mctx = bfact.createMarshallingContext();
			mctx.setIndent( 2 );
			StringWriter sw = new StringWriter();
			mctx.marshalDocument( javaObject, "UTF-8", null, sw );
			String objectXML = sw.toString();
			LOGGER.debug( "objectXML[" + objectXML + "]" );
			return objectXML;
		}
		catch( JiBXException e )
		{
			RCException ex = new RCException( RCException.DATA_CONVERSION_ERROR,
					"Problem while converting Java Objects from Redmine Server to XML Data Format.", e );
			LOGGER.error( ex );
			throw ex;
		}
	}

	/**
	 * This method creates the XML representation for Issue Object. We need to write a separate method from JavaToXML
	 * because Redmine accepts a different format for Issue XML while creating/updating the Object. We shall create a
	 * separate mapping for this case later.
	 * 
	 * @param issue Issue Object to convert to XML format
	 * @return XML representation of specified issue object
	 */
	public String issueXMLToCreateIssue( Issue issue )
	{
		LOGGER.trace( "issueXMLToCreateIssue: issue[" + issue + "]" );

		Utilities.assertNotNullArgument( issue );
		List<String> errors = issue.validate( null );
		if( errors != null && errors.size() > 0 )
		{
			IllegalArgumentException iae = new IllegalArgumentException(
					"Specified Issue does not have all required properties set. Error Details: " + errors );
			LOGGER.error( iae );
			throw iae;
		}

		StringBuffer issueXML = new StringBuffer( "<issue>" );
		issueXML = issueXML.append( "<project_id>" ).append( issue.getProject().getId() ).append( "</project_id>" );
		if( issue.getParent() != null )
		{
			issueXML = issueXML.append( "<parent_issue_id>" ).append( issue.getParent().getId() ).append(
					"</parent_issue_id>" );
		}
		issueXML = issueXML.append( "<subject>" ).append( issue.getSubject() ).append( "</subject>" );
		if( issue.getTracker() != null )
		{
			issueXML = issueXML.append( "<tracker_id>" ).append( issue.getTracker().getId() ).append( "</tracker_id>" );
		}
		if( issue.getPriority() != null )
		{
			issueXML = issueXML.append( "<priority_id>" ).append( issue.getPriority().getId() ).append(
					"</priority_id>" );
		}

		if( issue.getStartDate() != null )
		{
			issueXML = issueXML.append( "<start_date>" ).append( dateFormat_simple.format( issue.getStartDate() ) )
					.append( "</start_date>" );
		}
		if( issue.getDueDate() != null )
		{
			issueXML = issueXML.append( "<due_date>" ).append( dateFormat_simple.format( issue.getDueDate() ) ).append(
					"</due_date>" );
		}

		User user = issue.getAssignedTo();
		if( user != null )
		{
			issueXML = issueXML.append( "<assigned_to_id>" ).append( user.getId() ).append( "</assigned_to_id>" );
		}
		if( issue.getEstimatedEfforts() != null )
		{
			issueXML = issueXML.append( "<estimated_hours>" ).append( issue.getEstimatedEfforts() ).append(
					"</estimated_hours>" );
		}
		if( issue.getSpentEfforts() != null && issue.getSpentEfforts().floatValue() != 0.0f )
		{
			issueXML = issueXML.append( "<spent_hours>" ).append( issue.getSpentEfforts() ).append( "</spent_hours>" );
		}
		if( issue.getPercentageDone() != null )
		{
			issueXML = issueXML.append( "<done_ratio>" ).append( issue.getPercentageDone() ).append( "</done_ratio>" );
		}
		if( Utilities.isQualifiedString( issue.getDescription() ) )
		{
			issueXML = issueXML.append( "<description>" ).append( issue.getDescription() ).append( "</description>" );
		}
		issueXML = issueXML.append( "</issue>" );

		LOGGER.debug( "issue-xml[" + issueXML.toString() + "]" );

		return issueXML.toString();

	}

	/**
	 * This method is used by JIBX to serialize the float value. A custom implementation has been provided to handle the
	 * case of null object.
	 * 
	 * @param floatValue float value to serialize
	 * @return String representation of float value
	 * @throws JiBXException Throws error if there is any problem during conversion
	 */
	public static String serializeFloat( Float floatValue ) throws JiBXException
	{
		if( floatValue == null )
		{
			return "";
		}
		return Utility.serializeFloat( floatValue );
	}

	/**
	 * This method is used by JIBX to de-serialize the float value. A custom implementation has been provided to handle
	 * the case of null object or empty string.
	 * 
	 * @param text float value to de-serialize
	 * @return Float converted float value
	 * @throws JiBXException Throws error if there is any problem during conversion
	 */
	public static Float deserializeFloat( String text ) throws JiBXException
	{
		if( !Utilities.isQualifiedString( text ) )
		{
			return null;
		}
		return Utility.parseFloat( text );
	}

	/**
	 * This method is used by JIBX to serialize the date type value. A custom implementation has been provided to handle
	 * the case of null object.
	 * 
	 * @param dateValue date value to serialize
	 * @return String representation of date value
	 * @throws JiBXException Throws error if there is any problem during conversion
	 */
	public static String serializeDate( Date dateValue ) throws JiBXException
	{
		if( dateValue == null )
		{
			return "";
		}
		return Utility.serializeDate( dateValue );
	}

	/**
	 * This method is used by JIBX to de-serialize the date value. A custom implementation has been provided to handle
	 * the case of null object or empty string and to handle the special date format sent by Redmine.
	 * 
	 * @param text date value to de-serialize
	 * @return Date converted Date value
	 * @throws JiBXException Throws error if there is any problem during conversion
	 */
	public static Date deserializeDate( String text ) throws JiBXException
	{
		LOGGER.trace( "deserializeDate: text[" + text + "]" );

		if( !Utilities.isQualifiedString( text ) )
		{
			LOGGER.debug( "Returning Null as date text is null" );
			return null;
		}

		// EEE, d MMM yyyy HH:mm:ss Z >> Wed, 4 Jul 2001 12:08:56 -0700
		// Format for createdOn and updatedOn >> Sun Feb 13 17:59:24 +0200 2011

		try
		{
			LOGGER.debug( "trying to parse date with format[yyyy-mm-dd]" );
			return dateFormat_simple.parse( text );
		}
		catch( ParseException e )
		{
			try
			{
				LOGGER.debug( "trying to parse date with format[EEE MMM dd HH:mm:ss Z YYYY]" );
				return dateFormat_Redmine.parse( text );
			}
			catch( ParseException e1 )
			{
				LOGGER.debug( "Passing call to JiBX for parsing" );
				return Utility.deserializeDate( text );
			}
		}
	}

}

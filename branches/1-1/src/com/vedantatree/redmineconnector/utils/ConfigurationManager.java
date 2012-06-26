package com.vedantatree.redmineconnector.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vedantatree.redmineconnector.RCException;


/**
 * This object is used to load the configurations from property files and to provide these to the application.
 * 
 * @author Mohit Gupta <mohit.gupta@vedantatree.com>
 */

public class ConfigurationManager
{

	private static Log							LOGGER			= LogFactory.getLog( ConfigurationManager.class );
	private static final ConfigurationManager	sharedInstance	= new ConfigurationManager();

	/**
	 * Properties Store
	 */
	private Properties							properties		= new Properties();

	/**
	 * Indicate whether the configuration manager is initialized or not. It is initialized when the properties are
	 * loaded
	 */
	private boolean								initialized;

	private ConfigurationManager()
	{
		try
		{
			ensurePropertiesLoaded( "redmine-connector.properties" );
		}
		catch( RCException rce )
		{
			throw new IllegalStateException(
					"Problem occured while loading the Redmine Connector property file. file[rc.properties]", rce );
		}
	}

	public static ConfigurationManager getSharedInstance()
	{
		return sharedInstance;
	}

	private void ensurePropertiesLoaded( String configFilesInfo ) throws RCException
	{

		LOGGER.trace( "ensurePropertiesLoaded: configFilesInfo[" + configFilesInfo + "]" );
		try
		{
			String configFileNames = "" + configFilesInfo;
			List<String> fileNamesEntries = Utilities.getTokenizedString( configFileNames, "," );
			if( fileNamesEntries == null || fileNamesEntries.size() == 0 )
			{
				throw new RCException( RCException.ILLEGAL_ARGUMENT,
						"No configuration file has been mentioned while loading the properties" );
			}

			String configFileEntry;
			InputStream is = null;

			for( Iterator iter = fileNamesEntries.iterator(); iter.hasNext(); )
			{
				configFileEntry = (String) iter.next();

				System.out.println( "loading[" + configFileEntry + "]" );
				is = loadConfigurationFile( configFileEntry );

				if( is == null )
				{
					LOGGER.debug( "No resource found for config file > " + configFileEntry );
					continue;
				}

				initialized = true;
				properties.load( is );
				LOGGER.debug( "loaded[" + configFileEntry + "]" );
			}

		}
		catch( IOException ioe )
		{
			RCException ae = new RCException( RCException.IO_ERROR, "Problem while loading config file", ioe );
			throw ae;
		}

		LOGGER.debug( "loaded-Properties[" + properties + "]" );
	}

	private InputStream loadConfigurationFile( String configFileName ) throws RCException
	{
		InputStream is = null;

		try
		{
			is = new FileInputStream( configFileName + "" );
		}
		catch( FileNotFoundException e )
		{
			URL url = Thread.currentThread().getContextClassLoader().getResource( "" );
			if( url != null )
			{
				String path = url.getPath() + configFileName;
				path = path.substring( 1 );
				try
				{
					is = new FileInputStream( path );
				}
				catch( FileNotFoundException fe )
				{
					LOGGER.debug( "File not found. path[" + path + "]" );
				}
			}
		}
		if( is == null )
		{
			LOGGER.error( "Configuraiton file has not been found. configFile[" + configFileName + "]" );
			throw new RCException( RCException.RESOURCE_NOT_FOUND, "Configuraiton file has not been found. configFile["
					+ configFileName + "]" );
		}
		return is;
	}

	/**
	 * It checks whether the specified property value has been existed with Configuration Manager or not
	 * 
	 * @param propertyName Name of the property to check
	 * @return true if exist, false otherwise
	 */
	public boolean containsProperty( String propertyName )
	{
		return getPropertyValueInternal( propertyName, false ) != null;
	}

	/**
	 * Returns the value for the given property value.
	 * 
	 * @param propertyName
	 * @return propertyValue
	 */
	public String getPropertyValue( String propertyName )
	{
		return getPropertyValueInternal( propertyName, true );
	}

	private String getPropertyValueInternal( String propertyName, boolean assertExist )
	{
		if( !initialized && assertExist )
		{
			throw new IllegalStateException( "Please first initialize the Configuration Manager." );
		}

		if( !properties.containsKey( propertyName ) && assertExist )
		{
			throw new IllegalStateException( "No Property has been found for given name. propertyName[" + propertyName
					+ "]" );
		}

		return properties.getProperty( propertyName );
	}

	public static void main( String[] args )
	{
		String propertyValue = ConfigurationManager.getSharedInstance().getPropertyValue( "redmine.server" );
		System.out.println( propertyValue );
	}

}

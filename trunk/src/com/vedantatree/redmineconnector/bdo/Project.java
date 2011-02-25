package com.vedantatree.redmineconnector.bdo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vedantatree.redmineconnector.utils.Utilities;


/**
 * 
 * @author Mohit Gupta <mohit.gupta@vedantatree.com>
 */

public class Project
{

	private static Log			LOGGER	= LogFactory.getLog( Project.class );

	private Long				id;
	private String				name;
	private String				identifier;
	private String				description;
	private String				homePage;
	private Date				createdOn;
	private Date				updatedOn;

	private ArrayList<Tracker>	trackers;

	public Long getId()
	{
		return id;
	}

	public void setId( Long id )
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription( String description )
	{
		this.description = description;
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier( String identifier )
	{
		this.identifier = identifier;
	}

	public String getHomePage()
	{
		return homePage;
	}

	public void setHomePage( String homePage )
	{
		this.homePage = homePage;
	}

	public Date getCreatedOn()
	{
		return createdOn;
	}

	public void setCreatedOn( Date createdOn )
	{
		this.createdOn = createdOn;
	}

	public Date getUpdatedOn()
	{
		return updatedOn;
	}

	public void setUpdatedOn( Date updatedOn )
	{
		this.updatedOn = updatedOn;
	}

	public ArrayList<Tracker> getTrackers()
	{
		return trackers;
	}

	public void setTrackers( ArrayList<Tracker> trackers )
	{
		this.trackers = trackers;
	}

	public List<String> validate( List<String> errors )
	{
		if( errors == null )
		{
			errors = new ArrayList<String>();
		}
		if( getId() == null )
		{
			errors.add( "Project Id is null" );
		}
		if( !Utilities.isQualifiedString( getName() ) )
		{
			errors.add( "Project Name is null." );
		}
		if( !Utilities.isQualifiedString( getIdentifier() ) )
		{
			errors.add( "Project Identifier is null or empty string." );
		}
		// description should not be mandatory
		// if( !Utilities.isQualifiedString( getDescription() ) )
		// {
		// errors.add( "Project Description is null or empty string." );
		// }
		return errors;
	}

	public String toString()
	{
		if( LOGGER.isDebugEnabled() )
		{
			return "Project@" + hashCode() + ": id[" + id + "] name[" + name + "] identifier[" + identifier
					+ "] homePage[" + homePage + "] createdOn[" + createdOn + "] updatedOn[" + updatedOn
					+ "] trackers[" + ( trackers == null ? -1 : trackers.size() ) + "] description[" + description
					+ "]";
		}
		return "Project@" + hashCode() + ": id[" + id + "]";
	}
}

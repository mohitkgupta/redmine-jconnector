package com.vedantatree.redmineconnector.bdo;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author Mohit Gupta <mohit.gupta@vedantatree.com>
 */

public class Status
{

	/**
	 * Various values for Status. These all are corresponding to Default values in Redmine. If anyone has created new
	 * objects, then these values may vary
	 * */
	public static Status	NEW			= new Status( new Long( 1 ) );
	public static Status	IN_PROGRESS	= new Status( new Long( 2 ) );
	public static Status	RESOLVED	= new Status( new Long( 3 ) );
	public static Status	FEEDBACK	= new Status( new Long( 4 ) );
	public static Status	CLOSED		= new Status( new Long( 5 ) );
	public static Status	REJECTED	= new Status( new Long( 6 ) );
	public static Status	OBSOLETED	= new Status( new Long( 7 ) );

	private Long			id;
	private String			name;

	public Status()
	{

	}

	public Status( Long id )
	{
		this.id = id;
	}

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

	public List<String> validate( List<String> errors )
	{
		if( errors == null )
		{
			errors = new ArrayList<String>();
		}
		if( getId() == null )
		{
			errors.add( "Status's Id is null" );
		}
		return errors;
	}

}

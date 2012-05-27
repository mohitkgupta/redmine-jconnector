package com.vedantatree.redmineconnector.bdo;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author Mohit Gupta <mohit.gupta@vedantatree.com>
 */

public class Priority
{

	public static Priority	LOW			= new Priority( new Long( 3 ) );
	public static Priority	NORMAL		= new Priority( new Long( 4 ) );
	public static Priority	HIGH		= new Priority( new Long( 5 ) );
	public static Priority	URGENT		= new Priority( new Long( 6 ) );
	public static Priority	IMMEDIATE	= new Priority( new Long( 7 ) );

	private Long			id;
	private String			name;

	public Priority()
	{
	}

	public Priority( Long id )
	{
		setId( id );
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
			errors.add( "Priority's Id is null" );
		}
		return errors;
	}
}

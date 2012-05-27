package com.vedantatree.redmineconnector.bdo;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author Mohit Gupta <mohit.gupta@vedantatree.com>
 */

public class Tracker
{

	/**
	 * Various values for Tracker. These all are corresponding to Default values in Redmine. If anyone has created new
	 * objects, then these values may vary
	 * */
	public static Tracker	BUG		= new Tracker( new Long( 1 ) );
	public static Tracker	FEATURE	= new Tracker( new Long( 2 ) );
	public static Tracker	SUPPORT	= new Tracker( new Long( 3 ) );

	public Tracker()
	{
	}

	public Tracker( Long id )
	{
		setId( id );
	}

	private Long	id;
	private String	name;

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
			errors.add( "Tracker's Id is null" );
		}
		return errors;
	}

}

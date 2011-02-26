package com.vedantatree.redmineconnector.bdo;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author Mohit Gupta <mohit.gupta@vedantatree.com>
 */

public class User
{

	/**
	 * Default value for User. It is corresponding to Default value in Redmine. If anyone has created new objects, then
	 * these values may vary
	 * */
	public static User	REDMINE_ADMIN	= new User( new Long( 1 ) );

	private Long		id;
	private String		name;

	public User()
	{
	}

	public User( Long id )
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
			errors.add( "User's Id is null" );
		}
		return errors;
	}

	public String toString()
	{
		return "User@" + hashCode() + ": id[" + id + "] name[" + name + "]";
	}
}

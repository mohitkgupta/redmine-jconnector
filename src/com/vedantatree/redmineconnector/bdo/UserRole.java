package com.vedantatree.redmineconnector.bdo;

import java.util.ArrayList;
import java.util.List;

import com.vedantatree.redmineconnector.utils.Utilities;


public class UserRole extends RedmineBDO
{

	private String	name;

	private Long	id;

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
			errors.add( "Id is null" );
		}
		if( Utilities.isQualifiedString( getName() ) )
		{
			errors.add( "Name is not set" );
		}
		return errors;
	}

	public String toString()
	{
		return "UserRole@" + hashCode() + ": id[" + getId() + "] name[" + name + "]";
	}

}

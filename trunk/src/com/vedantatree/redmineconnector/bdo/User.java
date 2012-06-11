package com.vedantatree.redmineconnector.bdo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vedantatree.redmineconnector.utils.Utilities;


/**
 * 
 * @author Mohit Gupta <mohit.gupta@vedantatree.com>
 */

/*
 * POST
Creates a user.

Parameters:

user (required): a hash of the user attributes, including:
login (required): the user login
password: the user password
firstname (required)
lastname (required)
mail (required)
auth_source_id: authentication mode id
Example:

POST /users.xml

<?xml version="1.0" encoding="ISO-8859-1" ?>
<user>
  <login>jplang</login>
  <firstname>Jean-Philippe</firstname>
  <lastname>Lang</lastname>
  <password>secret</password>
  <mail>jp_lang@yahoo.fr</mail>
  <auth_source_id>2</auth_source_id>
</user>
Response:

201 Created: user was created
422 Unprocessable Entity: user was not created due to validation failures (response body contains the error messages)
 * 
 * TODO: What is auth_source_id?????
 */

public class User extends RedmineBDO
{

	public static String					INCLUDE_MEMBERSHIPS	= "memberships";
	public static String					INCLUDE_GROUPS		= "groups";

	private String							name;

	private String							login;

	// Security Concern: Password should not be set while retrieving the object. It should be used only to create the user.
	private String							password;
	private String							firstName;
	private String							lastName;
	private String							email;
	private Date							createdOn;
	private Date							lastLoginOn;
	private ArrayList<ProjectMembership>	projectMemberships;
	private ArrayList<UserGroup>			userGroups;

	public User()
	{
	}

	public User( Long id )
	{
		super.setId( id );
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public String getLogin()
	{
		return login;
	}

	public void setLogin( String login )
	{
		this.login = login;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword( String password )
	{
		this.password = password;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName( String firstName )
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName( String lastName )
	{
		this.lastName = lastName;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail( String email )
	{
		this.email = email;
	}

	public Date getCreatedOn()
	{
		return createdOn;
	}

	public void setCreatedOn( Date createdOn )
	{
		this.createdOn = createdOn;
	}

	public Date getLastLoginOn()
	{
		return lastLoginOn;
	}

	public void setLastLoginOn( Date lastLoginOn )
	{
		this.lastLoginOn = lastLoginOn;
	}

	public ArrayList<ProjectMembership> getProjectMemberships()
	{
		return projectMemberships;
	}

	public void setProjectMemberships( ArrayList<ProjectMembership> projectMemberships )
	{
		this.projectMemberships = projectMemberships;
	}

	public ArrayList<UserGroup> getUserGroups()
	{
		return userGroups;
	}

	public void setUserGroups( ArrayList<UserGroup> userGroups )
	{
		this.userGroups = userGroups;
	}

	public String getFullName()
	{
		if( Utilities.isQualifiedString( firstName ) )
		{
			return firstName + " " + lastName;
		}
		return name;
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
		if (!Utilities.isQualifiedString( firstName ))
		{
			errors.add( "First name is not set" );
		}
		if (!Utilities.isQualifiedString( lastName ))
		{
			errors.add( "Last name is not set" );
		}
		// TODO: validate email format, add util method
		if (!Utilities.isQualifiedString( email ))
		{
			errors.add( "Email is not set" );
		}
		return errors;
	}

	public String toString()
	{
		return "User@" + hashCode() + ": id[" + getId() + "] name[" + getFullName() + "]";
	}
}

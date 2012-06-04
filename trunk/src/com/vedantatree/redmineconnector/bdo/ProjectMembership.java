package com.vedantatree.redmineconnector.bdo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ProjectMembership
{

	private static Log			LOGGER	= LogFactory.getLog( ProjectMembership.class );

	private Project				project;
	private ArrayList<UserRole>	roles;

	Project getProject()
	{
		return project;
	}

	void setProject( Project project )
	{
		this.project = project;
	}

	ArrayList<UserRole> getRoles()
	{
		return roles;
	}

	void setRoles( ArrayList<UserRole> roles )
	{
		this.roles = roles;
	}

	public List<String> validate( List<String> errors )
	{
		if( errors == null )
		{
			errors = new ArrayList<String>();
		}
		if( getProject() == null )
		{
			errors.add( "Project Id is null" );
		}
		if( getRoles() == null || getRoles().size() == 0 )
		{
			errors.add( "Role is null for membership" );
		}
		else
		{
			for( Iterator<UserRole> iterator = roles.iterator(); iterator.hasNext(); )
			{
				UserRole role = (UserRole) iterator.next();
				role.validate( errors );
			}
		}
		return errors;
	}

	public String toString()
	{
		if( LOGGER.isDebugEnabled() )
		{
			return "ProjectMembership@" + hashCode() + ": project[" + project + "] roles[" + roles + "]";
		}
		return "Project@" + hashCode() + ": project[" + project.getId() + "]";
	}

}

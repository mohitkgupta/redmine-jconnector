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

public class Issue extends RedmineBDO
{

	private static Log	LOGGER				= LogFactory.getLog( Issue.class );
	
	public static String INCLUDE_CHILDREN = "children";
	public static String INCLUDE_ATTACHMENTS = "attachments";
	public static String INCLUDE_RELATIONS = "relations";
	public static String INCLUDE_CHANGESETS = "changesets";
	public static String INCLUDE_JOURNALS = "journals";

	// TODO - check if binding.xml has mapping for all fields
	private Long		id;

	private Issue		parent;
	private String		subject;
	private String		description;
	private Date		startDate;
	private Date		dueDate;
	private User		assignedTo;
	private User		author;
	private Float		estimatedEfforts	= new Float( 0 );
	private Float		spentEfforts		= new Float( 0 );
	private Integer		percentageDone		= new Integer( 0 );
	private Date		createdOn;
	private Date		updatedOn;

	private Project		project;
	private Tracker		tracker;
	private Status		status;
	private Priority	priority;

	// custom fields are left

	public Long getId()
	{
		return id;
	}

	public void setId( Long id )
	{
		this.id = id;
	}

	public Priority getPriority()
	{
		return priority;
	}

	public Date getStartDate()
	{
		return startDate;
	}

	public void setStartDate( Date startDate )
	{
		this.startDate = startDate;
	}

	public Date getDueDate()
	{
		return dueDate;
	}

	public void setDueDate( Date dueDate )
	{
		this.dueDate = dueDate;
	}

	public Issue getParent()
	{
		return parent;
	}

	public void setParent( Issue parent )
	{
		this.parent = parent;
	}

	public String getSubject()
	{
		return subject;
	}

	public void setSubject( String subject )
	{
		this.subject = subject;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription( String description )
	{
		this.description = description;
	}

	public User getAssignedTo()
	{
		return assignedTo;
	}

	public void setAssignedTo( User owner )
	{
		this.assignedTo = owner;
	}

	public User getAuthor()
	{
		return author;
	}

	public void setAuthor( User creator )
	{
		this.author = creator;
	}

	public Integer getPercentageDone()
	{
		return percentageDone;
	}

	public void setPercentageDone( Integer percentageDone )
	{
		this.percentageDone = percentageDone;
	}

	public Float getEstimatedEfforts()
	{
		return estimatedEfforts;
	}

	public void setEstimatedEfforts( Float estimatedEfforts )
	{
		this.estimatedEfforts = estimatedEfforts;
	}

	public Float getSpentEfforts()
	{
		return spentEfforts;
	}

	public void setSpentEfforts( Float spentEfforts )
	{
		if( spentEfforts.floatValue() != 0 )
		{
			throw new UnsupportedOperationException(
					"Spent Efforts is not processed properly by Redmine API, so currently there is no use to set it" );
		}
		this.spentEfforts = spentEfforts;
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

	public Project getProject()
	{
		return project;
	}

	public void setProject( Project project )
	{
		this.project = project;
	}

	public Tracker getTracker()
	{
		return tracker;
	}

	public void setTracker( Tracker tracker )
	{
		this.tracker = tracker;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus( Status status )
	{
		this.status = status;
	}

	public void setPriority( Priority priority )
	{
		this.priority = priority;
	}

	public List<String> validate( List<String> errors )
	{
		if( errors == null )
		{
			errors = new ArrayList<String>();
		}
		if( getProject() == null )
		{
			errors.add( "Issue's Project is null." );
		}
		else
		{
			// getProject().validate( errors );

			// duplicating the validation check for project here, instead of using from project object.
			// because project validations checks Identifier property also, however, identifier is not set to project
			// when it is returned with Issue
			if( getProject().getId() == null )
			{
				errors.add( "Project Id is null" );
			}
			if( !Utilities.isQualifiedString( getProject().getName() ) )
			{
				errors.add( "Project Name is null." );
			}
		}
		if( !Utilities.isQualifiedString( getSubject() ) )
		{
			errors.add( "Issue's Subject is null or empty String" );
		}
		if( getPriority() == null )
		{
			errors.add( "Issue's Priority is null." );
		}
		else
		{
			getPriority().validate( errors );
		}
		if( getAuthor() != null )
		{
			getAuthor().validate( errors );
		}
		if( getAssignedTo() != null )
		{
			getAssignedTo().validate( errors );
		}
		if( getTracker() != null )
		{
			getTracker().validate( errors );
		}

		return errors;
	}

	public String toString()
	{
		if( LOGGER.isDebugEnabled() )
		{
			return "Issue@" + hashCode() + ": id[" + getId() + "] subject[" + subject + "] startDate[" + startDate
					+ "] endDate[" + dueDate + "] estimateEfforts[" + estimatedEfforts + "] spentEfforts["
					+ spentEfforts + "] percentageDone[" + percentageDone + "] assignedTo[" + assignedTo
					+ "] createdOn[" + createdOn + "] updatedOn[" + updatedOn + "] author[" + author + "]";
		}
		return "Issue@" + hashCode() + ": id[" + getId() + "]";
	}
}

package com.vedantatree.redmineconnector.testcases;

import java.util.Calendar;

import junit.framework.TestCase;

import com.vedantatree.redmineconnector.RCException;
import com.vedantatree.redmineconnector.RedmineConnector;
import com.vedantatree.redmineconnector.bdo.Issue;
import com.vedantatree.redmineconnector.bdo.Priority;
import com.vedantatree.redmineconnector.bdo.Project;
import com.vedantatree.redmineconnector.bdo.Status;
import com.vedantatree.redmineconnector.bdo.Tracker;
import com.vedantatree.redmineconnector.bdo.User;


/**
 * 
 * @author Mohit Gupta <mohit.gupta@vedantatree.com>
 * 
 */

public class RCTestCase extends TestCase
{

	private RedmineConnector	redmineConnector;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		redmineConnector = RedmineConnector.getSharedInstance();
	}

	@Override
	protected void tearDown() throws Exception
	{
		redmineConnector = null;
		super.tearDown();
	}

	public void testProjectCRUD() throws Exception
	{
		Project newProject = newProjectInstance();

		Project createdProject = redmineConnector.createProject( newProject );
		checkProjectData( newProject, createdProject );

		createdProject = redmineConnector.getProjectById( createdProject.getId() + "" );
		checkProjectData( newProject, createdProject );

		boolean deleted = redmineConnector.deleteProject( createdProject.getId() + "" );
		assertEquals( "Requested to delete the Project, result should be true but received false", true, deleted );

		try
		{
			createdProject = redmineConnector.getProjectById( createdProject.getId() + "" );
		}
		catch( RCException rce )
		{
			if( rce.getErrorCode() != RCException.OBJECT_NOT_FOUND )
			{
				fail( "Project has already been deleted. However we get the Project object from Redmine. rceErrorCode["
						+ rce.getErrorCode() + "]" );
			}
			createdProject = null;
		}
		assertNull(
				"Project has already been deleted. So a request for object should return Exception, however we didn't get the exception",
				createdProject );
	}

	public void testIssueCRUD() throws Exception
	{
		Issue newIssue = newIssueInstance();

		// Project newProject = newProjectInstance();
		// newProject = redmineConnector.createProject( newProject );
		// assertNotNull( "Got null project object while createing the new project", newProject );

		Project newProject = redmineConnector.getSharedInstance().getProjectById( "1" );
		newIssue.setProject( newProject );
		try
		{
			Issue createdIssue = redmineConnector.createIssue( newIssue );
			System.out.println( "issue created[" + createdIssue + "]" );
			System.out.println( "originalIssue[" + newIssue + "]" );

			checkIssueData( newIssue, createdIssue );

			createdIssue = redmineConnector.getIssueById( createdIssue.getId() + "" );
			checkIssueData( newIssue, createdIssue );

			boolean deleted = redmineConnector.deleteIssue( createdIssue.getId() + "" );
			assertEquals( "Requested to delete the issue, result should be true but received false", true, deleted );

			try
			{
				createdIssue = redmineConnector.getIssueById( createdIssue.getId() + "" );
			}
			catch( RCException rce )
			{
				if( rce.getErrorCode() != RCException.OBJECT_NOT_FOUND )
				{
					fail( "Issue has already been deleted. However we get the Issue object from Redmine" );
				}
				createdIssue = null;
			}
			assertNull(
					"Issue has already been deleted. So a request for object should return Exception, however we didn't get the exception",
					createdIssue );
		}
		finally
		{

			// boolean deleted = redmineConnector.deleteProject( newProject.getId() + "" );
			// assertEquals( "Requested to delete the project, result should be true but received false", true, deleted
			// );
		}
	}

	private Project newProjectInstance()
	{
		Project testProject = new Project();
		testProject.setDescription( "Test Project from Redmine Connector TestCase" );
		testProject.setHomePage( "http://www.vedantatree.com/" );
		testProject.setIdentifier( "rc-test-project" );
		testProject.setName( "Redmine Connector Test Project" );
		// User user = new User();
		// user.setId( new Long( 1 ) );
		// testProject.set
		return testProject;
	}

	private void checkProjectData( Project originalProject, Project projectFromRedmine )
	{
		assertNotNull( "Project from Redmine found null", projectFromRedmine );
		assertNotNull( "Id of project from Redmine found null", projectFromRedmine.getId() );
		assertEquals( "Project name is not matching", originalProject.getName(), projectFromRedmine.getName() );
		assertEquals( "Project identifier is not matching", originalProject.getIdentifier(), projectFromRedmine
				.getIdentifier() );
		assertEquals( "Project description is not matching", originalProject.getDescription(), projectFromRedmine
				.getDescription() );
		assertEquals( "Project home page is not matching", originalProject.getHomePage(), projectFromRedmine
				.getHomePage() );
	}

	private Issue newIssueInstance()
	{
		Issue testIssue = new Issue();
		testIssue.setSubject( "RedmineConnector TC Issue" );
		testIssue.setDescription( "Test Issue from Redmine Connector TestCase - Long Description" );
		Calendar cal = Calendar.getInstance();
		cal.set( 2011, 02, 25 );
		testIssue.setStartDate( cal.getTime() );
		cal.set( 2011, 04, 12 );
		testIssue.setDueDate( cal.getTime() );
		testIssue.setEstimatedEfforts( 12.5f );
		// testIssue.setSpentEfforts( 2.5f );
		testIssue.setPercentageDone( 50 );
		testIssue.setPriority( Priority.HIGH );
		testIssue.setTracker( Tracker.FEATURE );
		testIssue.setStatus( Status.IN_PROGRESS );
		testIssue.setAssignedTo( User.REDMINE_ADMIN );

		return testIssue;
	}

	private void checkIssueData( Issue originalIssue, Issue issueFromRedmine )
	{
		assertNotNull( "Issue from Redmine found null", issueFromRedmine );
		assertNotNull( "Id of Redmine Issue found null", issueFromRedmine.getId() );
		assertEquals( "Short description of issue is not matching", originalIssue.getSubject(), issueFromRedmine
				.getSubject() );
		assertEquals( "Long description of issue is not matching", originalIssue.getDescription(), issueFromRedmine
				.getDescription() );
		assertEquals( "Estimated effort of issue is not matching", originalIssue.getEstimatedEfforts(),
				issueFromRedmine.getEstimatedEfforts(), 0 );
		assertEquals( "Spent effort of issue is not matching", originalIssue.getSpentEfforts(), issueFromRedmine
				.getSpentEfforts(), 0 );
		assertEquals( "Percentage Done of issue is not matching", originalIssue.getPercentageDone(), issueFromRedmine
				.getPercentageDone() );
		// assertEquals( newIssue.getProject().getId(), createdIssue.getProject().getId() );
		// for tracker, priority and user
	}

}

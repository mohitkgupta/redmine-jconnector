package com.vedantatree.redmineconnector.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vedantatree.redmineconnector.RCException;
import com.vedantatree.redmineconnector.RedmineConnector;
import com.vedantatree.redmineconnector.RedmineDataPaginator;
import com.vedantatree.redmineconnector.bdo.Issue;
import com.vedantatree.redmineconnector.bdo.Priority;
import com.vedantatree.redmineconnector.bdo.Project;
import com.vedantatree.redmineconnector.bdo.RedmineBDO;
import com.vedantatree.redmineconnector.bdo.Status;
import com.vedantatree.redmineconnector.bdo.Tracker;
import com.vedantatree.redmineconnector.bdo.User;


/**
 * Test case of RedmineConnector
 * 
 * 
 * @author Mohit Gupta <mohit.gupta@vedantatree.com>
 */

public class RCTestCase
{

	private static Log				LOGGER					= LogFactory.getLog( RCTestCase.class );
	/**
	 * Default value for User. It is corresponding to Default value in Redmine. If anyone has created new objects, then
	 * these values may vary
	 * 
	 * For testing purpose only
	 * */
	public static User		REDMINE_ADMIN		= new User( new Long( 1 ) );



	private RedmineConnector	redmineConnector;
	private static int			projectIdentifer = Integer.MIN_VALUE;

	@Before
	public void setUp() throws Exception
	{
		redmineConnector = RedmineConnector.getSharedInstance();
		projectIdentifer = 1;
	}

	@After
	public void tearDown() throws Exception
	{
		redmineConnector = null;
		projectIdentifer = Integer.MIN_VALUE;
	}

	@Test
	public void testProjectCRUD() throws Exception
	{
		Project newProject = newProjectInstance();
		Project createdProject = null;
		try
		{
			createdProject = redmineConnector.createProject( newProject );
			checkProjectData( newProject, createdProject );

			createdProject = redmineConnector.getProjectById( createdProject.getId() );
			checkProjectData( newProject, createdProject );

			createdProject.setDescription( createdProject.getDescription() + "---- Updated------" );
			redmineConnector.updateProject( createdProject );
			Project updatedProject = redmineConnector.getProjectById( createdProject.getId() );
			checkProjectData( createdProject, updatedProject );

		}
		finally
		{
			if( createdProject != null )
			{
				boolean deleted = redmineConnector.deleteProject( createdProject.getId() );
				assertEquals( "Requested to delete the Project, result should be true but received false", true,
						deleted );
			}
		}

		try
		{
			createdProject = redmineConnector.getProjectById( createdProject.getId() );
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

	@Test
	public void testIssueCRUD() throws Exception
	{
		Project newProject = newProjectInstance();
		newProject = redmineConnector.createProject( newProject );
		assertNotNull( "Got null project object while createing the new project", newProject );

		Issue newIssue = newIssueInstance();
		newIssue.setProject( newProject );
		try
		{
			Issue createdIssue = redmineConnector.createIssue( newIssue );
			System.out.println( "issue created[" + createdIssue + "]" );
			System.out.println( "originalIssue[" + newIssue + "]" );

			checkIssueData( newIssue, createdIssue );

			createdIssue = redmineConnector.getIssueById( createdIssue.getId() );
			checkIssueData( newIssue, createdIssue );

			createdIssue.setDescription( createdIssue.getDescription() + "----" + System.currentTimeMillis() );
			redmineConnector.updateIssue( createdIssue );
			Issue updatedIssue = redmineConnector.getIssueById( createdIssue.getId() );
			checkIssueData( createdIssue, updatedIssue );

			boolean deleted = redmineConnector.deleteIssue( createdIssue.getId() );
			assertEquals( "Requested to delete the issue, result should be true but received false", true, deleted );

			try
			{
				createdIssue = redmineConnector.getIssueById( createdIssue.getId() );
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

			boolean deleted = redmineConnector.deleteProject( newProject.getId() );
			assertEquals( "Requested to delete the project, result should be true but received false", true, deleted );
		}
	}

	@Test
	public void testIssuePaginator() throws Exception
	{
		try
		{
			Project newProject = newProjectInstance();
			newProject = redmineConnector.createProject( newProject );
			assertNotNull( "Got null project object while createing the new project", newProject );

			IssueCreator issueCreator = new IssueCreator( newProject, 1 );
			executeJob( issueCreator );

			RedmineDataPaginator issuePaginator = redmineConnector.getIssues();

			do
			{
				List<RedmineBDO> issues = issuePaginator.nextPageRecords();
				System.err.println( "############issues-size[" + ( issues != null ? issues.size() : -1 ) + "]" );

				for( Iterator iterator = issues.iterator(); iterator.hasNext(); )
				{
					Issue issue = (Issue) iterator.next();
					redmineConnector.deleteIssue( issue.getId() );
				}

			} while( issuePaginator.hasMoreRecords() );

			boolean deleted = redmineConnector.deleteProject( newProject.getId() );
			assertEquals( "Requested to delete the project, result should be true but received false", true, deleted );
		}
		catch( Exception ex )
		{
			LOGGER.error( ex );
			ex.printStackTrace();
			fail( "Failed due to exception faced. Exception[" + ex.getMessage() + "]" );
		}
	}

	@Test
	public void testProjectPaginator() throws Exception
	{
		try
		{
			ProjectCreator projectCreator = new ProjectCreator( 1 );
			executeJob( projectCreator );

			RedmineDataPaginator projectPaginator = redmineConnector.getProjects();

			do
			{
				List<RedmineBDO> projects = projectPaginator.nextPageRecords();
				System.err.println( "############projects-size[" + ( projects != null ? projects.size() : -1 ) + "]" );

				for( Iterator iterator = projects.iterator(); iterator.hasNext(); )
				{
					Project project = (Project) iterator.next();
					redmineConnector.deleteProject( project.getId() );
				}

			} while( projectPaginator.hasMoreRecords() );

		}
		catch( Exception ex )
		{
			LOGGER.error( ex );
			ex.printStackTrace();
			fail( "Failed due to exception faced. Exception[" + ex.getMessage() + "]" );
		}
	}

	private Project newProjectInstance()
	{
		Project testProject = new Project();
		testProject.setDescription( "Test Project from Redmine Connector TestCase. Time:" + System.currentTimeMillis() );
		testProject.setHomePage( "http://www.vedantatree.com/" );
		testProject.setIdentifier( "rc" + ( projectIdentifer++ ) );
		testProject.setName( "Redmine Connector Test Project" );
		Calendar cal = Calendar.getInstance();
		testProject.setCreatedOn( cal.getTime() );
		testProject.setUpdatedOn( cal.getTime() );
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
		testIssue.setDescription( "Test Issue from Redmine Connector TestCase - Long Description. Time:"
				+ System.currentTimeMillis() );
		Calendar cal = Calendar.getInstance();
		cal.set( 2011, 02, 25 );
		testIssue.setStartDate( cal.getTime() );
		cal.set( 2011, 04, 12 );
		testIssue.setDueDate( cal.getTime() );
		testIssue.setSubject( "RedmineConnector TC Issue - " + cal.getTime() );
		testIssue.setEstimatedEfforts( 12.5f );
		// testIssue.setSpentEfforts( 2.5f );
		testIssue.setPercentageDone( 50 );
		testIssue.setPriority( Priority.HIGH );
		testIssue.setTracker( Tracker.FEATURE );
		testIssue.setStatus( Status.IN_PROGRESS );
		testIssue.setAssignedTo( REDMINE_ADMIN );

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

	private void executeJob( Runnable job ) throws Exception
	{

		Thread thread1 = new Thread( job, "" + System.currentTimeMillis() );
		Thread thread2 = new Thread( job, "" + System.currentTimeMillis() );
		Thread thread3 = new Thread( job, "" + System.currentTimeMillis() );
		Thread thread4 = new Thread( job, "" + System.currentTimeMillis() );
		Thread thread5 = new Thread( job, "" + System.currentTimeMillis() );

		thread1.start();
		thread2.start();
		thread3.start();
		thread4.start();
		thread5.start();

		thread1.join();
		thread2.join();
		thread3.join();
		thread4.join();
		thread5.join();
	}

	class IssueCreator implements Runnable
	{

		private int		issueCount;
		private Project	project;

		public IssueCreator( Project project, int issuesCountPerJob )
		{
			this.project = project;
			this.issueCount = issuesCountPerJob;
		}

		public void run()
		{
			for( int i = 0; i < issueCount; i++ )
			{
				Issue newIssue = newIssueInstance();
				newIssue.setProject( project );
				try
				{
					redmineConnector.createIssue( newIssue );
				}
				catch( RCException e )
				{
					fail( e.getMessage() );
				}
			}
		}
	}

	class ProjectCreator implements Runnable
	{

		private int	projectsCount;

		public ProjectCreator( int projectsCountPerJob )
		{
			this.projectsCount = projectsCountPerJob;
		}

		public void run()
		{
			for( int i = 0; i < projectsCount; i++ )
			{
				Project newProject = newProjectInstance();
				try
				{
					redmineConnector.createProject( newProject );
				}
				catch( RCException e )
				{
					fail( e.getMessage() );
				}
			}
		}
	};

}

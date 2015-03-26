package com.vedantatree.redmineconnector.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;

import com.vedantatree.redmineconnector.bdo.Issue;
import com.vedantatree.redmineconnector.bdo.Priority;
import com.vedantatree.redmineconnector.bdo.Project;
import com.vedantatree.redmineconnector.bdo.Status;
import com.vedantatree.redmineconnector.bdo.Tracker;
import com.vedantatree.redmineconnector.bdo.User;


/**
 * A class having various utilities method for Testing the RedmineJConnector API
 * 
 * @author Mohit Gupta [mohit.gupta@vedantatree.com]
 * @since 1.1.0
 */
public class TestUtils
{

	private static int	projectIdentifer	= Integer.MIN_VALUE;
	private static int	USER_ID				= 0;

	/**
	 * Default value for User. It is corresponding to Default value in Redmine. If anyone has created new objects, then
	 * these values may vary
	 * 
	 * For testing purpose only
	 * */
	static User			REDMINE_ADMIN		= null;

	static
	{
		REDMINE_ADMIN = new User();
		REDMINE_ADMIN.setId( new Long( 1 ) );
		REDMINE_ADMIN.setFirstName( "admin" );

	}

	/**
	 * Execute the given job in 5 different threads for concurrent processing.
	 * 
	 * This is mostly used to perform time consuming jobs like if we want to create 1000 issues, or projects etc.
	 * 
	 * @param job Job to execute
	 * @throws Exception If there is any problem in execution
	 */
	static void executeJob( Runnable job ) throws Exception
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

	/**
	 * Method to create a new Project instance with some default data
	 * 
	 * @return Newly created Project. It is not persisted in Redmine.
	 */
	static Project newProjectInstance()
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

	/**
	 * Method to compare the data of given objects. It will put asserts on comparisons and hence will throw assertion
	 * failure errors in case comparison fails.
	 * 
	 * @param originalProject Original Project Object to compare
	 * @param projectFromRedmine Project Object retrieved from Redmine Server to compare
	 */
	static void checkProjectData( Project originalProject, Project projectFromRedmine )
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

	/**
	 * Method to create a new Issue instance with some default data
	 * 
	 * @return Newly created Issue. It is not persisted in Redmine.
	 */
	static Issue newIssueInstance()
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

	/**
	 * Method to compare the data of given objects. It will put asserts on comparisons and hence will throw assertion
	 * failure errors in case comparison fails.
	 * 
	 * @param originalIssue Original Issue Object to compare
	 * @param issueFromRedmine Issue Object retrieved from Redmine Server to compare
	 */
	static void checkIssueData( Issue originalIssue, Issue issueFromRedmine )
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

	/**
	 * Method to create a new User instance with some default data
	 * 
	 * @return Newly created User. It is not persisted in Redmine.
	 */
	static User newUserInstance()
	{
		User testUser = new User();

		testUser.setFirstName( "M-" + System.currentTimeMillis() );
		testUser.setLastName( "G-" + System.currentTimeMillis() );
		synchronized( REDMINE_ADMIN ) // can take lock on any other object :)
		{
			USER_ID = USER_ID + 1;
			testUser.setEmail( System.currentTimeMillis() + USER_ID + "@vedantatree.com" );
			testUser.setLogin( Thread.currentThread().getName() + "." + System.currentTimeMillis() + ".test" + USER_ID );
		}
		testUser.setPassword( "secret" );
		testUser.setAuthenticationSourceType( 2 );
		Calendar cal = Calendar.getInstance();
		testUser.setCreatedOn( cal.getTime() );
		testUser.setLastLoginOn( cal.getTime() );
		return testUser;
	}

	/**
	 * Method to compare the data of given objects. It will put asserts on comparisons and hence will throw assertion
	 * failure errors in case comparison fails.
	 * 
	 * @param originalUser Original User Object to compare
	 * @param userFromRedmine User Object retrieved from Redmine Server to compare
	 */
	static void checkUserData( User originalUser, User userFromRedmine )
	{
		assertNotNull( "User from Redmine found null", userFromRedmine );
		assertNotNull( "Id of user from Redmine found null", userFromRedmine.getId() );
		assertEquals( "User first name is not matching", originalUser.getFirstName(), userFromRedmine.getFirstName() );
		assertEquals( "User second name is not matching", originalUser.getLastName(), userFromRedmine.getLastName() );
		assertEquals( "User Email is not matching", originalUser.getEmail(), userFromRedmine.getEmail() );

		// TODO match created on, last access on etc
	}

}

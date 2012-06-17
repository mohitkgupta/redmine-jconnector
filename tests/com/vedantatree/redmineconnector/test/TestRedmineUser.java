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
import com.vedantatree.redmineconnector.bdo.RedmineBDO;
import com.vedantatree.redmineconnector.bdo.User;


public class TestRedmineUser
{

	private static Log			LOGGER			= LogFactory.getLog( RCTestCase.class );
	/**
	 * Default value for User. It is corresponding to Default value in Redmine. If anyone has created new objects, then
	 * these values may vary
	 * 
	 * For testing purpose only
	 * */
	private static User			REDMINE_ADMIN	= null;
	private static int			USER_ID			= 0;

	private RedmineConnector	redmineConnector;

	@Before
	public void setUp() throws Exception
	{
		// TODO clean all redmine data
		redmineConnector = RedmineConnector.getSharedInstance();
		REDMINE_ADMIN = new User();
		REDMINE_ADMIN.setId( new Long( 1 ) );
		REDMINE_ADMIN.setFirstName( "admin" );
		USER_ID = 0;
	}

	@After
	public void tearDown() throws Exception
	{
		// TODO clean all redmine data
		redmineConnector = null;
		USER_ID = 0;

		// write a class for data cleaning, like all projects, users, issues etc
	}

	@Test
	public void testUserCRUD() throws Exception
	{
		User newUser = newUserInstance();
		User createdUser = null;
		try
		{
			createdUser = redmineConnector.createUser( newUser );
			checkUserData( newUser, createdUser );

			createdUser = redmineConnector.getUserById( createdUser.getId(), null );
			checkUserData( newUser, createdUser );

			createdUser.setFirstName( createdUser.getFirstName() + "---- Updated------" );
			redmineConnector.updateUser( createdUser );
			User updatedUser = redmineConnector.getUserById( createdUser.getId(), null );
			checkUserData( createdUser, updatedUser );

		}
		finally
		{
			if( createdUser != null )
			{
				LOGGER.debug( "deleting user >>>>>>>>>>>>>>>>>>>>>>>>>" + createdUser.getId() + "----"
						+ createdUser.getLogin() );
				boolean deleted = redmineConnector.deleteUser( createdUser.getId() );
				assertEquals( "Requested to delete the User, result should be true but received false", true, deleted );
			}
		}

		try
		{
			createdUser = redmineConnector.getUserById( createdUser.getId(), null );
		}
		catch( RCException rce )
		{
			if( rce.getErrorCode() != RCException.OBJECT_NOT_FOUND )
			{
				fail( "User has already been deleted. However we get the User object from Redmine. rceErrorCode["
						+ rce.getErrorCode() + "]" );
			}
			createdUser = null;
		}
		assertNull(
				"User has already been deleted. So a request for object should return Exception, however we didn't get the exception",
				createdUser );
	}

	@Test
	public void testUserPaginator() throws Exception
	{
		try
		{
			UserCreator userCreator = new UserCreator( 1 );
			executeJob( userCreator );

			RedmineDataPaginator userPaginator = redmineConnector.getUsersIterator( 0, 25, null, null );

			do
			{
				List<RedmineBDO> users = userPaginator.nextPageRecords();
				System.err.println( "############users-size[" + ( users != null ? users.size() : -1 ) + "]" );

				for( Iterator iterator = users.iterator(); iterator.hasNext(); )
				{
					User user = (User) iterator.next();
					// if( true )
					// {
					// throw new UnsupportedOperationException(
					// "Review this method carefully for Admin user check below. If it is correct, comment it "
					// + "and then run the test case. If it is wrong, it can delete Redmine Admin user also. "
					// + "Check carefully." );
					// }
					if( user.getLogin().indexOf( "admin" ) >= 0 || user.getId() == 1 )
					{
						LOGGER.debug( "GOT ADMIN>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + user.getLogin()
								+ "---" + user.getId() );
						continue;
					}
					redmineConnector.deleteUser( user.getId() );
				}

			} while( userPaginator.hasMoreRecords() );

		}
		catch( Exception ex )
		{
			LOGGER.error( ex );
			ex.printStackTrace();
			fail( "Failed due to exception faced. Exception[" + ex.getMessage() + "]" );
		}
	}

	private User newUserInstance()
	{
		User testUser = new User();

		testUser.setFirstName( "Mohit" + System.currentTimeMillis() );
		testUser.setLastName( "Gupta" + System.currentTimeMillis() );
		synchronized( Thread.currentThread() )
		{
			USER_ID = USER_ID + 1;
		}
		testUser.setEmail( System.currentTimeMillis() + USER_ID + "@vedantatree.com" );
		testUser.setLogin( Thread.currentThread().getName() + "." + System.currentTimeMillis() + ".test" + USER_ID );
		testUser.setPassword( "secret" );
		testUser.setAuthenticationSourceType( 2 );
		Calendar cal = Calendar.getInstance();
		testUser.setCreatedOn( cal.getTime() );
		testUser.setLastLoginOn( cal.getTime() );
		return testUser;
	}

	private void checkUserData( User originalUser, User userFromRedmine )
	{
		assertNotNull( "User from Redmine found null", userFromRedmine );
		assertNotNull( "Id of user from Redmine found null", userFromRedmine.getId() );
		assertEquals( "User first name is not matching", originalUser.getFirstName(), userFromRedmine.getFirstName() );
		assertEquals( "User second name is not matching", originalUser.getLastName(), userFromRedmine.getLastName() );
		assertEquals( "User Email is not matching", originalUser.getEmail(), userFromRedmine.getEmail() );

		// TODO match created on, last access on etc
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

	class UserCreator implements Runnable
	{

		private int		issueCount;
		private User	user;

		public UserCreator( int issuesCountPerJob )
		{
			this.issueCount = issuesCountPerJob;
		}

		public void run()
		{
			for( int i = 0; i < issueCount; i++ )
			{
				try
				{
					Thread.sleep( 1000 );
					User newUser = newUserInstance();
					redmineConnector.createUser( newUser );
				}
				catch( Exception e )
				{
					// TODO: this error does not fail the test case being in different thread. Do something.
					LOGGER.error( e );
					fail( e.getMessage() );
				}
			}
		}
	}

}

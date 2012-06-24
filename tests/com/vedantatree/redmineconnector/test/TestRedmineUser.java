package com.vedantatree.redmineconnector.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

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


/**
 * Test case for RedmineConnector User API
 * 
 * 
 * @author Mohit Gupta [mohit.gupta@vedantatree.com]
 * @since 1.1.0
 */
public class TestRedmineUser
{

	private static Log			LOGGER	= LogFactory.getLog( TestRedmineProjects.class );

	private RedmineConnector	redmineConnector;

	@Before
	public void setUp() throws Exception
	{
		// TODO clean all redmine data
		redmineConnector = RedmineConnector.getSharedInstance();
	}

	@After
	public void tearDown() throws Exception
	{
		// TODO clean all redmine data
		redmineConnector = null;

		// write a class for data cleaning, like all projects, users, issues etc
	}

	@Test
	public void testUserCRUD() throws Exception
	{
		User newUser = TestUtils.newUserInstance();
		User createdUser = null;
		try
		{
			createdUser = redmineConnector.createUser( newUser );
			TestUtils.checkUserData( newUser, createdUser );

			createdUser = redmineConnector.getUserById( createdUser.getId(), null );
			TestUtils.checkUserData( newUser, createdUser );

			createdUser.setFirstName( createdUser.getFirstName() + "-U-" );
			redmineConnector.updateUser( createdUser );
			User updatedUser = redmineConnector.getUserById( createdUser.getId(), null );
			TestUtils.checkUserData( createdUser, updatedUser );

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
			TestUtils.executeJob( userCreator );

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
					User newUser = TestUtils.newUserInstance();
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

package com.vedantatree.redmineconnector.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.HashMap;
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
import com.vedantatree.redmineconnector.bdo.Project;
import com.vedantatree.redmineconnector.bdo.RedmineBDO;


/**
 * Test case for RedmineConnector Issues API
 * 
 * @author Mohit Gupta [mohit.gupta@vedantatree.com]
 */

public class TestRedmineIssues
{

	private static Log			LOGGER	= LogFactory.getLog( TestRedmineIssues.class );

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
	}

	@Test
	public void testIssueCRUD() throws Exception
	{
		Project newProject = TestUtils.newProjectInstance();

		// create project
		newProject = redmineConnector.createProject( newProject );
		assertNotNull( "Got null project object while createing the new project", newProject );

		Issue newIssue = TestUtils.newIssueInstance();
		newIssue.setProject( newProject );
		try
		{
			// create issue
			Issue createdIssue = redmineConnector.createIssue( newIssue );
			TestUtils.checkIssueData( newIssue, createdIssue );

			// retrieve saved issue
			createdIssue = redmineConnector.getIssueById( createdIssue.getId(), null );
			TestUtils.checkIssueData( newIssue, createdIssue );

			// update issue
			createdIssue.setDescription( createdIssue.getDescription() + "----" + System.currentTimeMillis() );
			redmineConnector.updateIssue( createdIssue );

			// retrieve updated issue
			Issue updatedIssue = redmineConnector.getIssueById( createdIssue.getId(), null );
			TestUtils.checkIssueData( createdIssue, updatedIssue );

			// delete issue
			boolean deleted = redmineConnector.deleteIssue( createdIssue.getId() );
			assertEquals( "Requested to delete the issue, result should be true but received false", true, deleted );

			try
			{
				// try to retrieve delete issue
				createdIssue = redmineConnector.getIssueById( createdIssue.getId(), null );
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
			// delete project
			boolean deleted = redmineConnector.deleteProject( newProject.getId() );
			assertEquals( "Requested to delete the project, result should be true but received false", true, deleted );
		}
	}

	@Test
	public void testIssuePaginator() throws Exception
	{
		Project newProject = TestUtils.newProjectInstance();
		try
		{
			// create project
			newProject = redmineConnector.createProject( newProject );
			assertNotNull( "Got null project object while createing the new project", newProject );

			// take service of issue creator to create issues for testing
			int issuesPerCreator = 1;
			IssueCreator issueCreator = new IssueCreator( newProject, issuesPerCreator );
			TestUtils.executeJob( issueCreator );
			int totalNoOfIssuesCreated = issuesPerCreator * 5; // issues per creator * total number of creators

			// try to retrieve issue iterator for wrong project id = -100
			HashMap<String, String> filterMap = new HashMap<String, String>();

			// get issue iterator for saved issues for right project id
			filterMap.put( Issue.FILTER_PROJECT_ID, newProject.getId() + "" );
			RedmineDataPaginator issuePaginator = redmineConnector.getIssuesIterator( 0, 20, null, filterMap );

			// iterate over issues iterator and delete one by one all issues
			int totalIssuesRetrieved = 0;
			do
			{
				List issues = issuePaginator.nextPageRecords();
				LOGGER.debug( "############issues-size[" + ( issues != null ? issues.size() : -1 ) + "]" );

				for( Iterator iterator = issues.iterator(); iterator.hasNext(); )
				{
					Issue issue = (Issue) iterator.next();
					totalIssuesRetrieved += 1;
					redmineConnector.deleteIssue( issue.getId() );
				}

			} while( issuePaginator.hasMoreRecords() );

			assertEquals( "Issues retrieved are not equal to issues created. issuesRetrieved[" + totalIssuesRetrieved
					+ "] issuesCreated[" + totalNoOfIssuesCreated + "]", totalNoOfIssuesCreated, totalIssuesRetrieved );

			issuePaginator = redmineConnector.getIssuesIterator( 0, 20, null, filterMap );
			List<RedmineBDO> issues = issuePaginator.hasMoreRecords() ? issuePaginator.nextPageRecords() : null;
			if( issues != null && issues.size() > 0 )
			{
				fail( "No issue should be retrieved as issues are already deleted" );
			}
		}
		catch( Exception ex )
		{
			LOGGER.error( ex );
			ex.printStackTrace();
			fail( "Failed due to exception faced. Exception[" + ex.getMessage() + "]" );
		}
		finally
		{
			// delete project
			if( newProject.getId() != null )
			{
				boolean deleted = redmineConnector.deleteProject( newProject.getId() );
				assertEquals( "Requested to delete the project, result should be true but received false", true,
						deleted );
			}
		}
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
				Issue newIssue = TestUtils.newIssueInstance();
				newIssue.setProject( project );
				try
				{
					Thread.sleep( 1000 );
					redmineConnector.createIssue( newIssue );
				}
				catch( Exception e )
				{
					// TODO: this error does not fail the test case being in different thread. Do something.
					fail( e.getMessage() );
				}
			}
		}
	}

}

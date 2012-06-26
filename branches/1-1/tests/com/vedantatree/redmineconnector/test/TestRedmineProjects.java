package com.vedantatree.redmineconnector.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
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
import com.vedantatree.redmineconnector.bdo.Project;
import com.vedantatree.redmineconnector.bdo.RedmineBDO;
import com.vedantatree.redmineconnector.bdo.User;


/**
 * Test case of RedmineConnector for Project and Issues API
 * 
 * 
 * @author Mohit Gupta [mohit.gupta@vedantatree.com]
 */
/*
 * TODO - Separate Project and Issue test cases in separate classes
 */
public class TestRedmineProjects
{

	private static Log			LOGGER			= LogFactory.getLog( TestRedmineProjects.class );

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
	public void testProjectCRUD() throws Exception
	{
		Project newProject = TestUtils.newProjectInstance();
		Project createdProject = null;
		try
		{
			// create project
			createdProject = redmineConnector.createProject( newProject );
			TestUtils.checkProjectData( newProject, createdProject );

			// define includes for get query
			ArrayList<String> includes = new ArrayList<String>();
			includes.add( Project.INCLUDE_TRACKERS );

			// retrieve saved project
			createdProject = redmineConnector.getProjectById( createdProject.getId(), includes );
			TestUtils.checkProjectData( newProject, createdProject );

			// update project
			createdProject.setDescription( createdProject.getDescription() + "---- Updated------" );
			redmineConnector.updateProject( createdProject );

			// retrieve updated project
			// includes.add( "issue_categories" ); // issue_categories are not supported yet
			Project updatedProject = redmineConnector.getProjectById( createdProject.getId(), includes );
			TestUtils.checkProjectData( createdProject, updatedProject );

		}
		finally
		{
			// delete project
			if( createdProject != null )
			{
				boolean deleted = redmineConnector.deleteProject( createdProject.getId() );
				assertEquals( "Requested to delete the Project, result should be true but received false", true,
						deleted );
			}
		}

		try
		{
			// try to retrieve project which is already deleted
			createdProject = redmineConnector.getProjectById( createdProject.getId(), null );
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
	public void testProjectPaginator() throws Exception
	{
		try
		{
			// use project creator to create projects for testing
			int projectsPerCreator = 1;
			ProjectCreator projectCreator = new ProjectCreator( 1 );
			TestUtils.executeJob( projectCreator );
			int totalNoOfProjectsCreated = projectsPerCreator * 5; // issues per creator * total number of creators

			// get project iterator
			RedmineDataPaginator projectPaginator = redmineConnector.getProjectsIterator( 0, 20, null, null );

			// iterate over project iterator
			int totalProjectsRetrieved = 0;
			do
			{
				List<RedmineBDO> projects = projectPaginator.nextPageRecords();
				System.err.println( "############projects-size[" + ( projects != null ? projects.size() : -1 ) + "]" );

				// delete all projects fetch as one page
				for( Iterator iterator = projects.iterator(); iterator.hasNext(); )
				{
					Project project = (Project) iterator.next();
					totalProjectsRetrieved += 1;
					redmineConnector.deleteProject( project.getId() );
				}

			} while( projectPaginator.hasMoreRecords() );

			assertEquals( "Projects retrieved are not equal to projects created. projectsRetrieved["
					+ totalProjectsRetrieved + "] projectsCreated[" + totalNoOfProjectsCreated + "]",
					totalNoOfProjectsCreated, totalProjectsRetrieved );

		}
		catch( Exception ex )
		{
			LOGGER.error( ex );
			ex.printStackTrace();
			fail( "Failed due to exception faced. Exception[" + ex.getMessage() + "]" );
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
				Project newProject = TestUtils.newProjectInstance();
				try
				{
					Thread.sleep( 1000 );
					redmineConnector.createProject( newProject );
				}
				catch( Exception e )
				{
					// TODO: this error does not fail the test case being in different thread. Do something.
					fail( e.getMessage() );
				}
			}
		}
	};

}

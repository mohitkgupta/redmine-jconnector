package com.vedantatree.redmineconnector.bdo;

import java.util.ArrayList;


public class ProjectsContainer extends RedmineBDOContainer
{

	public ArrayList<Project> getRedmineProjects()
	{
		return (ArrayList<Project>) super.getRedmineDataObjects();
	}

}

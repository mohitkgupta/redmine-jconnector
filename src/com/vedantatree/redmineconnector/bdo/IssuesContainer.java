package com.vedantatree.redmineconnector.bdo;

import java.util.ArrayList;


public class IssuesContainer extends RedmineBDOContainer
{

	public ArrayList<Issue> getRedmineIssues()
	{
		return (ArrayList<Issue>) super.getRedmineDataObjects();
	}

}

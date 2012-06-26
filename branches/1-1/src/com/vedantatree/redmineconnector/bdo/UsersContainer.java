package com.vedantatree.redmineconnector.bdo;

import java.util.ArrayList;


public class UsersContainer extends RedmineBDOContainer
{

	public ArrayList<User> getRedmineUsers()
	{
		return (ArrayList<User>) super.getRedmineDataObjects();
	}

}

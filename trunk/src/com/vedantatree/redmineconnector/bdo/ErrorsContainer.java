package com.vedantatree.redmineconnector.bdo;

import java.util.ArrayList;


public class ErrorsContainer
{

	// can not use List, as JIBX supports concrete classes only
	private ArrayList<Error>	errors;

	public void setErrors( ArrayList<Error> errors )
	{
		this.errors = errors;
	}

	public ArrayList<Error> getErrors()
	{
		return errors;
	}

}

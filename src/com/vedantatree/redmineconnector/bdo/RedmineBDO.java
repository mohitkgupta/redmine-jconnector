package com.vedantatree.redmineconnector.bdo;

import java.util.List;


public abstract class RedmineBDO
{

	// no use of keeping id here, as Jibx is not supporting property from parent class. We may need to explore more.
	 private Long id;

	public Long getId()
	{
		return id;
	}

	public void setId( Long id )
	{
		this.id = id;
	}

	public abstract List<String> validate( List<String> errors );

}

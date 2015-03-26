package com.vedantatree.redmineconnector.bdo;

import java.util.List;


public abstract class RedmineBDO
{

	/*
	 * Have to remove id from here. Facing problem with binding configuration.
	 * 
	 * Like, with mapping of Issue, project is used as reference object. So we tried with mapping as
	 * 
	 * <mapping name="issue" class="com.vedantatree.redmineconnector.bdo.Issue"
	 * extends="com.vedantatree.redmineconnector.bdo.RedmineBDO" value-style="element">
	 * 
	 * <structure map-as="com.vedantatree.redmineconnector.bdo.RedmineBDO" />
	 * 
	 * <!--
	 * 
	 * were trying to map as "compact project", but jibx gives error as
	 * "Binding has not mapping that extends compactProject" Hence we change it as following. --> <structure
	 * name="project" field="project" value-style="attribute"> <value name="name" field="name" style="attribute"/>
	 * <structure map-as="com.vedantatree.redmineconnector.bdo.RedmineBDO" value-style="attribute"/> </structure>
	 * 
	 * Here we repeated the fields name etc with project, because the attribute style is different i.e. attribute. With
	 * original mapping of Project, value style is element. We tried to work with original project mapping by just
	 * giving the value style on structure tag, but that does not work.
	 * 
	 * Then we tried to put the fields here with 'attribute' value style. Here we are again facing problem with
	 * RedmineBDO mapping as structure. Jibx is not read the id field of RedmineBDO. Problem faced is, Jibx gives error
	 * that id field found when name expected or something like this.
	 * 
	 * We can give direct id field here, instead of RedmineBDO structure. Because then Jibx says that id field is not
	 * found in Object, as it is in super abstract class.
	 * 
	 * Hence for now, we are moving this field to sub classes. Will try to find clean solution later.
	 */
	// private Long id;
	//
	// public Long getId()
	// {
	// return id;
	// }
	//
	// public void setId( Long id )
	// {
	// this.id = id;
	// }

	public abstract List<String> validate( List<String> errors );

}

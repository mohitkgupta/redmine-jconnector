package com.vedantatree.redmineconnector.bdo;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class RedmineBDOContainer
{

	private static Log				LOGGER	= LogFactory.getLog( RedmineBDOContainer.class );

	protected int					limit;
	protected long					totalCount;
	protected int					offset;

	protected ArrayList<RedmineBDO>	redmineDataObjects;

	public int getLimit()
	{
		return limit;
	}

	public void setLimit( int limit )
	{
		this.limit = limit;
	}

	public long getTotalCount()
	{
		return totalCount;
	}

	public void setTotalCount( long totalCount )
	{
		this.totalCount = totalCount;
	}

	public int getOffset()
	{
		return offset;
	}

	public void setOffset( int offset )
	{
		this.offset = offset;
	}

	public ArrayList getRedmineDataObjects()
	{
		return redmineDataObjects;
	}

	public void setRedmineDataObjects( ArrayList<RedmineBDO> redmineDataObjects )
	{
		this.redmineDataObjects = redmineDataObjects;
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append( "RedmineBDOContainer@" );
		sb.append( hashCode() );
		if( LOGGER.isDebugEnabled() )
		{
			sb.append( ": limit[" );
			sb.append( getLimit() );
			sb.append( "] offset[" );
			sb.append( getOffset() );
			sb.append( "] totalCount[" );
			sb.append( getTotalCount() );
			sb.append( "] currentObjectCount[" );
			sb.append( getRedmineDataObjects() != null ? getRedmineDataObjects().size() : -1 );
			sb.append( "]" );
		}
		return sb.toString();
	}

}

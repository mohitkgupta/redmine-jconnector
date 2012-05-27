package com.vedantatree.redmineconnector;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vedantatree.redmineconnector.bdo.RedmineBDO;
import com.vedantatree.redmineconnector.bdo.RedmineBDOContainer;
import com.vedantatree.redmineconnector.utils.Utilities;


/**
 * This class provides the default implementation of RedmineDataPaginator interface.
 * 
 * TODO should we change all int to long, as record count can go in long values.
 * 
 * @author Mohit Gupta [mohit.gupta@vedantatree.com]
 * 
 */
public class DefaultDataPaginator implements RedmineDataPaginator
{

	private static Log	LOGGER				= LogFactory.getLog( DefaultDataPaginator.class );

	/**
	 * It represents the type of RedmineBDO which this paginator will return for every user request.
	 */
	private Class		bdoContainerType;

	/**
	 * It tells us the record count which this pagination component should return to end user as one page. It will be
	 * set by user of API.
	 * 
	 * <p>
	 * Default page size for Redmine is 25 and maximum is 100. User can not set a page size bigger than 100, as that
	 * would force the paginator to cache the intermediate result with it, and hence defeat the purpose of fetching
	 * lesser records and to keep small amount of records in memory for a request.
	 * 
	 * <p>
	 * Alternatively, user can use 'getAllRecords' method to fetch all the records.
	 */
	private int			pageSize			= 25;

	/**
	 * It is the index of start page. Pagination component will fetch the data starting from this page. It will be set
	 * by user of API.
	 */
	private long		startRecordIndex;

	/**
	 * It will tell us the index of current page, which is being processed for user request. It will be set by component
	 * itself whenever user will fetch the records using getNextPageRecords method.
	 */
	private long		recordIndexToStartRetrieval;

	/**
	 * It is the total number of records this pagination component can return for current data request by user. For
	 * example, if user request for all Projects existed in Redmine, this component will keep the count of total number
	 * of projects exist in Redmine. However, total number of records returned in one 'getNextPageRecords' method are
	 * dependent on 'pageSize' attribute.
	 */
	private long		totalRecordCount	= Long.MIN_VALUE;

	/**
	 * Redmine URL for calling restlet service
	 */
	private String		requestURL;

	public DefaultDataPaginator( Class bdoContainerType, String requestURL )
	{
		this( bdoContainerType, requestURL, 0, 0 );
	}

	public DefaultDataPaginator( Class bdoContainerType, String requestURL, int pageSize )
	{
		this( bdoContainerType, requestURL, 0, pageSize );
	}

	public DefaultDataPaginator( Class bdoContainerType, String requestURL, long startRecordIndex, int pageSize )
	{
		this.bdoContainerType = bdoContainerType;
		this.requestURL = requestURL;
		this.startRecordIndex = startRecordIndex;
		this.recordIndexToStartRetrieval = startRecordIndex;
		this.pageSize = pageSize;

		// validate inputs
		validate();
	}

	private void validate()
	{
		RCRuntimeException rcrException = null;
		if( bdoContainerType == null )
		{
			rcrException = new RCRuntimeException( RCRuntimeException.ILLEGAL_ARGUMENT,
					"BDO Container type must not be null while initializing Data Paginator" );
		}
		if( !Utilities.isQualifiedString( requestURL ) )
		{
			rcrException = new RCRuntimeException( RCRuntimeException.ILLEGAL_ARGUMENT,
					"Request URL must be a qualified string while initializing Data Paginator" );
		}
		if( startRecordIndex < 0 )
		{
			rcrException = new RCRuntimeException( RCRuntimeException.ILLEGAL_ARGUMENT,
					"Start record Index must be equal to or greater than zero while initializing Data Paginator" );
		}
		if( pageSize < 0 )
		{
			rcrException = new RCRuntimeException( RCRuntimeException.ILLEGAL_ARGUMENT,
					"Page size must be equal to or greater than zero while initializing Data Paginator" );
		}
		if( rcrException != null )
		{
			LOGGER.error( rcrException );
			throw rcrException;
		}
	}

	private String prepareRequestURL()
	{
		return requestURL + "?offset=" + getRecordIndexToStartRetrieval() + "&limit=" + getPageSize();
	}

	// @Override
	public int getPageSize()
	{
		return this.pageSize;
	}

	// @Override
	public long getStartRecordIndex()
	{
		return this.startRecordIndex;
	}

	public long getRecordIndexToStartRetrieval()
	{
		return recordIndexToStartRetrieval;
	}

	// @Override
	public long getTotalRecordsCount() throws RCException
	{
		if( totalRecordCount == Long.MIN_VALUE )
		{
			throw new RCException(
					RCException.ILLEGAL_STATE,
					"Total Record count can be retrieved only after first call for records. It is not initialized before that. Hence first, fetch at least one page of records and then ask for Total Record Count" );
		}
		return totalRecordCount;
	}

	// @Override
	public boolean hasMoreRecords() throws RCException
	{
		LOGGER.trace( "hasMoreRecords: totalRecordCount[" + totalRecordCount + "] recordIndexToStartRetrieval["
				+ recordIndexToStartRetrieval + "]" );

		long pendingRecords = totalRecordCount;
		if( pendingRecords != Long.MIN_VALUE )
		{
			pendingRecords = pendingRecords - ( recordIndexToStartRetrieval );
		}
		return pendingRecords == Long.MIN_VALUE || pendingRecords > 0;
	}

	// @Override
	public List<RedmineBDO> nextPageRecords() throws RCException
	{
		LOGGER.trace( "nextPageRecords: preparedRequestURL[" + prepareRequestURL() + "]" );

		if( totalRecordCount != Long.MIN_VALUE && recordIndexToStartRetrieval >= totalRecordCount )
		{
			throw new RCException(
					RCException.ILLEGAL_STATE,
					"Total Records are already returned. Please use 'hasMoreRecords()' method before calling next page records. recordIndexToStartRetrieval["
							+ recordIndexToStartRetrieval + "] totalRecordCount[" + totalRecordCount + "]" );
		}

		RedmineBDOContainer redmineBDOContainer = (RedmineBDOContainer) RedmineConnector.getSharedInstance()
				.getRedmineObject( prepareRequestURL(), bdoContainerType );

		LOGGER.debug( "nextPageRecord-Container[" + redmineBDOContainer + "]" );

		List<RedmineBDO> objectsToReturn = redmineBDOContainer.getRedmineDataObjects();

		if( objectsToReturn == null )
		{
			totalRecordCount = 0;
			return null;
		}

		if( objectsToReturn.size() > getPageSize() )
		{
			throw new RCException( RCException.ILLEGAL_STATE, "size of list of objects returned is not correct. size["
					+ ( objectsToReturn == null ? "null" : "" + objectsToReturn.size() ) + "]" );
		}

		recordIndexToStartRetrieval += objectsToReturn.size();
		totalRecordCount = redmineBDOContainer.getTotalCount();

		LOGGER.debug( "objectsToReturn[" + objectsToReturn.size() + "] totalRecordCount[" + totalRecordCount
				+ "] recordIndexToStartRetrieval[" + recordIndexToStartRetrieval + "]" );

		return objectsToReturn;
	}

	// @Override
	public List<RedmineBDO> getAllRecords() throws RCException
	{

		RedmineDataPaginator issuePaginator = RedmineConnector.getSharedInstance().getIssues();
		List<RedmineBDO> bdos = new ArrayList<RedmineBDO>();
		while( issuePaginator.hasMoreRecords() )
		{
			List<RedmineBDO> issues = issuePaginator.nextPageRecords();
			bdos.addAll( issues );
		}

		return bdos;
	}

	public static void main( String[] args ) throws RCException
	{
		RedmineDataPaginator issuePaginator = RedmineConnector.getSharedInstance().getIssues();
		while( issuePaginator.hasMoreRecords() )
		{
			List<RedmineBDO> issues = issuePaginator.nextPageRecords();
			System.err.println( "############issues-size[" + ( issues != null ? issues.size() : -1 ) + "]" );
		}

	}

}

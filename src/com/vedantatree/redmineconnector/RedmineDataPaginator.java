package com.vedantatree.redmineconnector;

import java.util.List;

import com.vedantatree.redmineconnector.bdo.RedmineBDO;


/**
 * Object of this type will provide functionality to access the data from Redmine with pagination feature.
 * 
 * <p>
 * Redmine provide maximum 100 records in one call, where we can define the page size. Object of this class will utilize
 * this feature of Redmine to provide a user friendly Pagination Interface to end user.
 * 
 * Refer to http://www.redmine.org/projects/redmine/wiki/Rest_api for more details.
 * 
 * @author Mohit Gupta [mohit.gupta@vedantatree.com]
 */
public interface RedmineDataPaginator
{

	static int			REDMINE_MAX_PAGE_SIZE	= 100;

	/**
	 * It returns the number of records for one page.
	 * 
	 * One page is logical presentation of one call to Redmine. Redmine supports up to 100 records in one call. So page
	 * size can never be greater than 100. If any user set size greater than 100, system should raise error.
	 * 
	 * @return Number of records 'one page'/'one call to redmine' will return
	 */
	int getPageSize();

	/**
	 * This is the record offset for the start of iteration. It indicates that iteration over records should start from
	 * this given record number.
	 * 
	 * @return Record Number to start iteration with
	 */
	long getStartRecordIndex();

	/**
	 * Total number of records which will be returned by this paginator
	 * 
	 * @return Total number of records available with this paginator
	 * @throws
	 */
	long getTotalRecordsCount() throws RCException;

	/**
	 * It indicates whether there are more records to iterate or not
	 * 
	 * @return true if there are more records to iterate, false otherwise if all records are already iterated
	 */
	boolean hasMoreRecords() throws RCException;

	/**
	 * This method will fetch the next page of records from Redmine and will return these records. It considers page
	 * size, page offset and total number of records.
	 * 
	 * @return Next page of records, if exist otherwise throws exception
	 */
	List<RedmineBDO> nextPageRecords() throws RCException;

	/**
	 * This method can be used to get complete data through this RedmineDataPaginator for given criteria in one go. As
	 * Redmine has limit of '100' records to fetch per call, paginator will give repeated calls to Redmine to collect
	 * all records in memory first and then will return the whole set of data. Please consider, it may consume lot of
	 * memory and system may crash in case of large amount of data.
	 * 
	 * <p>
	 * Recommended approach is to use iteration using pages always.
	 * 
	 * @return Whole data available through this paginator
	 */
	List<RedmineBDO> getAllRecords() throws RCException;

}

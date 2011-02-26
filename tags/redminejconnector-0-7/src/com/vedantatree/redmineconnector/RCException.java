package com.vedantatree.redmineconnector;

/**
 * This object represents the Exception state when it is thrown from any operation. It is a customized Exception made to
 * accomodate some custom information like error code etc.
 * 
 * @author Mohit Gupta <mohit.gupta@vedantatree.com>
 */

public class RCException extends Exception
{

	public static int	IO_ERROR				= 0;
	public static int	RESOURCE_NOT_FOUND		= 1;
	public static int	DATA_CONVERSION_ERROR	= 2;
	public static int	ILLEGAL_ARGUMENT		= 3;
	public static int	ILLEGAL_STATE			= 4;
	public static int	OBJECT_NOT_FOUND		= 404;
	public static int	UNPROCESSABLE_ENTITY	= 422;

	/**
	 * The code represents the specified error state
	 */
	private int			errorCode;

	public RCException( int errorCode, String message )
	{
		super( message );
		this.errorCode = errorCode;
	}

	public RCException( int errorCode, Throwable th )
	{
		super( th );
		this.errorCode = errorCode;
	}

	public RCException( int errorCode, String message, Throwable th )
	{
		super( message, th );
		this.errorCode = errorCode;
	}

	public int getErrorCode()
	{
		return errorCode;
	}

}

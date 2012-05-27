package com.vedantatree.redmineconnector;

public class RCRuntimeException extends RuntimeException
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

	public RCRuntimeException( int errorCode, String message )
	{
		super( message );
		this.errorCode = errorCode;
	}

	public RCRuntimeException( int errorCode, Throwable th )
	{
		super( th );
		this.errorCode = errorCode;
	}

	public RCRuntimeException( int errorCode, String message, Throwable th )
	{
		super( message, th );
		this.errorCode = errorCode;
	}

	public int getErrorCode()
	{
		return errorCode;
	}

}

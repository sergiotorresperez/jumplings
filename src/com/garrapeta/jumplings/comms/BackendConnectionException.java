package com.garrapeta.jumplings.comms;

/**
 * Exceptions that may happen when communicating with the backend
 */
public class BackendConnectionException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Classification of errors
	 */
	public enum ErrorType{CLIENT_ERROR, IO_ERROR, HTTP_ERROR, PARSING_ERROR};
	
	private final ErrorType mErrorType;
	
	public BackendConnectionException(ErrorType errorType) {
		super();
		mErrorType = errorType;
	}

	/**
	 * @param errorType
	 * @param detailMessage
	 * @param throwable
	 */
	public BackendConnectionException(ErrorType errorType, String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		mErrorType = errorType;
	}

	/**
	 * @param errorType
	 * @param detailMessage
	 */
	public BackendConnectionException(ErrorType errorType, String detailMessage) {
		super(detailMessage);
		mErrorType = errorType;
	}

	/**
	 * @param errorType
	 * @param throwable
	 */
	public BackendConnectionException(ErrorType errorType, Throwable throwable) {
		super(throwable);
		mErrorType = errorType;
	}

	/**
	 * @return the type of error
	 */
	public ErrorType getErrorType() {
		return mErrorType;
	}
	

	
	

}

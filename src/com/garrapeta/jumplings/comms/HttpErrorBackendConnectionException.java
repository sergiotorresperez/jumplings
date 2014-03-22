package com.garrapeta.jumplings.comms;

public class HttpErrorBackendConnectionException extends BackendConnectionException {

    private static final long serialVersionUID = 978583767436899581L;

    private final int mHttpErrorCode;

    public HttpErrorBackendConnectionException(ErrorType errorType, int httpErrorCode) {
        super(errorType);
        mHttpErrorCode = httpErrorCode;
    }

    public HttpErrorBackendConnectionException(ErrorType errorType, String detailMessage, int httpErrorCode) {
        super(errorType, detailMessage);
        mHttpErrorCode = httpErrorCode;
    }

    public HttpErrorBackendConnectionException(ErrorType errorType, String detailMessage, Throwable throwable, int httpErrorCode) {
        super(errorType, detailMessage, throwable);
        mHttpErrorCode = httpErrorCode;
    }

    public HttpErrorBackendConnectionException(ErrorType errorType, Throwable throwable, int httpErrorCode) {
        super(errorType, throwable);
        mHttpErrorCode = httpErrorCode;
    }

    public int getHttpErrorCode() {
        return mHttpErrorCode;
    }

}

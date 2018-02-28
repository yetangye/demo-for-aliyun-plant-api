

package com.tld.company.http;

public class InvocationError extends Exception {
    private final ErrorType errorType;
    private int errorCode = 0;

    public InvocationError(ErrorType error) {
        this.errorType = error;
    }

    public InvocationError(ErrorType errorType, String exceptionMessage) {
        super(exceptionMessage);
        this.errorType = errorType;
    }

    public InvocationError(ErrorType errorType, int errorCode, String exceptionMessage) {
        super(exceptionMessage);
        this.errorType = errorType;
        this.errorCode = errorCode;
    }

    public InvocationError(ErrorType errorType, String exceptionMessage, Throwable reason) {
        super(exceptionMessage, reason);
        this.errorType = errorType;
    }

    public InvocationError(ErrorType errorType, Throwable cause) {
        super(cause);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return this.errorType;
    }

    public int getErrorCode() {
        return errorCode;
    }
}

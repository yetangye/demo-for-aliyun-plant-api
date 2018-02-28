
package com.tld.company.http;

public class Response<T> {
    private int Status;
    private String Message;
    private T Result;

    public Response() {
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public T getResult() {
        return Result;
    }

    public void setResult(T result) {
        Result = result;
    }
}

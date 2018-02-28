package com.tld.company.bean;

public class ResponseData {
    private int httpStatusCode;
    private String body;
    private Exception exception;

    //打印应答数据，方便开发调试
    public void print() {
        System.out.println("Response Http Status: " + httpStatusCode);
        System.out.println("Response Body       : " + ((body==null)?"":body));
        if(exception!=null) {
            System.out.println("Exception       : " + exception.toString());
        }
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}

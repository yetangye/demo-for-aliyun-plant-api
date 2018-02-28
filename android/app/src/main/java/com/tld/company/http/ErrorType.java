
package com.tld.company.http;

public enum ErrorType {
    NO_CONNECTION_ERROR("no network connection error"),//手机未联网
    NETWORK_ERROR("network error"),//发生网络异常
    HTTP_ERROR("http error"),//http返回非2xx返回码
    TIMEOUT_ERROR("timeout error"),//请求超时
    SERVER_ERROR("server error"),//服务器内部错误
    BUSINESS_ERROR("business error"),//业务逻辑错误
    CODE_ERROR("code error"),//带有错误码，表示特定错误
    PARSE_ERROR("parse response data error"),//不能解析返回数椐
    AUTH_FAILURE_ERROR("auth failure error"),
    INTERRUPTED_ERROR("Interrupted error"),
    BAD_REQUEST_ERROR("bad request error"),
    USER_CANCELED("user canceled"),//用户手动取消
    UNKNOWN_ERROR("unknown error");//未知错误

    public final String description;

    private ErrorType(String description) {
        this.description = description;
    }
}

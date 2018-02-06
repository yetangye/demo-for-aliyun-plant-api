package tld.your.company.common;

import lombok.Data;

@Data
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
}

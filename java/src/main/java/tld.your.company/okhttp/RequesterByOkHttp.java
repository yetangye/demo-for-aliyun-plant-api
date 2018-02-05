package tld.your.company.okhttp;

import okhttp3.*;
import tld.your.company.common.ResponseData;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RequesterByOkHttp {
    private static final int TIME_OUT = 30000;
    private String apiUrl;

    public RequesterByOkHttp(String url) {
        this.apiUrl = url;
    }

    public ResponseData postWwwFormUrlEncoded(String authorization, Map<String, String> bodyFields) {
        ResponseData responseData = new ResponseData();

        try {
            //组织请求格式
            FormBody.Builder postBuilder= new FormBody.Builder();

            for (Map.Entry<String, String> field:bodyFields.entrySet()) {
                postBuilder.add(field.getKey(), field.getValue());
            }
            FormBody formBody = postBuilder.build();

            //发起网络请求
            OkHttpClient okHttpClient = this.getRequestClient();
            Request request = new Request.Builder()
                    .addHeader("Authorization", authorization)
                    .url(apiUrl)
                    .post(formBody)
                    .build();

            Response httpResponse = okHttpClient.newCall(request).execute();
            String body = null;
            if(httpResponse.isSuccessful()) { //网络请求成功
                if (httpResponse.body() != null) {
                    body = httpResponse.body().string();
                }
            }
            else { //网络请求失败，应用要判断Status Code
            }

            responseData.setHttpStatusCode(httpResponse.code());
            responseData.setBody(body);

        } catch (Exception ex) { //网络异常
            responseData.setException(ex);
            ex.printStackTrace();
        }

        return responseData;
    }

    private OkHttpClient getRequestClient(){
        OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = httpBuilder
                .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .build();
        return okHttpClient;
    }
}

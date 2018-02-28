package com.tld.company.service;

import com.tld.company.bean.ResponseData;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AliyunPlantApiService {
    private static final String BASE_URL = "http://plantgw.nongbangzhu.cn/";
    private static final String APP_CODE = "替换为你购买的AppCode";

    private static CloseableHttpClient httpclient;

    /**
     * 植物花卉识别接口_v2 的请求示例
     */
    public static ResponseData recognize2(String img_base64){
        //构建请求格式
        String apiUrl = BASE_URL + "plant/recognize2";
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("img_base64", img_base64));
        return post(apiUrl, nvps);
    }

    /**
     * 植物花卉识别接口 的请求示例 （已不推荐使用，建议使用上面的植物花卉识别接口_v2）
     */
    public static ResponseData recognize(String img_base64){
        //构建请求格式
        String apiUrl = BASE_URL + "plant/recognize";
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("img_base64", img_base64));
        return post(apiUrl, nvps);
    }

    /**
     * 植物百科信息获取
     */
    public static ResponseData info(String code){
        //构建请求格式
        String apiUrl = BASE_URL + "plant/info";
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("code", code));
        return post(apiUrl, nvps);
    }

    /**
     * 常见杂草识别
     */
    public static ResponseData weed(String img_base64){
        //构建请求格式
        String apiUrl = BASE_URL + "plant/recognize_weed";
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("img_base64", img_base64));
        return post(apiUrl, nvps);
    }

    private static ResponseData post(String apiUrl,List<NameValuePair> nvps) {
        httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(apiUrl);
        httpPost.addHeader("Authorization", "APPCODE " + APP_CODE);
        CloseableHttpResponse response = null;
        ResponseData responseData = new ResponseData();
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            response = httpclient.execute(httpPost);
            int statusCode=response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                responseData.setHttpStatusCode(statusCode);
                responseData.setException(new RuntimeException("HttpClient,error status code :" + statusCode));
            }else{
                responseData.setHttpStatusCode(statusCode);
            }

            HttpEntity entity = response.getEntity();
            if (entity != null) { //打印应答数据部分
                responseData.setBody(EntityUtils.toString(entity));
            }

        } catch (IOException ex) {
            responseData.setException(ex);
        }
        finally {
            try {
                if(response!=null) response.close();
            }catch(Exception ex){

            }
        }

        return responseData;
    }

    public static void cancel(){
        if(httpclient!=null){
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

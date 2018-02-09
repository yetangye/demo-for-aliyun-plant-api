package tld.your.company.httpclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import tld.your.company.common.ResponseData;
import tld.your.company.common.Utils;

/**
 * Apache HttpClient 4+(在pom.xml内导入)，在JDK 1.8+运行本程序
 *
 * 【注意】这只是一个使用Apache HttpClient请求库的极简示例代码，
 * 缺少各种异常的完善处理以及和具体业务连接的逻辑，
 * 程序内的各种超时时间，要根据自己产品的网络环境和具体情况做判断和调整。
 *
 *  * 阿里云市场 智能植物识别（含花卉与杂草） API的购买网址：
 *    https://market.aliyun.com/products/57124001/cmapi018620.html#sku=yuncode1262000000
 *
 */

public class RequestDemoForAliyunPlantApiByHttpClient {

    private static final String BASE_URL = "http://plantgw.nongbangzhu.cn/";

    //购买后可得到AppCode，查看方法是在阿里云市场进入买家中心的管理控制台，
    //在已购买的服务列表内，找到 智能植物识别（含花卉与杂草），下方AppCode一行即是
    //相关截图请查看doc目录下的截图文件
    private static final String APP_CODE = "替换为您购买后得到的AppCode，获取方法请看这行代码上方的注释";

    public static void main(String args[]) {
        RequestDemoForAliyunPlantApiByHttpClient demo = new RequestDemoForAliyunPlantApiByHttpClient();

        demo.recognize2().print(); //演示 植物花卉识别接口_v2 的API调用
        demo.recognize().print();  //演示 植物花卉识别接口    的API调用
        demo.weed().print();       //演示 常见杂草识别       的API调用
        demo.info().print();       //演示 植物百科信息获取    的API调用
    }


    /**
     * 植物花卉识别接口_v2 的请求示例
     */
    public ResponseData recognize2(){
        //先准备数据
        String img_base64 = Utils.loadFileAsBase64("../pics/杜鹃.jpg");;

        //构建请求格式
        String apiUrl = BASE_URL + "plant/recognize2";
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("img_base64", img_base64));
        return this.post(apiUrl, nvps);
    }

    /**
     * 植物花卉识别接口 的请求示例 （已不推荐使用，建议使用上面的植物花卉识别接口_v2）
     */
    public ResponseData recognize(){
        //先准备数据
        String img_base64 = Utils.loadFileAsBase64("../pics/杜鹃.jpg");;

        //构建请求格式
        String apiUrl = BASE_URL + "plant/recognize";
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("img_base64", img_base64));
        return this.post(apiUrl, nvps);
    }

    /**
     * 植物百科信息获取
     */
    public ResponseData info(){
        //先准备数据
        String code = "CwZ0AVGtMcl5LJom";//这个植物代号是调用recognize2()时获得的InfoCode字段

        //构建请求格式
        String apiUrl = BASE_URL + "plant/info";
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("code", code));
        return this.post(apiUrl, nvps);
    }

    /**
     * 常见杂草识别
     */
    public ResponseData weed(){
        //先准备数据
        String img_base64 = Utils.loadFileAsBase64("../pics/狗尾草.jpg");;

        //构建请求格式
        String apiUrl = BASE_URL + "plant/recognize";
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("img_base64", img_base64));
        return this.post(apiUrl, nvps);
    }

    private ResponseData post(String apiUrl, List<NameValuePair> nvps) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(apiUrl);
        httpPost.addHeader("Authorization", "APPCODE " + APP_CODE);

        CloseableHttpResponse response = null;
        ResponseData responseData = new ResponseData();
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            response = httpclient.execute(httpPost);

            responseData.setHttpStatusCode(response.getStatusLine().getStatusCode());

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
}

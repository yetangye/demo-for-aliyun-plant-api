package tld.your.company.okhttp;

import tld.your.company.common.ResponseData;
import tld.your.company.common.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * OkHttp 3.9.1+（在pom.xml内导入），在JDK 1.8+运行本程序
 *
 * 【注意】这只是一个使用OkHttp请求库的极简示例代码，
 * 缺少各种异常的完善处理以及和具体业务连接的逻辑，
 * 程序内的各种超时时间，要根据自己产品的网络环境和具体情况做判断和调整。
 *
 * 阿里云市场 智能植物识别（含花卉与杂草） API的购买网址：
 *    https://market.aliyun.com/products/57124001/cmapi018620.html#sku=yuncode1262000000
 *
 */

public class RequestDemoForAliyunPlantApiByOkHttp {

    private static final String BASE_URL = "http://plantgw.nongbangzhu.cn/";

    //购买后可得到AppCode，查看方法是在阿里云市场进入买家中心的管理控制台，
    //在已购买的服务列表内，找到 智能植物识别（含花卉与杂草），下方AppCode一行即是
    //相关截图请查看doc目录下的截图文件
    private static final String APP_CODE = "替换为您购买后得到的AppCode，获取方法请看这行代码上方的注释";

    public static void main(String args[]) {
        RequestDemoForAliyunPlantApiByOkHttp demo = new RequestDemoForAliyunPlantApiByOkHttp();

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

        //组织POST BODY数据格式
        Map<String, String> bodyFields = new HashMap<String, String>();
        bodyFields.put("img_base64", img_base64);

        //发起网络请求，得到数据
        RequesterByOkHttp requester = new RequesterByOkHttp(BASE_URL + "plant/recognize2");
        ResponseData data = requester.postWwwFormUrlEncoded("APPCODE " + APP_CODE, bodyFields);
        return data;
    }

    /**
     * 植物花卉识别接口 的请求示例 （已不推荐使用，建议使用上面的植物花卉识别接口_v2）
     */
    public ResponseData recognize(){
        //先准备数据
        String img_base64 = Utils.loadFileAsBase64("../pics/杜鹃.jpg");;

        //组织POST BODY数据格式
        Map<String, String> bodyFields = new HashMap<String, String>();
        bodyFields.put("img_base64", img_base64);

        //发起网络请求，得到数据
        RequesterByOkHttp requester = new RequesterByOkHttp(BASE_URL + "plant/recognize");
        ResponseData data = requester.postWwwFormUrlEncoded("APPCODE " + APP_CODE, bodyFields);
        return data;
    }

    /**
     * 植物百科信息获取
     */
    public ResponseData info(){
        //先准备数据
        String code = "CwZ0AVGtMcl5LJom";

        //组织POST BODY数据格式
        Map<String, String> bodyFields = new HashMap<String, String>();
        bodyFields.put("code", code);

        //发起网络请求，得到数据
        RequesterByOkHttp requester = new RequesterByOkHttp(BASE_URL + "plant/info");
        ResponseData data = requester.postWwwFormUrlEncoded("APPCODE " + APP_CODE, bodyFields);
        return data;
    }

    /**
     * 常见杂草识别
     */
    public ResponseData weed(){
        //先准备数据
        String img_base64 = Utils.loadFileAsBase64("../pics/狗尾草.jpg");;

        //组织POST BODY数据格式
        Map<String, String> bodyFields = new HashMap<String, String>();
        bodyFields.put("img_base64", img_base64);

        //发起网络请求，得到数据
        RequesterByOkHttp requester = new RequesterByOkHttp(BASE_URL + "plant/recognize_weed");
        ResponseData data = requester.postWwwFormUrlEncoded("APPCODE " + APP_CODE, bodyFields);
        return data;
    }
}

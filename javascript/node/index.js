/**
 * 在NodeJS+运行本程序
 *
 * 【注意】这只是一个使用NodeJS请求的极简示例代码，
 * 缺少各种异常的完善处理以及和具体业务连接的逻辑，
 * 程序内的各种超时时间，要根据自己产品的网络环境和具体情况做判断和调整。
 *
 * 阿里云市场 智能植物识别（含花卉与杂草） API的购买网址：
 *    https://market.aliyun.com/products/57124001/cmapi018620.html#sku=yuncode1262000000
 *
 */

var fs = require('fs');
var request = require('request');


//购买后可得到AppCode，查看方法是在阿里云市场进入买家中心的管理控制台，
//在已购买的服务列表内，找到 智能植物识别（含花卉与杂草），下方AppCode一行即是
//相关截图请查看doc目录下的截图文件
var APP_CODE = "替换为您购买后得到的AppCode，获取方法请看这行代码上方的注释";
var BASE_URL = "http://plantgw.nongbangzhu.cn/";


function main() {
    recognize2();
    recognize();
    info();
    weed();
}

/**
 * 植物花卉识别接口_v2 的请求示例
 */
function recognize2() {
    //先准备数据
    var img_base64 = base64_encode('../../pics/杜鹃.jpg');
    var apiContextUrl = 'plant/recognize2';

    var formData = {
        img_base64: img_base64
    };

    post(apiContextUrl, formData);

}

/**
 * 植物花卉识别接口 的请求示例 （已不推荐使用，建议使用上面的植物花卉识别接口_v2）
 */
function recognize() {
    //先准备数据
    var img_base64 = base64_encode('../../pics/杜鹃.jpg');
    var apiContextUrl = 'plant/recognize';

    var formData = {
        img_base64: img_base64
    };

    post(apiContextUrl, formData);
}

/**
 * 植物百科信息获取
 */
function info() {
    //先准备数据
    var code = "CwZ0AVGtMcl5LJom"; //这个植物代号是调用 recognize2()接口可获得的InfoCode字段
    var apiContextUrl = 'plant/info';

    var formData = {
        code: code
    };

    post(apiContextUrl, formData);
}

/**
 * 常见杂草识别
 */
function weed() {
    //先准备数据
    var img_base64 = base64_encode('../../pics/狗尾草.jpg');
    var apiContextUrl = 'plant/recognize_weed';

    var formData = {
        img_base64: img_base64
    };

    post(apiContextUrl, formData);
}

function post(apiContextUrl, formData) {
    var options = {
        url: BASE_URL + apiContextUrl,
        headers: {
            'Authorization': 'APPCODE ' + APP_CODE
        },
        form: formData
    };

    request.post(options, function(err, httpResponse, body){
        if (err) {
            console.error('请求失败:', err);
        } else {
            console.log('请求成功：', body);
        }
    });
}

// function to encode file data to base64 encoded string
function base64_encode(file) {
    // read binary data
    var bitmap = fs.readFileSync(file);
    // convert binary data to base64 encoded string
    return new Buffer(bitmap).toString('base64');
}

main();
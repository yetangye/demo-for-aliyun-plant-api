//购买后可得到AppCode，查看方法是在阿里云市场进入买家中心的管理控制台，
//在已购买的服务列表内，找到 智能植物识别（含花卉与杂草），下方AppCode一行即是
//相关截图请查看doc目录下的截图文件
var APP_CODE = "替换为您购买后得到的AppCode，获取方法请看这行代码上方的注释";
var BASE_URL = "http://plantgw.nongbangzhu.cn/";

function post(apiContextUrl, formData) {

    $.ajax ({
        url: BASE_URL + apiContextUrl,
        type: "POST",
        headers: { 'Authorization': 'APPCODE ' + APP_CODE },
        data: formData,
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
        error:function(err){
            console.error('请求出错：' + err);
        },
        success:function(data){
            console.log('请求成功：' + JSON.stringify(data));
        },
        complete:function(){
            console.log("请求处理结束。");
        }
    });
}

function encodeImagetoBase64(element) {

    var file = element.files[0];
    var reader = new FileReader();

    reader.onloadend = function() {
        $(".link").attr("href",reader.result);
        $(".link").text(reader.result);
        };

    reader.readAsDataURL(file);
}
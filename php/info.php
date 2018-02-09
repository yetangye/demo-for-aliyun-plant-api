<html>
    <title>植物百科信息获取</title>
    <head><meta charset="UTF-8">
    </head>

<?php
if(!isset($_POST["submit"])) {
?>

    <body>
    <p>
        【注意】这只是一个使用PHP请求的极简示例代码，
        缺少各种异常的完善处理以及和具体业务连接的逻辑，
        程序内的各种超时时间，要根据自己产品的网络环境和具体情况做判断和调整。

        <a href="https://market.aliyun.com/products/57124001/cmapi018620.html#sku=yuncode1262000000">阿里云市场 智能植物识别（含花卉与杂草） API的购买网址</a>

    <form action="" method="POST" enctype="application/x-www-form-urlencoded">
        <input type="submit" name="submit" value="请求百科信息" onclick="return get_info()" />
	</form>

	</body>
<html>

<?php
}
else {
        /**
         *
         * 【注意】这只是一个使用PHP请求的极简示例代码，
         * 缺少各种异常的完善处理以及和具体业务连接的逻辑，
         * 程序内的各种超时时间，要根据自己产品的网络环境和具体情况做判断和调整。
         *
         * 阿里云市场 智能植物识别（含花卉与杂草） API的购买网址：
         *    https://market.aliyun.com/products/57124001/cmapi018620.html#sku=yuncode1262000000
         *
         */

        $host = "http://plantgw.nongbangzhu.cn";
        $path = "/plant/info";

        $method = "POST";
        //购买后可得到AppCode，查看方法是在阿里云市场进入买家中心的管理控制台，
        //在已购买的服务列表内，找到 智能植物识别（含花卉与杂草），下方AppCode一行即是
        //相关截图请查看doc目录下的截图文件
        $appcode = "替换为您购买后得到的AppCode，获取方法请看这行代码上方的注释";
        $headers = array();
        array_push($headers, "Authorization:APPCODE " . $appcode);
        array_push($headers, "Content-Type:application/x-www-form-urlencoded; charset=UTF-8");

        $plant_code = "CwZ0AVGtMcl5LJom";//这个植物代号是调用recognize2.php内接口可获得的InfoCode字段
        $bodys = "code=".$plant_code;
        $url = $host . $path;

        $curl = curl_init();
        curl_setopt($curl, CURLOPT_CUSTOMREQUEST, $method);
        curl_setopt($curl, CURLOPT_URL, $url);
        curl_setopt($curl, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($curl, CURLOPT_FAILONERROR, false);
        curl_setopt($curl, CURLOPT_RETURNTRANSFER, true);
        if (1 == strpos("$".$host, "https://"))
        {
            curl_setopt($curl, CURLOPT_SSL_VERIFYPEER, false);
            curl_setopt($curl, CURLOPT_SSL_VERIFYHOST, false);
        }
        curl_setopt($curl, CURLOPT_POSTFIELDS, $bodys);
        var_dump(curl_exec($curl));
    }
?>
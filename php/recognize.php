<html>
<title>植物花卉识别接口</title>
<head><meta charset="UTF-8">
</head>

<?php

if(!isset($_FILES['file'] )) {
?>

<body>
    <p>
        【注意】这只是一个使用PHP请求的极简示例代码，
        缺少各种异常的完善处理以及和具体业务连接的逻辑，
        程序内的各种超时时间，要根据自己产品的网络环境和具体情况做判断和调整。

        <a href="https://market.aliyun.com/products/57124001/cmapi018620.html#sku=yuncode1262000000">阿里云市场 智能植物识别（含花卉与杂草） API的购买网址</a>

        <p>请先选择一个.jpg格式的植物照片，然后点击发起请求按钮，应答结果可在浏览器控制台输出内查看到</p>


    <form action="" method="POST" enctype="multipart/form-data">
        <label for="file">上传植物花卉图片(jpg):<br></label>
        <input type="file" name="file" accept="image/jpeg" id="file" />
        <br />
        <input type="submit" name="submit" value="上传识别" onclick="return checkext()" />
	</form>

	<script>
        function checkext() {
            var file = document.getElementById("file").files[0];
            if(!/image\/\w+/.test(file.type)){
                    alert("请确保文件为图像类型");
                    return false;
            }
            else
                return true;
        }
	</script>

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

	$img = $_FILES['file'];
    $filename = $img['tmp_name'];
    $handle = fopen($filename, "rb"); //按二进制文件方式读取
    $contents = fread($handle, filesize($filename));
    fclose($handle);

	$img_base64 = urlencode(get_base64($contents));

    $host = "http://plantgw.nongbangzhu.cn";
	$path = "/plant/recognize";

	$method = "POST";
    //购买后可得到AppCode，查看方法是在阿里云市场进入买家中心的管理控制台，
    //在已购买的服务列表内，找到 智能植物识别（含花卉与杂草），下方AppCode一行即是
    //相关截图请查看doc目录下的截图文件
	$appcode = "替换为您购买后得到的AppCode，获取方法请看这行代码上方的注释";
	$headers = array();
	array_push($headers, "Authorization:APPCODE " . $appcode);
	array_push($headers, "Content-Type:application/x-www-form-urlencoded; charset=UTF-8");

	$bodys = "img_base64=".$img_base64;
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

function get_base64($bin_content) {
    $base64 = base64_encode($bin_content);
	echo "**************************<br>";
	echo "base64=".$base64;
	echo "<br>**************************<br>";
	echo "<img width='200' height='200' src='data:image/x-icon;base64,".$base64."'</>";
	echo "<br>************************************************<br>";
	return $base64;
}

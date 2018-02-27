# demo-for-aliyun-plant-api

阿里云市场智能植物识别（含花卉与杂草）API的开箱即用的示例代码（ <https://market.aliyun.com/products/57124001/cmapi018620.html#sku=yuncode1262000000> ）

## 关于已购APP CODE无法用于新发布接口的问题

  阿里云市场上，我们会不定期发布新的接口，对于新的接口，如果您使用该新接口发布前购买的APP CODE，可能会出现未授权问题，此时这个方法解决：
  登录api网关控制台：<https://apigateway.console.aliyun.com/>，
  点击【调用API】-【已购买API】，点开API详情，点击左下方授权按钮，找到您新的App，完成授权即可。

## 植物图片拍照要求

  照片尽快能拍成方形，在转换成BASE64前，要求分辨率尽可能调整到500x500，压缩比0.8，格式jpeg，尺寸小于512K。
  有些工具在将图片文件转化为BASE64编码时，会带上 "data:image/jpeg;base64," 前缀，请在调用接口时注意删除。

## Java语言示例

  请用支持pom.xml的IDE，比如IntelliJ IDEA，Eclipse打开或导入java目录。
  用JDK 1.8+运行。

### 用OkHttp库做请求

  参见代码文件：java/src/main/tld.your.company/okhttp/RequestDemoForAliyunPlantApiByOkHttp.java

### 用Apache HttpClient库做请求

  参见代码文件：java/src/main/tld.your.company/httpclient/RequestDemoForAliyunPlantApiByHttpClient.java

## JavaScript语言示例

### NodeJS版

  参见代码： javascript/node目录
  AppCode请在index.js内设置

### 浏览器内JS版

  参见代码： javascript/browser目录
  AppCode请在common.js内设置

## PHP

  参见代码： php目录
  AppCode请用每个php文件内的$appcode变量来设置

## Python（Python 2/Python 3皆可）

  编写中

## .NET/C#

  编写中

## Android版（带拍照）

  编写中

## iOS版

### Objective-C语言版

  参见代码：ios/objective-x目录内的Xcode工程。
  AppCode请在ViewController.m内设置。

### Swift语言版（带拍照）

  参见代码：ios/swift目录内的Xcode工程。
  AppCode请在ViewController.swift内设置。
  识别结果请从Xcode的日志输出中查看。

## 常见错误和解决方法

### 应答码 401

  用户AppCode输入不对或过期。如果是过期需重新购买。

### 应答码 403

  应答体为：
  {"Status":1002,"Message":"The parameter img_base64 is not base64 ","Result":[]}

  可能的原因包括：
  （1）转化后的BASE64编码带 "data:image/jpeg;base64," 前缀，但在请求时没有去掉
  （2）BASE64编码中的加号（+）没有被转化为 %2B
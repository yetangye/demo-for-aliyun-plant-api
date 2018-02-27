# -*- coding: utf-8 -*-

# 【注意】这只是一个使用requests请求库的极简示例代码，
# 缺少各种异常的完善处理以及和具体业务连接的逻辑，
# 程序内的各种超时时间，要根据自己产品的网络环境和具体情况做判断和调整。
#
# 阿里云市场
# 智能植物识别（含花卉与杂草） API的购买网址：
# https: // market.aliyun.com / products / 57124001 / cmapi018620.html  # sku=yuncode1262000000

import base64
import requests

url_host = "http://plantgw.nongbangzhu.cn"

# 购买后可得到AppCode，查看方法是在阿里云市场进入买家中心的管理控制台，
# 在已购买的服务列表内，找到
# 智能植物识别（含花卉与杂草），下方AppCode一行即是
# 相关截图请查看doc目录下的截图文件
app_code = 'replace with your AppCode' #这里替换为你购买的AppCode


# 植物花卉识别接口_v2的请求示例
def recognize2():
    url_path = '/plant/recognize2'

    with open("../pics/杜鹃.jpg", "rb") as image_file:
        img_base64 = base64.b64encode(image_file.read()).decode('ascii')
        body = {'img_base64': img_base64}

        headers = {'content-type': "application/x-www-form-urlencoded", 'authorization': "APPCODE " + app_code}
        response = requests.request("POST", url_host+url_path, data=body, headers=headers) # 默认utf-8
        print(response.text)

    return


# 植物花卉识别接口 的请求示例 （已不推荐使用，建议使用上面的植物花卉识别接口_v2）
def recognize():
    url_path = '/plant/recognize'

    with open("../pics/杜鹃.jpg", "rb") as image_file:
        img_base64 = base64.b64encode(image_file.read()).decode('ascii')
        body = {'img_base64': img_base64}

        headers = {'content-type': "application/x-www-form-urlencoded", 'authorization': "APPCODE " + app_code}
        response = requests.request("POST", url_host+url_path, data=body, headers=headers) # 默认utf-8
        print(response.text)

    return


# 植物百科信息获取
def info():
    url_path = '/plant/info'

    code = "CwZ0AVGtMcl5LJom" # 这个植物代号是调用recognize2()时获得的InfoCode字段
    body = {'code': code}
    headers = {'content-type': "application/x-www-form-urlencoded", 'authorization': "APPCODE " + app_code}
    response = requests.request("POST", url_host+url_path, data=body, headers=headers) # 默认utf-8
    print(response.text)

    return

# 常见杂草识别
def weed():
    url_path = '/plant/recognize_weed'

    with open("../pics/狗尾草.jpg", "rb") as image_file:
        img_base64 = base64.b64encode(image_file.read()).decode('ascii')
        body = {'img_base64': img_base64}

        headers = {'content-type': "application/x-www-form-urlencoded", 'authorization': "APPCODE " + app_code}
        response = requests.request("POST", url_host+url_path, data=body, headers=headers) # 默认utf-8
        print(response.text)

    return


if __name__ == '__main__':
    recognize2()
    info()
    recognize()
    weed()


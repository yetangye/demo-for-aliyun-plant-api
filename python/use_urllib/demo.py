import urllib
import urllib.request
import base64
from urllib.parse import urlencode, quote_plus, unquote

host = 'http://plantgw.nongbangzhu.cn'
path = '/plant/recognize2'
method = 'POST'
appcode = 'replace with your AppCode'
querys = ''
bodys = {}

url = host + path

img_base64=''
with open("../../pics/杜鹃.jpg", "rb") as image_file:
    img_base64 = base64.b64encode(image_file.read()).decode('ascii')

bodys['img_base64'] = img_base64
post_data = urlencode(bodys).encode() #默认utf-8

request = urllib.request.Request(url)
request.add_header('Authorization', 'APPCODE ' + appcode)
request.add_header('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8')

with urllib.request.urlopen(request, data=post_data) as f:
    resp = f.read()
    print(resp)

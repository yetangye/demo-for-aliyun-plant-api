using RestSharp;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace demofx
{
    class Requester
    {
        public Requester()
        {
        }

        public IRestResponse recognize2(string imgFilePath)
        {
            var appCode = "替换为您购买的App Code"; //替换为您购买的App Code，格式类似 a7b6888adf199f5d8f987777774219a7

            byte[] imageArray = System.IO.File.ReadAllBytes(imgFilePath);
            string base64Img = Convert.ToBase64String(imageArray);
            base64Img = System.Web.HttpUtility.UrlEncode(base64Img);

            var client = new RestClient("http://plantgw.nongbangzhu.cn/plant/recognize2");
            var request = new RestRequest(Method.POST);

            request.AddHeader("authorization", "APPCODE " + appCode);
            request.AddHeader("content-type", "application/x-www-form-urlencoded");
            request.AddParameter("application/x-www-form-urlencoded", "img_base64="+ base64Img, ParameterType.RequestBody);
            IRestResponse response = client.Execute(request);

            return response;

        }
    }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace demofx
{
    class Program
    {
        static void Main(string[] args)
        {
            var requester = new Requester();
            var response = requester.recognize2("../../../../../pics/杜鹃.jpg");
            Console.WriteLine("返回状态码：" + response.StatusCode);
            Console.WriteLine("返回结果：" + response.Content);
        }
    }
}

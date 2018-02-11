#import "ViewController.h"
#import "Base64.h"
@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    
    NSString *image_path = [[NSBundle mainBundle] pathForResource:@"杜鹃" ofType:@"jpg"];
    NSData   *image_data = [NSData dataWithContentsOfFile:image_path];
    NSString *image_base64 = [Base64 encode:image_data];
    
    //NSLog(@"image base64 : %@",sstr);
    NSLog(@"image base64 length: %lu",(unsigned long)image_base64.length);
    
    //购买后可得到AppCode，查看方法是在阿里云市场进入买家中心的管理控制台，
    //在已购买的服务列表内，找到 智能植物识别（含花卉与杂草），下方AppCode一行即是
    //相关截图请查看doc目录下的截图文件
    NSString *appcode = @"替换为您购买后得到的AppCode，获取方法请看这行代码上方的注释";
    
    NSString *host = @"http://plantgw.nongbangzhu.cn";
    NSString *path = @"/plant/recognize";
    NSString *method = @"POST";
    NSString *querys = @"";
    NSString *url = [NSString stringWithFormat:@"%@%@%@", host, path , querys];

    NSString *urlencoded_img_base64 = [image_base64 stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLPathAllowedCharacterSet]];
    //NSString的stringByAddingPercentEncodingWithAllowedCharacters不会把加号（+）转为%2B，有些奇怪，需要手动转一下
    urlencoded_img_base64 = [urlencoded_img_base64 stringByReplacingOccurrencesOfString:@"+" withString:@"%2B"];
    
    NSString *bodys = [NSString stringWithFormat:@"img_base64=%@", urlencoded_img_base64];
    
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:
                                    [NSURL URLWithString: url]  cachePolicy:1 timeoutInterval: 5];
    [request setHTTPMethod: method];
    [request addValue: [NSString stringWithFormat:@"APPCODE %@", appcode]   forHTTPHeaderField: @"Authorization"];
    [request addValue: @"application/x-www-form-urlencoded; charset=UTF-8"  forHTTPHeaderField: @"Content-Type"];

    NSData *data = [bodys dataUsingEncoding: NSUTF8StringEncoding];
    
    [request setHTTPBody: data];
    
    //NSLog(@"Request body %@", [[NSString alloc] initWithData:[request HTTPBody] encoding:NSUTF8StringEncoding]);
    
    NSURLSession *requestSession = [NSURLSession sessionWithConfiguration: [NSURLSessionConfiguration defaultSessionConfiguration]];
    NSURLSessionDataTask *task   =
        [requestSession dataTaskWithRequest:request
           completionHandler:^(NSData * _Nullable body, NSURLResponse * _Nullable response, NSError * _Nullable error) {
               NSHTTPURLResponse * httpResponse = (NSHTTPURLResponse *)response;
               
               //NSLog(@"Response object: %@", httpResponse);
               NSString *body_text = [[NSString alloc] initWithData:body encoding:NSUTF8StringEncoding];
               NSLog(@"Response body: %@" , body_text);
               
               if(httpResponse.statusCode == 200) {
                   NSLog(@"Requese finished");
               } else {
                   NSLog(@"Requese failed, status code: %ld", (long)httpResponse.statusCode);
               }
           }];
    [task resume];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


@end

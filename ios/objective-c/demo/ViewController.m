#import "ViewController.h"
#import "Base64.h"
@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    
    UIImage *img = [UIImage imageNamed:@"杜鹃.jpg"];
    NSData *imgdata = UIImageJPEGRepresentation(img, 1);
    NSString *pp = [[NSBundle mainBundle] pathForResource:@"杜鹃" ofType:@"jpg"];
    NSData * da = [NSData dataWithContentsOfFile:pp];
    NSString *sstr = [Base64 encode:da];
    
    //NSLog(@"image base64 : %@",sstr);
    NSLog(@"image base64 length: %d",sstr.length);
    NSString *appcode = @"68058bae27ad4b97ad3cb9ec534815d2";
    NSString *host = @"http://plantgw.nongbangzhu.cn";
    NSString *path = @"/plant/recognize";
    NSString *method = @"POST";
    NSString *querys = @"";
    NSString *url = [NSString stringWithFormat:@"%@%@%@", host, path , querys];
    NSString *bodys = [NSString stringWithFormat:@"img_base64=%@",sstr];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:
                                    [NSURL URLWithString: url]  cachePolicy:1  timeoutInterval:  5];
    request.HTTPMethod  =  method;
    [request addValue:  [NSString  stringWithFormat:@"APPCODE %@" ,  appcode]
   forHTTPHeaderField:  @"Authorization"];
    [request addValue: @"application/x-www-form-urlencoded; charset=UTF-8"
   forHTTPHeaderField: @"Content-Type"];
    NSData *data = [bodys dataUsingEncoding: NSUTF8StringEncoding];
    
    [request setHTTPBody: data];
    NSURLSession *requestSession = [NSURLSession sessionWithConfiguration:
                                    [NSURLSessionConfiguration defaultSessionConfiguration]];
    NSURLSessionDataTask *task = [requestSession dataTaskWithRequest:request
                                                   completionHandler:^(NSData * _Nullable body ,
                                                                       NSURLResponse * _Nullable response, NSError * _Nullable error) {
                                                       NSLog(@"Response object: %@" , response);
                                                       NSString *bodyString = [[NSString alloc] initWithData:body
                                                                                                    encoding:NSUTF8StringEncoding];
                                                       //打印应答中的body
                                                       NSLog(@"Response body: %@" , bodyString); }];
    [task resume];
    
}




- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


@end

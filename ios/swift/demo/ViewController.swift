import UIKit
import AVFoundation
let SCREENWIDTH = UIScreen.main.bounds.width
let SCREENHEIGHT = UIScreen.main.bounds.height
let kDeviceScale = UIScreen.main.scale
let imageWidthBiPingMu:CGFloat = (SCREENWIDTH*kDeviceScale)/720
let imageHeightBiPingMu:CGFloat = (SCREENHEIGHT*kDeviceScale)/1280
let qjwh =  SCREENWIDTH*(2/3)
class ViewController: UIViewController {

    @IBOutlet weak var bigBgView: UIView!
    @IBOutlet weak var imgView: UIImageView!
    @IBOutlet weak var bgView: UIView!
    @IBOutlet weak var qujingView: UIImageView!
    @IBOutlet weak var camerBtn: UIButton!
    @IBOutlet weak var quJTopCons: NSLayoutConstraint!
    @IBOutlet weak var quJLeftCons: NSLayoutConstraint!
    @IBOutlet weak var quJWidthCons: NSLayoutConstraint!
    @IBOutlet weak var quJHeightCons: NSLayoutConstraint!
    
    var device:AVCaptureDevice?//捕获设备，后置摄像头
    var input:AVCaptureDeviceInput?//输入设备
    var outPut:AVCaptureMetadataOutput?//当启动摄像头开始捕获输入
    var imageOutput:AVCaptureStillImageOutput?//输出图片
    private var session:AVCaptureSession = AVCaptureSession()//session结合输入输出，并开始启动捕获设备
    var previewLayer:AVCaptureVideoPreviewLayer?//图像预览层，实时显示捕获的图像
    var beginGestureScale:CGFloat?//开始的缩放比例
    var effectiveScale:CGFloat?//最后的缩放比例
    var canCamera:Bool = false//是否可以使用相机
    var image:UIImage?

    private let sessionQueue = DispatchQueue(label: "session queue", attributes: [], target: nil)
    private var isSessionRunning = false
    private var isFirst = true
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        customCamera()
        customUI()
        
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if isFirst {
            sessionQueue.async {
                self.session.startRunning()
                self.isSessionRunning = (self.session.isRunning)
            }
        }
    }
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        isFirst = false
    }
    
    func customCamera() {
        self.view.backgroundColor = UIColor.black
        
        //使用self.session 初始化预览层，self.session负责驱动input收集信息，layer负责把图像渲染显示
        self.previewLayer = AVCaptureVideoPreviewLayer.init(session: self.session)
        self.previewLayer?.frame = CGRect(x: 0, y: 0, width: SCREENWIDTH, height: SCREENHEIGHT)
        self.previewLayer?.videoGravity = AVLayerVideoGravity.resizeAspectFill
        
        sessionQueue.async { [weak self] in
            guard (self != nil) else {
                return
            }            //生成回话，用来结合输入输出
            self!.session.beginConfiguration()
            //使用设备初始化输入
            do {
                //使用AVMediaTypeVideo 指明self.device代表视频，默认使用后置摄像头
                
                if #available(iOS 10.0, *) {
                    if let dualCameraDevice = AVCaptureDevice.default(AVCaptureDevice.DeviceType.builtInDuoCamera, for: AVMediaType.video, position: AVCaptureDevice.Position.back){
                        self!.device = dualCameraDevice
                    }
                    else if let backCameraDevice = AVCaptureDevice.default(AVCaptureDevice.DeviceType.builtInWideAngleCamera, for: AVMediaType.video, position: AVCaptureDevice.Position.back){
                        self!.device = backCameraDevice
                    }
                } else {
                    self!.device = AVCaptureDevice.default(for: AVMediaType.video)
                }
                try self!.input = AVCaptureDeviceInput.init(device: self!.device!)
                
                if self!.session.canAddInput(self!.input!) == true {
                    self!.session.addInput(self!.input!)
                } else {
                    self!.session.commitConfiguration()
                    return
                }
                
                
            } catch {
                self!.session.commitConfiguration()
                return
            }
            
            //生成输出对象
            self!.outPut = AVCaptureMetadataOutput.init()
            self!.imageOutput = AVCaptureStillImageOutput.init()
            
            if ((self!.session.canSetSessionPreset(AVCaptureSession.Preset.hd1280x720)) == true) {
                self!.session.sessionPreset = AVCaptureSession.Preset.hd1280x720
            }
            
            if self!.session.canAddOutput(self!.imageOutput!) == true {
                self!.session.addOutput(self!.imageOutput!)
            }
            
            //开始启动
            self!.session.commitConfiguration()
        }
        
        self.bigBgView.layer.addSublayer(self.previewLayer!)
        self.bigBgView.clipsToBounds = true
        
        
        self.bgView.backgroundColor = UIColor.init(red: 0, green: 0, blue: 0, alpha: 150/255)
        let maskLayer:CAShapeLayer = CAShapeLayer.init()
        let maskRectTop = CGRect(x: 0, y: 0, width: SCREENWIDTH, height: SCREENHEIGHT/6)
        let maskRectLeft = CGRect(x: 0, y: SCREENHEIGHT/6, width: SCREENWIDTH/2-qjwh/2, height: qjwh)
        let maskRectBottom = CGRect(x: 0, y: SCREENHEIGHT/6+qjwh, width: SCREENWIDTH, height: SCREENHEIGHT-SCREENHEIGHT/6-qjwh)
        let maskRectRight = CGRect(x: SCREENWIDTH/2+qjwh/2, y: SCREENHEIGHT/6, width: SCREENWIDTH/2-qjwh/2, height: qjwh)
        
        let path:CGMutablePath = CGMutablePath()
        path.addRect(maskRectTop)
        path.addRect(maskRectLeft)
        path.addRect(maskRectBottom)
        path.addRect(maskRectRight)
        maskLayer.path = path
        self.bgView.layer.mask = maskLayer
        
        self.view.bringSubview(toFront: self.bigBgView)
        self.bigBgView.bringSubview(toFront: self.imgView)
        self.bigBgView.bringSubview(toFront: self.bgView)
        self.view.bringSubview(toFront: camerBtn)
        self.effectiveScale = 1.0
        self.beginGestureScale = 1.0
        
    }
    func customUI() {
        camerBtn.isHidden = false
        qujingView.layer.borderWidth = 2
        qujingView.layer.borderColor = UIColor.white.cgColor
        quJHeightCons.constant = SCREENWIDTH/3*2
        quJWidthCons.constant = SCREENWIDTH/3*2
        quJTopCons.constant = SCREENHEIGHT/6
        quJLeftCons.constant = SCREENWIDTH/6
        
    }
    @IBAction func btnAction(_ sender: Any) {
        self.shutterCamera()
    }
    func shutterCamera() {
        let videoConnection:AVCaptureConnection = (self.imageOutput?.connection(with: AVMediaType.video))!
        videoConnection.videoScaleAndCropFactor = self.effectiveScale!
        self.imageOutput?.captureStillImageAsynchronously(from: videoConnection, completionHandler: { (imageDataSampleBuffer, error) -> Void in
            guard imageDataSampleBuffer != nil else {
                return
            }
            let imageData = AVCaptureStillImageOutput.jpegStillImageNSDataRepresentation(imageDataSampleBuffer!)
            self.image = UIImage.init(data: imageData!)
            
            self.session.stopRunning()
            self.imgView.isHidden = false
            self.imgView.image = self.image
            //上传图片
            let cutImag = self.cutImageFromAlbum(self.image!)
            self.postImage(cutImag)
        })
    }
    
    func postImage(_ ima:UIImage) {
        
        let data = UIImageJPEGRepresentation(ima, 0.5)
        if let encodeBase64 = data?.base64EncodedString(options: .lineLength64Characters) {
            if let encodeBase64Url = encodeBase64.addingPercentEncoding(withAllowedCharacters: CharacterSet.urlPathAllowed) {
            let imgBase64 = encodeBase64Url.replacingOccurrences(of: "+", with: "%2B")
            
            let url = "http://plantgw.nongbangzhu.cn/plant/recognize"
                
            //购买后可得到AppCode，查看方法是在阿里云市场进入买家中心的管理控制台，
            //在已购买的服务列表内，找到 智能植物识别（含花卉与杂草），下方AppCode一行即是
            //相关截图请查看doc目录下的截图文件
            let appcode = "替换为您购买后得到的AppCode，获取方法请看这行代码上方的注释"
            
            var request = URLRequest.init(url: URL.init(string: url)!)
            request.httpMethod = "POST"
            var headers:[String:String] = [:]
            headers["Authorization"] = "APPCODE \(appcode)"
            headers["Content-Type"] = "application/x-www-form-urlencoded; charset=UTF-8"
            request.allHTTPHeaderFields = headers
            
            request.httpBody = ("img_base64="+imgBase64).data(using: String.Encoding.utf8)
            let config = URLSessionConfiguration.default
            let session = URLSession(configuration: config)
            let dataTask = session.dataTask(with: request, completionHandler: { (data:Data?, response:URLResponse?, error:Error?) in
                print("response object :")
                print(response ?? "nil")
                if data != nil {
                    let bodyString = String.init(data: data!, encoding: String.Encoding.utf8)
                    print("body string : \(bodyString ?? "nil")")
                }
                if let resp = response as? HTTPURLResponse {
                    if resp.statusCode == 200 {
                        print("request success")
                    } else {
                        print("request failed, statusCode = \(resp.statusCode)")
                    }
                }
            })
            dataTask.resume()
            }
        }
    }
    //裁剪取景框部分图片
    func cutImageFromAlbum(_ cutImage:UIImage)->UIImage {
        
        let subImageSize:CGSize = CGSize(width: SCREENWIDTH, height: SCREENHEIGHT);
        let subImageRect:CGRect = CGRect(x: (SCREENWIDTH/6/imageWidthBiPingMu) * kDeviceScale,y: (SCREENHEIGHT/6/imageHeightBiPingMu) * kDeviceScale, width: (qjwh/imageWidthBiPingMu) * kDeviceScale, height: (qjwh/imageWidthBiPingMu) * kDeviceScale)
        let imageRef:CGImage = cutImage.cgImage!;
        
        if let subImageCrop = imageRef.cropping(to: subImageRect) {
            let subImageRef:CGImage = subImageCrop as CGImage
            UIGraphicsBeginImageContextWithOptions(subImageSize,false,kDeviceScale);
            let context:CGContext = UIGraphicsGetCurrentContext()!
            context.draw(subImageRef, in: subImageRect)
            let subImage:UIImage = UIImage.init(cgImage: subImageRef)
            UIGraphicsEndImageContext()
            //返回裁剪的部分图像
            return subImage
        } else {
            return cutImage
        }
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}


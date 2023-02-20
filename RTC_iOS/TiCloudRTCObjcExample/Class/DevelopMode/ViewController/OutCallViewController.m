//
//  OutCallViewController.m
//  TiCloudRTCObjcExample
//
//  Created by 高延波 on 2022/8/23.
//

#import "OutCallViewController.h"

@interface OutCallViewController ()
{
    // 外呼号码
    UITextField *outCallNumberTF;
    // 外显号码
    UITextField *obClidNumberTF;
    // 随路数据
    UITextField *alongRoadTF;
    /// 主叫号码
    UITextField *callerNumberTF;
    // 外呼按钮
    UIButton *outCallBtn;
}

@end

@implementation OutCallViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [[SDKCloudEngine sharedInstancet] initSDK];
}
- (void)setupSubviews
{
    CGFloat Margin = 20;
    CGFloat MargimY = 50;
    CGFloat MarginH = 70;
    
    NSLog(@"OutCallViewController setupSubviews");
    outCallNumberTF = [[UITextField alloc] init];
    outCallNumberTF.frame = CGRectMake(Margin, MargimY, self.view.width - 2 *Margin, 40.f);
    outCallNumberTF.font = CHFont13;
    outCallNumberTF.textAlignment = NSTextAlignmentCenter;
    outCallNumberTF.placeholder = @"请输入客户号码";
    outCallNumberTF.layer.borderWidth = 1.0;
    outCallNumberTF.layer.borderColor = UIColor.grayColor.CGColor;
    outCallNumberTF.layer.cornerRadius = 6;
        
    obClidNumberTF = [[UITextField alloc] init];
    obClidNumberTF.frame = CGRectMake(Margin, MargimY + MarginH, self.view.width - 2 *Margin, 40.f);
    obClidNumberTF.font = CHFont13;
    obClidNumberTF.textAlignment = NSTextAlignmentCenter;
    obClidNumberTF.placeholder = @"请输入外显号码(可选)";
    obClidNumberTF.layer.borderWidth = 1.0;
    obClidNumberTF.layer.borderColor = UIColor.grayColor.CGColor;
    obClidNumberTF.layer.cornerRadius = 6;
    
    alongRoadTF = [[UITextField alloc] init];
    alongRoadTF.frame = CGRectMake(Margin, MargimY + 2*MarginH, self.view.width - 2 *Margin, 40.f);
    alongRoadTF.font = CHFont13;
    alongRoadTF.placeholder = @"请输入随路数据(可选)";
    alongRoadTF.textAlignment = NSTextAlignmentCenter;
    alongRoadTF.layer.borderWidth = 1.0;
    alongRoadTF.layer.borderColor = UIColor.grayColor.CGColor;
    alongRoadTF.layer.cornerRadius = 6;
    
    callerNumberTF = [[UITextField alloc] init];
    callerNumberTF.frame = CGRectMake(Margin, MargimY + 3 *MarginH, self.view.width - 2 *Margin, 40.f);
    callerNumberTF.font = CHFont13;
    callerNumberTF.placeholder = @"请输入回呼号（可选）";
    callerNumberTF.textAlignment = NSTextAlignmentCenter;
    callerNumberTF.layer.borderWidth = 1.0;
    callerNumberTF.layer.borderColor = UIColor.grayColor.CGColor;
    callerNumberTF.layer.cornerRadius = 6;
    
    outCallBtn = [[UIButton alloc] init];
    outCallBtn.frame = CGRectMake(Margin, self.view.height - kNavTop - kTabBarHeight - 100 - 45, self.view.width - 2 *Margin, 40.f);
    outCallBtn.backgroundColor = kHexColor(0x00865C);
    [outCallBtn setTitle:@"呼叫" forState:UIControlStateNormal];
    [outCallBtn addTarget:self action:@selector(didClickOutCallBtnAction) forControlEvents:UIControlEventTouchUpInside];
    outCallBtn.layer.cornerRadius = 6;
    
    [self.view addSubview:outCallBtn];
    [self.view addSubview:outCallNumberTF];
    [self.view addSubview:obClidNumberTF];
    [self.view addSubview:alongRoadTF];
    [self.view addSubview:callerNumberTF];
}


- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    [self.view endEditing:YES];
}
/*
- (void)setupSubviews
{
    NSLog(@"OutCallViewController setupSubviews");
    outCallNumberTF = [[UITextField alloc] init];
    outCallNumberTF.frame = CGRectMake(50.f, 50.f, 150.f, 40.f);
    outCallNumberTF.font = CHFont13;
    outCallNumberTF.textAlignment = NSTextAlignmentCenter;
    outCallNumberTF.placeholder = @"请输入客户号码";
    outCallNumberTF.layer.borderWidth = 1.0;
    outCallNumberTF.layer.borderColor = UIColor.grayColor.CGColor;
    outCallNumberTF.layer.cornerRadius = 6;
        
    obClidNumberTF = [[UITextField alloc] init];
    obClidNumberTF.frame = CGRectMake(50.f, 120.f, 150.f, 40.f);
    obClidNumberTF.font = CHFont13;
    obClidNumberTF.textAlignment = NSTextAlignmentCenter;
    obClidNumberTF.placeholder = @"请输入外显号码(可选)";
    obClidNumberTF.layer.borderWidth = 1.0;
    obClidNumberTF.layer.borderColor = UIColor.grayColor.CGColor;
    obClidNumberTF.layer.cornerRadius = 6;
    
    alongRoadTF = [[UITextField alloc] init];
    alongRoadTF.frame = CGRectMake(50.f, 190.f, 150.f, 40.f);
    alongRoadTF.font = CHFont13;
    alongRoadTF.placeholder = @"请输入随路数据(可选)";
    alongRoadTF.textAlignment = NSTextAlignmentCenter;
    alongRoadTF.layer.borderWidth = 1.0;
    alongRoadTF.layer.borderColor = UIColor.grayColor.CGColor;
    alongRoadTF.layer.cornerRadius = 6;
    
    outCallBtn = [[UIButton alloc] init];
    outCallBtn.frame = CGRectMake(250.f, 50.f, 60.f, 40.f);
    outCallBtn.backgroundColor = kHexColor(0x00865C);
    [outCallBtn setTitle:@"呼叫" forState:UIControlStateNormal];
    [outCallBtn addTarget:self action:@selector(didClickOutCallBtnAction) forControlEvents:UIControlEventTouchUpInside];
    outCallBtn.layer.cornerRadius = 6;
    
    [self.view addSubview:outCallBtn];
    [self.view addSubview:outCallNumberTF];
    [self.view addSubview:obClidNumberTF];
    [self.view addSubview:alongRoadTF];
}

 */
 
//呼叫按钮
- (void)didClickOutCallBtnAction
{
    NSLog(@"didClickOutCallBtnAction ");
    
    [self.view endEditing:YES];
    
    if (!outCallNumberTF.text.length)
    {
        [self showErrorView:@"请输入客户号码"];
        return;
    }
    else
    {
        BOOL isPhone = [AppConfig isValidatePhoneNumber:outCallNumberTF.text];
        
        if (!isPhone)
        {
            [self showErrorView:@"请输入正确的手机号码"];
            return;
        }
    }
    
    
    
    TiCloudRTCCallConfig * callConf = [[TiCloudRTCCallConfig alloc] init];
    callConf.tel = outCallNumberTF.text;
    callConf.type = TiCloudRtcScence_OUTCALLSCENCE;
    callConf.clid = obClidNumberTF.text;
    
    if (callerNumberTF.text.length)
    {
        BOOL isPhone = [AppConfig isValidatePhoneNumber:callerNumberTF.text];
        
        if (!isPhone)
        {
            [self showErrorView:@"请输入正确的主叫手机号码"];
            return;
        }
        else
        {
            callConf.callerNumber = callerNumberTF.text;
        }
    }
    
    CHWeakSelf
    [[SDKCloudEngine sharedInstancet].tiCloudEngine call:callConf success:^{
        NSLog(@"call ... success");
        [weakSelf showTelephoneView:callConf.tel];
    } error:^(TiCloudRtcErrCode nErrorCode, NSString * _Nonnull errorDes) {
        NSLog(@"call ... error = %@",errorDes);
    }];
}

@end

//
//  DialPageViewController.m
//  TiCloudRTCObjcExample
//
//  Created by 马迪 on 2022/9/6.
//

#import "DialPageViewController.h"
#import "SDKCloudEngine.h"
#import "TelephoneView.h"
#import "AppConfig.h"
#import "LoginViewController.h"
#import "LoginViewModel.h"
#import "LoginModel.h"

#define MarginH  30
#define ButtonWidth  72
#define LeftMargin  (self.view.width - 3 * ButtonWidth - 2 * MarginH)/2
#define MarginV  15

@interface DialPageViewController ()
<
    TiCloudRTCEventDelegate,
    TelephoneViewDelegate
>

// SDK引擎实例
@property(nonatomic, strong) SDKCloudEngine * SDKEngine;

@property(nonatomic, weak) UILabel *phoneLabel;

@property(nonatomic, weak) UIButton *callBtn;

@property(nonatomic, weak) UIButton *hiddenBtn;

@property (nonatomic, weak)TelephoneView *telephoneView;

@property (nonatomic, assign) BOOL isRinging;

@end

@implementation DialPageViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = UIColor.whiteColor;
    
    self.SDKEngine = [SDKCloudEngine sharedInstancet];
    [self.SDKEngine.tiCloudEngine setEventListener:self];

    UIButton *backBtn = [[UIButton alloc]initWithFrame:CGRectMake(20 , kStatusBarHeight + 5, 70, 42)];
    backBtn.imageView.contentMode = UIViewContentModeCenter;
    [backBtn setImage:[UIImage imageNamed:@"backPrevViewC"] forState:UIControlStateNormal];
    backBtn.imageEdgeInsets = UIEdgeInsetsMake(0, -15, 0, 10);
    [backBtn addTarget:self action:@selector(backBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:backBtn];
    
    [self setupUIView];
    
    // 数字键盘
    [self setupNumbersView];
}

- (void)backBtnClick
{
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)setupUIView
{
    UILabel *phoneLabel = [[UILabel alloc]initWithFrame:CGRectMake(20, 80, self.view.width - 40, 35)];
    phoneLabel.text = self.phoneNumber;
    phoneLabel.font = CHFont(36);
    phoneLabel.textColor = kHexColor(0x262626);
    phoneLabel.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:phoneLabel];
    self.phoneLabel = phoneLabel;

    UIButton *callBtn = [[UIButton alloc]initWithFrame:CGRectMake((self.view.width - ButtonWidth) * 0.5, self.view.height - 60 - ButtonWidth, ButtonWidth, ButtonWidth)];
    [callBtn setBackgroundImage:[UIImage imageNamed:@"Frame 1211"] forState:UIControlStateNormal];
    [callBtn addTarget:self action:@selector(callBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:callBtn];
    self.callBtn = callBtn;

    UIButton *hiddenBtn = [[UIButton alloc]initWithFrame:CGRectMake(self.view.width - LeftMargin - 42 - 10 , 0, 42, 42)];
    hiddenBtn.centerY = callBtn.centerY;
    hiddenBtn.imageView.contentMode = UIViewContentModeCenter;
    [hiddenBtn setImage:[UIImage imageNamed:@"面性／deldte-number"] forState:UIControlStateNormal];
    [hiddenBtn addTarget:self action:@selector(hiddenBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:hiddenBtn];
    self.hiddenBtn = hiddenBtn;
}

/// 创建数字键盘
- (void)setupNumbersView
{
    CGFloat bgHeight = 4 * ButtonWidth + 3 * MarginV;
    CGFloat topY = self.callBtn.top - 15 - bgHeight;
    
    for (int i = 0; i < 12; i++)
    {
        NSInteger tempX = i % 3;  // 小格子所在的列
        NSInteger tempY = i / 3; // 小格子所在的行
        
        UIButton *button = [[UIButton alloc]initWithFrame:CGRectMake(LeftMargin + tempX * (ButtonWidth +MarginH), topY + tempY * (ButtonWidth + MarginV), ButtonWidth, ButtonWidth)];
        [button setTitleColor:kHexColor(0x262626) forState:UIControlStateNormal];
        button.titleLabel.font = CHFont(36);
        [button setBackgroundColor:kHexColor(0xF0F0F0)];
        [button addTarget:self action:@selector(numbersButtonsClick:) forControlEvents:UIControlEventTouchUpInside];
        button.tag = i + 1;
        [self.view addSubview:button];
        button.layer.cornerRadius = ButtonWidth *0.5;
        
        if (i < 9)
        {
            [button setTitle:[NSString stringWithFormat:@"%d",i+1] forState:UIControlStateNormal];
        }
        else if (i == 9)
        {
            [button setTitle:@"*" forState:UIControlStateNormal];
        }
        else if (i == 10)
        {
            [button setTitle:@"0" forState:UIControlStateNormal];
        }
        else if (i == 11)
        {
            [button setTitle:@"#" forState:UIControlStateNormal];
        }
    }
}

- (void)callBtnClick
{
    if (![AppConfig isValidatePhoneNumber:self.phoneLabel.text])
    {
        [self showErrorView:@"请输入正确手机号"];
        return;
    }
    
    TiCloudRTCCallConfig * callConf = [[TiCloudRTCCallConfig alloc] init];
    callConf.tel = self.phoneLabel.text;
    callConf.type = OUTCALL_SCENCE;
    CHWeakSelf
    [self.SDKEngine.tiCloudEngine call:callConf success:^{
        NSLog(@"call ... success");
        [weakSelf showTelephoneView:callConf.tel];
    } error:^(TiCloudRtcErrCode nErrorCode, NSString * _Nonnull errorDes) {
        NSLog(@"call ... error = %@",errorDes);
    }];
}

- (void)hiddenBtnClick
{
    if ([self.phoneLabel.text length] > 0)
    {
        self.phoneLabel.text = [self.phoneLabel.text substringToIndex:[self.phoneLabel.text length] - 1];
    }
}

- (void)numbersButtonsClick:(UIButton *)button
{
    NSString *string = nil;
    if (button.tag < 10)
    {
        string = [NSString stringWithFormat:@"%ld",button.tag];
    }
    else if (button.tag == 10)
    {
        string = @"*";
    }
    else if (button.tag == 11)
    {
        string = @"0";
    }
    else if (button.tag == 12)
    {
        string = @"#";
    }
    
    [button setBackgroundColor:kHexColor(0xD9D9D9)];
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.25 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [button setBackgroundColor:kHexColor(0xF0F0F0)];
    });
    
    self.phoneLabel.text = [self.phoneLabel.text stringByAppendingString:string];
}

- (void)showTelephoneView:(NSString *)phoneNumber
{
    self.telephoneView = [TelephoneView sharedInstance];
    self.telephoneView.phoneNumber = phoneNumber;
    self.telephoneView.delegate = self;
    [self.telephoneView callingStart];
    
    [UIView animateWithDuration:0.25 animations:^{
        self.telephoneView.y = 0;
    }];
}

#pragma mark - TelephoneViewDelegate

-(void)hangupButtonClick
{
    [self.SDKEngine.tiCloudEngine hangup];
}

- (void)numberButtonsClick:(NSString *)number
{
    [self.SDKEngine.tiCloudEngine dtmf:number];
}

- (void)localAudioButtonClick:(BOOL)isSelect
{
    [self.SDKEngine.tiCloudEngine setEnableLocalAudio:isSelect];    
}


-(void)speakphoneButtonClick:(BOOL)isSelect
{
    [self.SDKEngine.tiCloudEngine setEnableSpeakerphone:isSelect];
    
    self.telephoneView.speakEnable = [self.SDKEngine.tiCloudEngine isSpeakerphoneEnabled];
}

#pragma mark -TiCloudRTCEventDelegate

/**
 * 引擎全局内错误信息事件回调
 *
 * @param errorCode         错误码
 * @param errorMessage   错误描述
 */
- (void)onError:(TiCloudRtcErrCode)errorCode errorMessage:(nonnull NSString *)errorMessage
{
    NSLog(@"用户端回调：errorMessage");
}

/**
 * 开始外呼
 *
 * @param requestUniqueId      为当前呼叫的唯一标识
 */
- (void)onCallingStart:(nonnull NSString *)requestUniqueId
{
    NSLog(@"用户端回调：onCallingStart");
}

/**
 * 播放铃声中
 *
 */
- (void)onRinging
{
    NSLog(@"用户端回调：onRinging");
    self.isRinging = YES;
}

/**
 * 外呼已取消
 *
 */
- (void)onCallCancelled
{
    NSLog(@"用户端回调：onCallCancelled");
    self.isRinging = NO;
}

/**
 * 呼叫被拒绝
 *
 */
- (void)onCallRefused
{
    NSLog(@"用户端回调：onCallRefused");
    [self showErrorView:@"对方已拒绝"];
    self.isRinging = NO;
}

/**
 * 呼叫中
 *
 */
- (void)onCalling
{
    NSLog(@"用户端回调：onCalling");
    [self.telephoneView callingReceive];
}

/**
 * 外呼结束
 * @param isPeerHangup     为 true 表示对方挂断，false 表示己方挂断
 *
 */
- (void)onCallingEnd:(BOOL)isPeerHangup
{
    NSLog(@"用户端回调：onCallingEnd");
    self.isRinging = NO;
    
    [self.telephoneView callingEnd];
    
    [UIView animateWithDuration:0.25 animations:^{
        self.telephoneView.y = kScreenHeight;
    }];
}

/**
 * 外呼失败
 *
 * @param errorCode         错误码
 * @param errorMessage   错误描述
 */
- (void)onCallFailure:(TiCloudRtcErrCode)errorCode errorMessage:(nonnull NSString *)errorMessage
{
    NSLog(@"用户端回调：onCallFailure");
}

/**
 *  token即将过期提醒
 *
 * @param accessToken       将在 10 分钟后过期
 */
- (void)onAccessTokenWillExpire:(nonnull NSString *)accessToken
{
    NSLog(@"用户端回调：onAccessTokenWillExpire");
    LoginViewModel *viewModel = [LoginViewModel sharedInstance];
    
    [viewModel requestData];
    
    @weakify(self);
    [RACObserve(viewModel, networkState) subscribeNext:^(NSNumber *networkState) {
        if (viewModel.networkState == NetworkStateSuccess) {
            LoginModel *model = [LoginModel loginModel];
            
            [self.SDKEngine.tiCloudEngine renewAccessToken:model.accessToken];
        }
    }];
}

/**
 *  token已过期
 *
 */
- (void)onAccessTokenHasExpired
{
    NSLog(@"用户端回调：onAccessTokenHasExpired");
    CHWeakSelf
    [[SDKCloudEngine sharedInstancet].tiCloudEngine destroyClient:^{
        
        [weakSelf.telephoneView callingEnd];
        
        [UIView animateWithDuration:0.25 animations:^{
            weakSelf.telephoneView.y = kScreenHeight;
        }];
        
        [weakSelf.presentingViewController.presentingViewController dismissViewControllerAnimated:YES completion:nil];
        
    } error:^(TiCloudRtcErrCode errorCode, NSString * _Nonnull errorMessage) {
        [weakSelf showErrorView:@"退出失败"];
    }];
}

@end

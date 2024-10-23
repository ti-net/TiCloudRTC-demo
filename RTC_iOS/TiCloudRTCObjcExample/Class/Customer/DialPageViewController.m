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


// (没有用到的控制器)
@interface DialPageViewController ()
<
    TiCloudRTCEventDelegate,
    TelephoneViewDelegate
>

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
    
    [[SDKCloudEngine sharedInstancet].tiCloudEngine setEventListener:self];

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
//    if (![AppConfig isValidatePhoneNumber:self.phoneLabel.text])
//    {
//        [self showErrorView:@"请输入正确手机号"];
//        return;
//    }
    
    TiCloudRTCCallConfig * callConf = [[TiCloudRTCCallConfig alloc] init];
    callConf.tel = self.phoneLabel.text;
    callConf.type = TiCloudRtcScence_OUTCALLSCENCE;
    CHWeakSelf
    [[SDKCloudEngine sharedInstancet].tiCloudEngine call:callConf success:^{
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

-(void)hangupProcess{
    self.isRinging = NO;
    
    [self.telephoneView callingEnd];
    
    [UIView animateWithDuration:0.25 animations:^{
        self.telephoneView.y = kScreenHeight;
    }];
}

#pragma mark - TelephoneViewDelegate

-(void)hangupButtonClick
{
    [[SDKCloudEngine sharedInstancet].tiCloudEngine hangup];
}

- (void)numberButtonsClick:(NSString *)number
{
    [[SDKCloudEngine sharedInstancet].tiCloudEngine dtmf:number];
}

- (void)localAudioButtonClick:(BOOL)isSelect
{
    [[SDKCloudEngine sharedInstancet].tiCloudEngine setEnableLocalAudio:isSelect];
}


-(void)speakphoneButtonClick:(BOOL)isSelect
{
    [[SDKCloudEngine sharedInstancet].tiCloudEngine setEnableSpeakerphone:isSelect];
    
    self.telephoneView.speakEnable = [[SDKCloudEngine sharedInstancet].tiCloudEngine isSpeakerphoneEnabled];
}

#pragma mark -TiCloudRTCEventDelegate

/**
 * 引擎全局内错误信息事件回调
 *
 * @param errorCode         错误码
 * @param errorMessage   错误描述
 */
- (void)onError:(TiCloudRtcErrCode)errorCode errorMessage:(nonnull NSString *)errorMessage{
    NSLog(@"用户端回调：onError errorCode=%d errorMessage=%@",(int)errorCode,errorMessage);
}

/**
 * 开始外呼
 *
 * @param requestUniqueId      为当前呼叫的唯一标识
 */
- (void)onCallingStart:(nonnull NSString *)requestUniqueId{
    NSLog(@"用户端回调：onCallingStart requestUniqueId=%@",requestUniqueId);
}

/**
 * 播放铃声中
 *
 */
- (void)onRinging{
    NSLog(@"用户端回调：onRinging");
    self.isRinging = YES;
}

/**
 * 外呼已取消
 *
 */
- (void)onCallCancelled{
    NSLog(@"用户端回调：onCallCancelled");
    self.isRinging = YES;
}

/**
 * 呼叫被拒绝
 *
 */
- (void)onCallRefused{
    [self showErrorView:@"对方已拒绝"];
    self.isRinging = NO;
}

/**
 * 呼叫中
 *
 */
- (void)onCalling{
    NSLog(@"用户端回调：onCalling");
    [self.telephoneView callingReceive];
}

/**
 * 调用 SDK hangup 接口引发挂断
 *
 */
- (void)onLocalHangup{
    NSLog(@"用户端回调：onLocalHangup");
    [self hangupProcess];
}

/**
 * 对方接起后挂断
 *
 */
- (void)onRemoteHangup{
    NSLog(@"用户端回调：onRemoteHangup");
    [self hangupProcess];
}

/**
 * 外呼结束
 * @param errorCode         错误码
 * @param errorMessage   错误描述
 * @param sipCode           sip错误码
 *
 */
- (void)onCallingEnd:(TiCloudRtcErrCode)errorCode errorMessage:(nonnull NSString *)errorMessage sipCode:(NSInteger)sipCode{
    NSLog(@"用户端回调：onCallingEnd errorCode=%d errorMessage=%@ sipCode=%d",(int)errorCode,errorMessage,(int)sipCode);
    [self hangupProcess];
}

/**
 * 外呼失败
 *
 * @param errorCode         错误码
 * @param errorMessage   错误描述
 */
- (void)onCallFailure:(TiCloudRtcErrCode)errorCode errorMessage:(nonnull NSString *)errorMessage{
    NSLog(@"用户端回调：onCallFailure errorCode=%d errorMessage=%@",(int)errorCode,errorMessage);
}

/**
 *  token即将过期提醒
 *
 * @param accessToken       将在 10 分钟后过期
 */
- (void)onAccessTokenWillExpire:(nonnull NSString *)accessToken{
    NSLog(@"用户端回调：onAccessTokenWillExpire");
    LoginViewModel *viewModel = [LoginViewModel sharedInstance];
    
    [viewModel requestData];
    
    @weakify(self);
    [RACObserve(viewModel, networkState) subscribeNext:^(NSNumber *networkState) {
        if (viewModel.networkState == NetworkStateSuccess) {
            LoginModel *model = [LoginModel loginModel];
            
            [[SDKCloudEngine sharedInstancet].tiCloudEngine renewAccessToken:model.accessToken];
        }
    }];
}

/**
 *  token已过期
 *
 */
- (void)onAccessTokenHasExpired{
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

/**
 * 拉取到的对方的数据流
 *
 * @param data 对方原始数据
 * @param size 数据大小
 */
- (void)receiveStreamDataFromOther:(void *_Nonnull)data size:(int)size{
//    NSLog(@"用户端回调：receiveStreamDataFromOther size=%d",size);
}

/**
 * 拉取的对端的音频格式
 *
 * @param samples 采样率
 * @param channels 声道数
 */
- (void)receiveStreamSample:(int)samples channels:(int)channels{
    NSLog(@"用户端回调：receiveStreamSample samples=%d channels=%d",samples,channels);
}


/**
 *  检测本端网络质量
 */
- (void)networkQuality:(TiCloudRtcNetwotkQuality)netwotkQuality{
    NSLog(@"用户端回调：networkQuality netwotkQuality=%d",(int)netwotkQuality);
}

/**
 *  检测全部用户网络质量
 *
 *  uid：用户id
 *
 *  txQuality：传输质量
 *  rxQuality：接受质量
 *
 */
- (void)networkQuality:(NSUInteger)uid txQuality:(AgoraNetworkQuality)txQuality rxQuality:(AgoraNetworkQuality)rxQuality{
    NSLog(@"用户端回调：networkQuality txQuality=%d rxQuality=%d",(int)txQuality,(int)rxQuality);
}

/**
* 接收到远端呼叫
*
*  @param fields 包含如下数据：
*
* customerNumber：主叫号码
* requestUniqueId：通话唯一标识
*
*/
- (void)onRemoteInvitationReceived:(nonnull NSDictionary *)fields{
    NSString * cbCustomerNumber = @"";
    NSString * cbRequestUniqueId = @"";
    if ([fields objectForKey:@"customerNumber"]) {
        cbCustomerNumber = fields[@"customerNumber"];
    }
    if ([fields objectForKey:@"requestUniqueId"]) {
        cbRequestUniqueId = fields[@"requestUniqueId"];
    }
    NSLog(@"用户端回调：onRemoteInvitationReceived customerNumber=%@ requestUniqueId=%@",cbCustomerNumber,cbRequestUniqueId);
}

/**
* 远端呼叫已拒绝
*
* @param fields 包含如下数据：
*
* customerNumber：主叫号码
* requestUniqueId：通话唯一标识
* isCalling：标识本次邀请是否因正处于通话中而自动拒绝 YES：是，NO：否
*
*/
- (void)onInvitationRefusedByLocal:(nonnull NSDictionary *)fields{
    NSString * cbCustomerNumber = @"";
    NSString * cbRequestUniqueId = @"";
    NSNumber * cbIsCalling = [NSNumber numberWithBool:NO];
    if ([fields objectForKey:@"customerNumber"]) {
        cbCustomerNumber = fields[@"customerNumber"];
    }
    if ([fields objectForKey:@"requestUniqueId"]) {
        cbRequestUniqueId = fields[@"requestUniqueId"];
    }
    if ([fields objectForKey:@"isCalling"]) {
        cbIsCalling = fields[@"isCalling"];
    }
    NSLog(@"用户端回调：onInvitationRefusedByLocal customerNumber=%@ requestUniqueId=%@ isCalling=%d",cbCustomerNumber,cbRequestUniqueId,cbIsCalling.boolValue);
}

/**
* 远端呼叫已取消
*
* @param fields 包含如下数据：
*
* customerNumber：主叫号码
* requestUniqueId：通话唯一标识
*
*/
- (void)onRemoteInvitationCanceled:(nonnull NSDictionary *)fields{
    NSString * cbCustomerNumber = @"";
    NSString * cbRequestUniqueId = @"";
    if ([fields objectForKey:@"customerNumber"]) {
        cbCustomerNumber = fields[@"customerNumber"];
    }
    if ([fields objectForKey:@"requestUniqueId"]) {
        cbRequestUniqueId = fields[@"requestUniqueId"];
    }
    NSLog(@"用户端回调：onRemoteInvitationCanceled customerNumber=%@ requestUniqueId=%@",cbCustomerNumber,cbRequestUniqueId);
}

/**
* 接收回呼失败
*
* errorCode：错误码
* errorMessage：通话唯一标识
*
*/
- (void)onRemoteInviteFailure:(TiCloudRtcErrCode)errorCode errorMessage:(nonnull NSString *)errorMessage{
    NSLog(@"用户端回调：onRemoteInviteFailure errorCode=%d errorMessage=%@",(int)errorCode,errorMessage);
}

/**
 * 当前 userId 在其他设备登录，此时引擎已销毁
 */
- (void)onRemoteLogin{
    NSLog(@"用户端回调：onRemoteLogin 当前 userId 在其他设备登录，此时引擎已销毁");
    [self hangupProcess];
}

/**
 * 本地监测无发送语音流时间间隔5秒时回调
 */
- (void)onLocalNoVoiceStreamSent{
    NSLog(@"用户端回调：onLocalNoVoiceStreamSent");
}

/**
 * SDK 根据平台配置对 userField 外呼参数里的特殊字符进行了移除处理
 *
 * @param removedCharList 被移除的特殊字符列表
 * @param srcUserField 原始的 userField
 * @param finalUserField 处理后的 userField
 * */
- (void)onUserFieldModifiedByConfig:(nonnull NSArray *)removedCharList srcUserField:(nonnull NSString *)srcUserField finalUserField:(nonnull NSString *)finalUserField{
    NSLog(@"用户端回调：onUserFieldModifiedByConfig srcUserField=%@ finalUserField=%@",srcUserField,finalUserField);
    for (NSString *object in removedCharList) {
        NSLog(@"用户端回调：removedCharList obj=%@ \n",object);
    }
}

/// 远程音频状态改变
- (void)onRemoteAudioStateChangedOfUid:(NSUInteger)uid state:(AgoraAudioRemoteState)state reason:(AgoraAudioRemoteReason)reason elapsed:(NSInteger)elapsed{
    NSLog(@"用户端回调：onRemoteAudioStateChangedOfUid uid=%d state=%d reason=%d elapsed=%d",(int)uid,(int)state,(int)reason,(int)elapsed);
}

/// 远程音频质量统计
- (void)onRemoteAudioStats:(AgoraRtcRemoteAudioStats * _Nonnull)stats{
    NSLog(@"用户端回调：onRemoteAudioStats stats=%@",stats.description);
}

/// 本地音频状态改变
- (void)onLocalAudioStateChanged:(AgoraAudioLocalState)state error:(AgoraAudioLocalError)error{
    NSLog(@"用户端回调：onLocalAudioStateChanged state=%d error=%d",(int)state,(int)error);
}

/// 本地音频质量统计
- (void)onLocalAudioStats:(AgoraRtcLocalAudioStats *_Nullable)stats{
    NSLog(@"用户端回调：onLocalAudioStats stats=%@",stats.description);
}

@end

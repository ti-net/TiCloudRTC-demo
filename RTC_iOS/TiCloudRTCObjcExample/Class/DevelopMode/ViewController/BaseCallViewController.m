//
//  BaseCallViewController.m
//  TiCloudRTCObjcExample
//
//  Created by 马迪 on 2022/9/5.
//

#import "BaseCallViewController.h"
#import "LoginModel.h"
#import "LoginViewController.h"
#import "LoginViewModel.h"

@interface BaseCallViewController ()

@property (nonatomic, assign) BOOL isRinging;

@end

@implementation BaseCallViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
//    self.SDKEngine = [SDKCloudEngine sharedInstancet];
    
    
}

- (void)setIsSelectPage:(BOOL)isSelectPage
{
    if (isSelectPage)
    {
        [[SDKCloudEngine sharedInstancet].tiCloudEngine setEventListener:self];
    }
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

- (void)speakphoneButtonClick:(BOOL)isSelect
{
    [[SDKCloudEngine sharedInstancet].tiCloudEngine setEnableSpeakerphone:isSelect];
    
    if (self.isRinging)
    {
        self.telephoneView.speakEnable = isSelect;
    }
}

#pragma mark -TiCloudRTCEventDelegate

/**
 * 引擎全局内错误信息事件回调
 *
 * @param errorCode         错误码
 * @param errorMessage   错误描述
 */
- (void)onError:(TiCloudRtcErrCode)errorCode errorMessage:(nonnull NSString *)errorMessage{
    NSLog(@"Base用户端回调：onError errorCode=%d errorMessage=%@",(int)errorCode,errorMessage);
}

/**
 * 开始外呼
 *
 * @param requestUniqueId      为当前呼叫的唯一标识
 */
- (void)onCallingStart:(nonnull NSString *)requestUniqueId{
    NSLog(@"Base用户端回调：onCallingStart requestUniqueId=%@",requestUniqueId);
}

/**
 * 播放铃声中
 *
 */
- (void)onRinging{
    NSLog(@"Base用户端回调：onRinging");
    self.isRinging = YES;
}

/**
 * 外呼已取消
 *
 */
- (void)onCallCancelled{
    NSLog(@"Base用户端回调：onCallCancelled");
    self.isRinging = NO;
}

/**
 * 呼叫被拒绝
 *
 */
- (void)onCallRefused{
    NSLog(@"Base用户端回调：onCallRefused");
    [self showErrorView:@"对方已拒绝"];
    self.isRinging = NO;
}

/**
 * 呼叫中
 *
 */
- (void)onCalling{
    NSLog(@"Base用户端回调：onCalling");
    [self.telephoneView callingReceive];
}

/**
 * 调用 SDK hangup 接口引发挂断
 *
 */
- (void)onLocalHangup{
    NSLog(@"Base用户端回调：onLocalHangup");
    [self hangupProcess];
}

/**
 * 对方接起后挂断
 *
 */
- (void)onRemoteHangup{
    NSLog(@"Base用户端回调：onRemoteHangup");
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
    NSLog(@"Base用户端回调：onCallingEnd errorCode=%d errorMessage=%@ sipCode=%d",(int)errorCode,errorMessage,(int)sipCode);
    [self hangupProcess];
}

/**
 * 外呼失败
 *
 * @param errorCode         错误码
 * @param errorMessage   错误描述
 */
- (void)onCallFailure:(TiCloudRtcErrCode)errorCode errorMessage:(nonnull NSString *)errorMessage{
    NSLog(@"Base用户端回调：onCallFailure errorCode=%d errorMessage=%@",(int)errorCode,errorMessage);
}

/**
 *  token即将过期提醒
 *
 * @param accessToken       将在 10 分钟后过期
 */
- (void)onAccessTokenWillExpire:(nonnull NSString *)accessToken{
    NSLog(@"Base用户端回调：onAccessTokenWillExpire");
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
    NSLog(@"Base用户端回调：onAccessTokenHasExpired");
    [self showErrorView:@"token已过期"];
    
    
    CHWeakSelf
    [[SDKCloudEngine sharedInstancet].tiCloudEngine destroyClient:^{
        
        [weakSelf.telephoneView callingEnd];
        
        [UIView animateWithDuration:0.25 animations:^{
            weakSelf.telephoneView.y = kScreenHeight;
        }];
        
        if (@available(iOS 13.0, *))
        {
            [weakSelf dismissViewControllerAnimated:YES completion:nil];
        }
        else
        {
            [AppDelegate shareAppDelegate].window.rootViewController = [AppDelegate shareAppDelegate].loginVC;
        }
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
//    NSLog(@"Base用户端回调：receiveStreamDataFromOther size=%d",size);
}

/**
 * 拉取的对端的音频格式
 *
 * @param samples 采样率
 * @param channels 声道数
 */
- (void)receiveStreamSample:(int)samples channels:(int)channels{
    NSLog(@"Base用户端回调：receiveStreamSample samples=%d channels=%d",samples,channels);
}


/**
 *  检测本端网络质量
 */
- (void)networkQuality:(TiCloudRtcNetwotkQuality)netwotkQuality{
    NSLog(@"Base用户端回调：networkQuality netwotkQuality=%d",(int)netwotkQuality);
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
    NSLog(@"Base用户端回调：networkQuality txQuality=%d rxQuality=%d",(int)txQuality,(int)rxQuality);
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
    NSLog(@"Base用户端回调：onRemoteInvitationReceived customerNumber=%@ requestUniqueId=%@",cbCustomerNumber,cbRequestUniqueId);
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
    NSLog(@"Base用户端回调：onInvitationRefusedByLocal customerNumber=%@ requestUniqueId=%@ isCalling=%d",cbCustomerNumber,cbRequestUniqueId,cbIsCalling.boolValue);
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
    NSLog(@"Base用户端回调：onRemoteInvitationCanceled customerNumber=%@ requestUniqueId=%@",cbCustomerNumber,cbRequestUniqueId);
}

/**
* 接收回呼失败
*
* errorCode：错误码
* errorMessage：通话唯一标识
*
*/
- (void)onRemoteInviteFailure:(TiCloudRtcErrCode)errorCode errorMessage:(nonnull NSString *)errorMessage{
    NSLog(@"Base用户端回调：onRemoteInviteFailure errorCode=%d errorMessage=%@",(int)errorCode,errorMessage);
}

/**
 * 当前 userId 在其他设备登录，此时引擎已销毁
 */
- (void)onRemoteLogin{
    NSLog(@"Base用户端回调：onRemoteLogin 当前 userId 在其他设备登录，此时引擎已销毁");
    [self hangupProcess];
}

/**
 * 本地监测无发送语音流时间间隔5秒时回调
 */
- (void)onLocalNoVoiceStreamSent{
    NSLog(@"Base用户端回调：onLocalNoVoiceStreamSent");
}

/**
 * SDK 根据平台配置对 userField 外呼参数里的特殊字符进行了移除处理
 *
 * @param removedCharList 被移除的特殊字符列表
 * @param srcUserField 原始的 userField
 * @param finalUserField 处理后的 userField
 * */
- (void)onUserFieldModifiedByConfig:(nonnull NSArray *)removedCharList srcUserField:(nonnull NSString *)srcUserField finalUserField:(nonnull NSString *)finalUserField{
    NSLog(@"Base用户端回调：onUserFieldModifiedByConfig srcUserField=%@ finalUserField=%@",srcUserField,finalUserField);
    for (NSString *object in removedCharList) {
        NSLog(@"Base用户端回调：removedCharList obj=%@ \n",object);
    }
}

/// 远程音频状态改变
- (void)onRemoteAudioStateChangedOfUid:(NSUInteger)uid state:(AgoraAudioRemoteState)state reason:(AgoraAudioRemoteReason)reason elapsed:(NSInteger)elapsed{
    NSLog(@"Base用户端回调：onRemoteAudioStateChangedOfUid uid=%d state=%d reason=%d elapsed=%d",(int)uid,(int)state,(int)reason,(int)elapsed);
}

/// 远程音频质量统计
- (void)onRemoteAudioStats:(AgoraRtcRemoteAudioStats * _Nonnull)stats{
    NSLog(@"Base用户端回调：onRemoteAudioStats stats=%@",stats.description);
}

/// 本地音频状态改变
- (void)onLocalAudioStateChanged:(AgoraAudioLocalState)state error:(AgoraAudioLocalError)error{
    NSLog(@"Base用户端回调：onLocalAudioStateChanged state=%d error=%d",(int)state,(int)error);
}

/// 本地音频质量统计
- (void)onLocalAudioStats:(AgoraRtcLocalAudioStats *_Nullable)stats{
    NSLog(@"Base用户端回调：onLocalAudioStats stats=%@",stats.description);
}

@end

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
    self.SDKEngine = [SDKCloudEngine sharedInstancet];
}

- (void)setIsSelectPage:(BOOL)isSelectPage
{
    if (isSelectPage)
    {
        [self.SDKEngine.tiCloudEngine setEventListener:self];
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

- (void)speakphoneButtonClick:(BOOL)isSelect
{
    [self.SDKEngine.tiCloudEngine setEnableSpeakerphone:isSelect];
    
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
//            LoginViewController *loginVC = [[LoginViewController alloc]init];
            
            [AppDelegate shareAppDelegate].window.rootViewController = [AppDelegate shareAppDelegate].loginVC;
        }
    } error:^(TiCloudRtcErrCode errorCode, NSString * _Nonnull errorMessage) {
        [weakSelf showErrorView:@"退出失败"];
    }];

}

@end

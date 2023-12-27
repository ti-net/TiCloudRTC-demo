//
//  SDKEngine.m
//  TiCloudRTCObjcExample
//
//  Created by 马迪 on 2022/9/6.
//

#import "SDKCloudEngine.h"
#import "LoginModel.h"

@implementation SDKCloudEngine

+ (instancetype)sharedInstancet
{
    static SDKCloudEngine *SDKEngine;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        SDKEngine  = [[SDKCloudEngine alloc] init];
    });
    return SDKEngine;
}

// 初始化SDK
-(void)initSDK
{
    LoginModel *model = [LoginModel loginModel];
    
    TiCloudRTCEngineConfig * config = [[TiCloudRTCEngineConfig alloc] init];
    config.rtcEndpoint = model.baseUrl;
    config.enterpriseId = model.enterpriseId;
    config.userId = model.userName;
    
    NSString *callerNumber = [[NSUserDefaults standardUserDefaults] valueForKey:kCallerNumber];
    if (callerNumber.length)
    {
        config.callerNumber = callerNumber;
    }
    
    config.accessToken = [LoginModel loginModel].accessToken;
    
    config.isDebug = YES;
    
//    config.accessToken = @"0018000560IABYE+rTIY9iTYx26YWf+tR5j+scRv5ebly71/DrD3Xk+kSjI/oQAPHcm+xBgS9lAQABANE9LmU=";
//    config.rtcEndpoint = @"https://rtc-api-test0.clink.cn";
//    config.enterpriseId = 8000560;
//    config.userId = @"1022";
    
    self.tiCloudEngine = [TiCloudRTCEngine createClient:config success:^(NSDictionary * _Nonnull data) {
        NSLog(@"createClient success..");
    } error:^(TiCloudRtcErrCode nErrorCode, NSString * _Nonnull errorDes) {
        NSLog(@"createClient error %@ ",errorDes);
    }];

    
    NSString *SDKVersion = [TiCloudRTCEngine getVersion];
    [[NSUserDefaults standardUserDefaults] setValue:SDKVersion forKey:kSDKVersonPath];
}


@end

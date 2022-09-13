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
        
        [SDKEngine initSDK];
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
    
    config.accessToken = [LoginModel loginModel].accessToken;
    self.tiCloudEngine = [TiCloudRTCEngine createClient:config success:^{
            NSLog(@"createClient success..");
    } error:^(TiCloudRtcErrCode nErrorCode, NSString * _Nonnull errorDes) {
        NSLog(@"createClient error %@ ",errorDes);
    }];
    
    NSString *SDKVersion = [TiCloudRTCEngine getVersion];
    [[NSUserDefaults standardUserDefaults] setValue:SDKVersion forKey:kSDKVersonPath];
}


@end

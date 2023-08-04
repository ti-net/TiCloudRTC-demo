//
//  TiCloudRTCEngine+ApiRequest.h
//  TiCloudRTC
//
//  Created by 高延波 on 2022/8/22.
//

#import "TiCloudRTCEngine.h"

NS_ASSUME_NONNULL_BEGIN

@interface TiCloudRTCEngine (ApiRequest)

/// 注册SDK接口
-(void)registerSdk:(void (^)(void))successBlock error:(void (^)(TiCloudRtcErrCode nErrorCode, NSString *errorDes))errorBlock;

/// 呼叫前获取呼叫信息
-(void)getCallInfo:(void (^)(void))successBlock error:(void (^)(TiCloudRtcErrCode nErrorCode, NSString *errorDes))errorBlock;

/// 获取新的RTC和RTM  Token
- (void)getAgoraNewToken:(void(^)(void))successBlock error:(void(^)(TiCloudRtcErrCode nErrorCode, NSString *errorDes))errorBlock;

/// 刷新AccessToken
- (void)renewAccessToken:(NSString *)accessToken success:(void(^)(NSInteger expires))successBlock error:(void(^)(TiCloudRtcErrCode nErrorCode, NSString *errorDes))errorBlock;

/// 上传日志
//- (void)uploadLogs:(NSString *)accessToken success:(void(^)(NSInteger expires))successBlock error:(void(^)(TiCloudRtcErrCode nErrorCode, NSString *errorDes))errorBlock;
- (void)uploadLogs:(NSArray *)logs;

@end

NS_ASSUME_NONNULL_END

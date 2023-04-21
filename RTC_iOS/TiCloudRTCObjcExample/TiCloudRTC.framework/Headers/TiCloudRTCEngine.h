//
//  TiCloudRTCEngine.h
//  TiCloudRTC
//
//  Created by 高延波 on 2022/8/19.
//

#import <TiCloudRTC/TiCloudRTCEventDelegate.h>

NS_ASSUME_NONNULL_BEGIN

@interface TiCloudRTCEngine : NSObject

/**
 * 创建引擎
 */
+ (TiCloudRTCEngine * _Nonnull)createClient:(TiCloudRTCEngineConfig *_Nonnull)config success:(void (^)( NSDictionary *data))successBlock error:(void (^)(TiCloudRtcErrCode nErrorCode, NSString *errorDes))errorBlock;

/**
 * 设置引擎事件的监听
 */
-(void)setEventListener:(nullable id<TiCloudRTCEventDelegate>)delegate;

/**
 * 外呼
 */
- (void)call:(TiCloudRTCCallConfig *_Nonnull)config success:(void (^)(void))successBlock error:(void (^)(TiCloudRtcErrCode nErrorCode, NSString *errorDes))errorBlock;

/**
 * 通话中发送按键信息
 */
- (void)dtmf:(NSString *)digits;

/**
 * 打开/关闭扬声器
 */
- (void)setEnableSpeakerphone:(BOOL)isEnable;

/**
 * 判断是否打开扬声器
 * YES: 扬声器已开启，语音会输出到扬声器
 * NO: 扬声器未开启，语音会输出到非扬声器（听筒，耳机等）
 */
- (BOOL)isSpeakerphoneEnabled;

/**
 * 打开/关闭本地音频 (静音/取消静音)
 */
- (void)setEnableLocalAudio:(BOOL)isEnable;

/**
 *  更新accessToken接口
 */
- (void)renewAccessToken:(NSString *)accessToken;

/**
 *  挂断接口
 */
- (void)hangup;

/**
 * 销毁客户端
 */
- (void)destroyClient:(void (^)(void))onSuccess error:(void (^)(TiCloudRtcErrCode errorCode, NSString *errorMessage))onFailed;

/**
 * 获取当前引擎的版本
 */
+ (NSString *_Nonnull)getVersion;

/**
 * 接受邀请
 */
- (void)acceptCall;

/**
 * 拒绝邀请
 */
- (void)refuseCall;


@end

NS_ASSUME_NONNULL_END

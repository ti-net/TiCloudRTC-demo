//
//  TiCloudRTCEventHandler.h
//  TiCloudRTC
//
//  Created by 高延波 on 2022/8/19.
//

#ifndef TiCloudRTCEventHandler_h
#define TiCloudRTCEventHandler_h

#import <TiCloudRTC/TiCloudRTCDefines.h>

@protocol TiCloudRTCEventDelegate <NSObject>

/**
 * 引擎全局内错误信息事件回调
 *
 * @param errorCode         错误码
 * @param errorMessage   错误描述
 */
- (void)onError:(TiCloudRtcErrCode)errorCode errorMessage:(nonnull NSString *)errorMessage;

/**
 * 开始外呼
 *
 * @param requestUniqueId      为当前呼叫的唯一标识
 */
- (void)onCallingStart:(nonnull NSString *)requestUniqueId;

/**
 * 播放铃声中
 *
 */
- (void)onRinging;

/**
 * 外呼已取消
 *
 */
- (void)onCallCancelled;

/**
 * 呼叫被拒绝
 *
 */
- (void)onCallRefused;

/**
 * 呼叫中
 *
 */
- (void)onCalling;

/**
 * 外呼结束
 * @param isPeerHangup     为 true 表示对方挂断，false 表示己方挂断
 *
 */
- (void)onCallingEnd:(BOOL)isPeerHangup;

/**
 * 外呼失败
 *
 * @param errorCode         错误码
 * @param errorMessage   错误描述
 */
- (void)onCallFailure:(TiCloudRtcErrCode)errorCode errorMessage:(nonnull NSString *)errorMessage;

/**
 *  token即将过期提醒
 *
 * @param accessToken       将在 10 分钟后过期
 */
- (void)onAccessTokenWillExpire:(nonnull NSString *)accessToken;

/**
 *  token已过期
 *
 */
- (void)onAccessTokenHasExpired;

@end

#endif /* TiCloudRTCEventHandler_h */

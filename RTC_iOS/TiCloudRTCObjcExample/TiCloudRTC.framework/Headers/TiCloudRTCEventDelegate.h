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

/**
 * 拉取到的对方的数据流
 *
 * @param data 对方原始数据
 * @param size 数据大小
 */
- (void)receiveStreamDataFromOther:(void *_Nonnull)data size:(int)size;

/**
 * 拉取的对端的音频格式
 *
 * @param samples 采样率
 * @param channels 声道数
 */
- (void)receiveStreamSample:(int)samples channels:(int)channels;


/**
 *  检测本端网络质量
 */
- (void)networkQuality:(TiCloudRtcNetwotkQuality)netwotkQuality;

/**
* 接收到远端呼叫
*
*  @param fields 包含如下数据：
*
* customerNumber：主叫号码
* requestUniqueId：通话唯一标识
*
*/
- (void)onRemoteInvitationReceived:(nonnull NSDictionary *)fields;

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
- (void)onInvitationRefusedByLocal:(nonnull NSDictionary *)fields;

/**
* 远端呼叫已取消
*
* @param fields 包含如下数据：
*
* customerNumber：主叫号码
* requestUniqueId：通话唯一标识
*
*/
- (void)onRemoteInvitationCanceled:(nonnull NSDictionary *)fields;

/**
* 接收回呼失败
*
* @param fields 包含如下数据：
*
* customerNumber：主叫号码
* requestUniqueId：通话唯一标识
*
*/
- (void)onRemoteInvitationFailure:(nonnull NSDictionary *)fields;

/**
 * 当前 userId 在其他设备登录，此时引擎已销毁
 */
- (void)onRemoteLogin;

@end

#endif /* TiCloudRTCEventHandler_h */

//
//  TiCloudRTCDefines.h
//  TiCloudRTC
//
//  Created by 高延波 on 2022/8/19.
//

#ifndef TiCloudRTCDefines_h
#define TiCloudRTCDefines_h

#import <Foundation/Foundation.h>

#if TARGET_OS_IPHONE
#import <UIKit/UIKit.h>
#elif TARGET_OS_OSX
#import <AppKit/AppKit.h>
#endif

typedef NS_ENUM(NSInteger, TiCloudRtcErrCode)
{
    // 初始化及登录错误码
    ERR_NET_ERROR                         = 10001,   // "网络错误"
    ERR_ENGINE_NOT_INITIALIZE             = 10002,   // "引擎未初始化"
    ERR_INNER_MESSAGE_SEND_FIELD          = 10003,   // "内部信令发送失败"
    ERR_INNER_ERROR                       = 10004,   // "内部错误",通常因为引擎处于初始化中或销毁中导致当前操作执行失败
    ERR_RTM_ERROR                         = 10005,   // "内部信令错误"
    ERR_RTC_ERROR                         = 10006,   // "内部 RTC 错误"
    ERR_TOKEN_EXPIRED                     = 10007,   // "token 已过期"
    ERR_TOKEN_INVALID                     = 10008,   // "token 无效"
    ERR_TOKEN_PERIOD_TOO_SHORT            = 10009,   // "token 有效期过短，有效期最小为 10 分钟"
    ERR_AUTH_FAILED                       = 10010,   // 鉴权失败,userId 与 accessToken 不匹配
    ERR_REQUEST_TOO_FREQUENT              = 10011,   // 请求过于频繁
    ERR_REGISTRATION_CONCURRENCY_EXCEEDED = 10012,   // 注册并发数超限制
    ERR_ENTERPRISE_NOT_EXIST              = 10013,   // 企业不存在
    
    // 外呼相关错误码
    ERR_CALL_FAILED_PARAMS_INCORRECT      = 11001,   // "外呼失败：参数不正确"
    ERR_CALL_FAILED_CALL_REPEAT           = 11002,   // "外呼失败：重复呼叫"
    ERR_CALL_FAILED_REMOTE_OFFLINE        = 11003,   // "外呼失败：对端异常掉线"
    ERR_CALL_FAILED_NET_ERROR             = 11004,   // "外呼失败：网络异常"
    ERR_CALL_FAILED_RTM_ERROR             = 11005,   // "外呼失败：内部信令错误"
    ERR_CALL_HOTLINE_NOT_EXIST            = 11006,   // "热线号码未配置"
    
    // SDK 初始化时参数校验错误码
    ERR_RTC_ENDPOINT_FORMAT_INCORRECT     = 13001,   // "RTC 平台地址格式不正确"
    ERR_ENTERPRISE_ID_FORMAT_INCORRECT    = 13002,   // "企业 ID 格式不正确"
    ERR_USER_ID_FORMAT_INCORRECT          = 13003,   // "用户 ID 格式不正确"
    ERR_ACCESS_TOKEN_FORMAT_INCORRECT     = 13004,   // "accessToken 格式不正确"
};

/// 网络状态
typedef NS_ENUM(NSInteger, TiCloudRtcNetwotkQuality)
{
    TiCloudRtcNetwotkQuality_Unknown,      // 网络质量未知
    TiCloudRtcNetwotkQuality_Good ,      // 网络质量较好
    TiCloudRtcNetwotkQuality_General ,    // 网络质量一般
    TiCloudRtcNetwotkQuality_Bad ,    // 网络质量较差
};

/// 呼叫场景
typedef NS_ENUM(NSInteger, TiCloudRtcScence)
{
    TiCloudRtcScence_AGENTSCENCE = 1,      // 客服场景
    TiCloudRtcScence_OUTCALLSCENCE = 6,    // 外呼场景
};

@interface TiCloudRTCEngineConfig : NSObject

// 平台地址
@property(nonatomic, copy, nonnull) NSString *rtcEndpoint;

// 企业ID
@property(nonatomic, assign) NSInteger enterpriseId;

// 用户ID
@property(nonatomic, copy, nonnull) NSString *userId;

// 登录接口返回的 accessToken
@property(nonatomic, copy, nonnull) NSString *accessToken;

// 预留扩展参数
@property(nonatomic, copy, nullable) NSDictionary<NSString *, NSString *> *advancedConnectConfig;

// 调试标识，true 为输出调试信息，false 为不输出调试信息
@property(nonatomic, assign) BOOL isDebug;

// 主叫号码（用于回呼）
@property(nonatomic, copy, nullable) NSString *callerNumber;

+ (instancetype _Nonnull)defaultConfig;

@end

@interface TiCloudRTCCallConfig : NSObject

// 外呼号码
@property(nonatomic, copy, nonnull) NSString *tel;

// 外显号码
@property(nonatomic, copy, nullable) NSString *clid;

// 指定外显区号
@property(nonatomic, copy, nullable) NSString *obClidAreaCode;

/// 指定外显号码池，使用此参数时obClid参数无效
@property (nonatomic, copy, nullable) NSString *obClidGroup;

// 坐席号
@property(nonatomic, copy, nullable) NSString *cno;

// 通话唯一标识 不填则有sdk内部生成
@property(nonatomic, copy, nullable) NSString *requestUniqueId;

// 自定义字段
@property(nonatomic, copy, nullable) NSString *userField;

// 呼叫场景 1:客服场景 6：外呼场景
@property(nonatomic, assign) TiCloudRtcScence type;

// 主叫号码（用于回呼,若不为空可覆盖初始化时传入的值）
@property(nonatomic, copy, nullable) NSString *callerNumber;

// 预留扩展参数
@property(nonatomic, copy, nullable) NSDictionary<NSString *, NSString *> *advancedConnectConfig;

+ (instancetype _Nonnull)defaultConfig;

@end

#endif /* TiCloudRTCDefines_h */

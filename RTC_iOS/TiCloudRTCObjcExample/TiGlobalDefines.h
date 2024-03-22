//
//  TiGlobalDefines.h
//  TiCloudRTCObjcExample
//
//  Created by 高延波 on 2022/8/22.
//

#ifndef TiGlobalDefines_h
#define TiGlobalDefines_h

#import <UIKit/UIKit.h>

#define CHWeakSelf                  __weak __typeof(self) weakSelf = self;

#ifdef DEBUG
#define NSLog(...) NSLog(__VA_ARGS__)
#define debugMethod() NSLog(@"%s", __func__)
#else
#define NSLog(...)
#define debugMethod()
#endif

#pragma mark - UI适配

#define IphoneX ({ \
BOOL ipX = NO; \
if (@available(iOS 11.0, *)) { \
    UIWindow *window = [[UIApplication sharedApplication].windows firstObject]; \
    ipX = window.safeAreaInsets.bottom > 0; \
} \
  ipX; \
})

#define IS_IPAD (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad)
#define IS_IPHONE (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone)

#define SCREEN_WIDTH ([[UIScreen mainScreen] bounds].size.width)
#define SCREEN_HEIGHT ([[UIScreen mainScreen] bounds].size.height)
#define SCREEN_MAX_LENGTH (MAX(SCREEN_WIDTH, SCREEN_HEIGHT))
#define SCREEN_MIN_LENGTH (MIN(SCREEN_WIDTH, SCREEN_HEIGHT))
#define SCALE(x)  x*self.scale
#define HEIGHTSCALE(x)  (IS_IPHONE_X || IS_IPHONE_XsMax ? x*self.heightScale : x*self.scale)


#define IS_IPHONE_4_OR_LESS (IS_IPHONE && SCREEN_MAX_LENGTH < 568.0)
#define IS_IPHONE_5 (IS_IPHONE && SCREEN_MAX_LENGTH == 568.0)

#define IS_IPHONE_6 (IS_IPHONE && SCREEN_MAX_LENGTH == 667.0)
#define IS_IPHONE_6P (IS_IPHONE && SCREEN_MAX_LENGTH == 736.0)
#define IS_IPHONE_X (IS_IPHONE && SCREEN_MAX_LENGTH == 812)
#define IS_IPHONE_XsMax (IS_IPHONE && SCREEN_MAX_LENGTH == 896)

#define IS_IPHONE_BangsScreen (IS_IPHONE && SCREEN_MAX_LENGTH >= 780)

#define kNavTop (IS_IPHONE_BangsScreen ? 88.f : 64.f)
#define kBottomBarHeight (IS_IPHONE_BangsScreen ? 34.0f : 0)
#define kStatusBarHeight    (IS_IPHONE_BangsScreen ? 44.f : 20.f)

#define kTabBarHeight 49.f

#define kWindowWidth    [UIScreen mainScreen].bounds.size.width
#define kWindowHeight   [[UIScreen mainScreen] bounds].size.height
#define kMainWindow      [[[UIApplication sharedApplication] delegate] window]


//初始化xib文件
#define kInitXibName(nibName) [[UINib nibWithNibName:nibName bundle:nil] instantiateWithOwner:nil options:nil].firstObject
/// 初始化Class的cell
#define kInitClassName(className) [[NSClassFromString(className) alloc] initWithStyle:(UITableViewCellStyleDefault) reuseIdentifier:className]


//RGB进制颜色值
#define kRGBColor(r,g,b)  [UIColor colorWithRed:(r)/255.0f green:(g)/255.0f blue:(b)/255.0f alpha:1]
#define kRGBAColor(r,g,b,a)  [UIColor colorWithRed:(r)/255.0f green:(g)/255.0f blue:(b)/255.0f alpha:(a)]

//16进制颜色值，注意：在使用的时候hexValue写成：0x000000
#define kHexColor(hexValue) [UIColor colorWithRed:((float)(((hexValue) & 0xFF0000) >> 16))/255.0 green:((float)(((hexValue) & 0xFF00) >> 8))/255.0 blue:((float)((hexValue) & 0xFF))/255.0 alpha:1.0]
#define kHexAColor(hexValue,a) [UIColor colorWithRed:((float)(((hexValue) & 0xFF0000) >> 16))/255.0 green:((float)(((hexValue) & 0xFF00) >> 8))/255.0 blue:((float)((hexValue) & 0xFF))/255.0 alpha:a]
/// AppDelegate
#define TRSharedAppDelegate ((AppDelegate *)[UIApplication sharedApplication].delegate)

/// UIFont
#define CHFont(size) [UIFont systemFontOfSize:size]
#define CHFont18 CHFont(18)
#define CHFont17 CHFont(17)
#define CHFont16 CHFont(16)
#define CHFont15 CHFont(15)
#define CHFont14 CHFont(14)
#define CHFont13 CHFont(13)
#define CHFont12 CHFont(12)
#define CHFont10 CHFont(10)
#define CHFont8 CHFont(8)


static NSString * const kTRLocalizeLanguageCodeKey = @"LangCode";
static NSString * const kTRLocalizeLanguageDescriptionKey = @"LangDescription";
static NSString * const kUserDefaultLanguageKey = @"kUserDefaultLanguageKey";

static NSInteger const kStateBarColor = 0xFFFFFF;
static NSInteger const kBackgroundColor = 0xF5F8F9;
static NSInteger const kThemeColor = 0x2397FF;
static NSInteger const kDefaultBlue = 0x007AFF;

static CGFloat const kNetworkRequestTimeout = 30.f;
static CGFloat const kImageUploadNetworkRequestTimeout = 30.f;

static CGFloat const kWMPageHeight = 44.f;

static NSString * const kFontNameRegular = @"PingFangSC-Regular";      //常规
static NSString * const kFontNameMedium = @"PingFangSC-Medium";        //中黑体
static NSString * const kFontNameLight = @"PingFangSC-Light";   //细体

static NSString * const kApiUrlDomainName = @"kApiUrlDomainName";
/// 在线客服平台保存
static NSString * const kOnlineUrlDomainName = @"kOnlineUrlDomainName";
/// 秘钥
static NSString * const kAccessSecretDomainName = @"kAccessSecretDomainName";
/// 渠道入口Id
static NSString * const kAccessIdDomainName = @"kAccessIdDomainName";
/// 相应的企业Id保存
static NSString * const kEnterpriseIdDomainName = @"kEnterpriseIdDomainName";
/// 项目域名保存
static NSString * const kDomainName = @"kDomainName";


/// SDK版本保存
static NSString * const kSDKVersonPath = @"SDKVerson";

/// 登录信息保存（不包括密码）
static NSString * const kLoginPath = @"LoginInformation";
/// 登录密码保存
static NSString * const kLoginPassword = @"LoginPassword";
/// 主叫号码
static NSString * const kCallerNumber = @"callerNumber";

///  accessToken
static NSString * const kaccessToken = @"accessToken";

#endif /* TiGlobalDefines_h */

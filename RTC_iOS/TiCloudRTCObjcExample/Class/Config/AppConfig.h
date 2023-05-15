//
//  AppConfig.h
//  TiCloudRTCObjcExample
//
//  Created by 马迪 on 2022/9/5.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface AppConfig : NSObject

+ (UIWindow *)getKeyWindow;

+ (BOOL)isValidatePhoneNumber:(NSString *)mobile;

+ (BOOL)stringIsValidatePhoneNumber:(NSString *)phoneNum;

// 字典转字符串
+ (NSString*)toNSStringWithDictionary:(NSDictionary *)dict;

// 字符串转字典
+ (NSDictionary *)toDictionaryWithNSString:(NSString *)string;

@end

NS_ASSUME_NONNULL_END

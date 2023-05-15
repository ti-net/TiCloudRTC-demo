//
//  AppConfig.m
//  TiCloudRTCObjcExample
//
//  Created by 马迪 on 2022/9/5.
//

#import "AppConfig.h"

@implementation AppConfig

+ (UIWindow *)getKeyWindow
{
    if (@available(iOS 13.0, *)) {
        for (UIWindowScene *scene in UIApplication.sharedApplication.connectedScenes) {
            for (UIWindow *window in scene.windows) {
                if (window.isKeyWindow) {
                    return window;
                }
            }
        }
        
    } else {
        return [UIApplication sharedApplication].keyWindow;
    }
    return nil;
}

+ (BOOL)isValidatePhoneNumber:(NSString *)mobile
{
    NSString *phoneRegex = @"1[3456789]([0-9]){9}";
    NSPredicate *phoneTest = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", phoneRegex];
    return [phoneTest evaluateWithObject:mobile];
}


+ (BOOL)stringIsPhoneNumber:(NSString *)phoneNum
{
    phoneNum = [phoneNum stringByReplacingOccurrencesOfString:@" " withString:@""];
    
    /**
     * 中国移动：China Mobile
     * 134(0-8)、135、136、137、138、139、1440、147、148、150、151、152、157、158、159、172、178、182、183、184、187、188、195 [1]  、197、198
     *  虚拟运营商：1703、1705、1706、165
     */
    NSString *cm = @"(^1(3[5-9]|4[78]|5[0-27-9]|65|7[28]|8[2-478]|9[78])\\d{8}$)|(^134[0-8]\\d{7}$)|(^1440\\d{7}$)|(^170[356]\\d{7}$)";
    /**
     * 中国联通：China Unicom
     * 130、131、132、145、155、156、166、167、171、175、176、185、186、196
     * 虚拟运营商：1704、1707、1708、1709、171、167
     */
    NSString *cu = @"(^1(3[0-2]|4[5]|5[56]|6[67]|7[156]|8[56]|196)\\d{8}$)|(^1709\\d{7}$)|(^170[47-9]\\d{7}$)";
    /**
     * 中国电信：China Telecom
     * 133、149、153、173、177、180、181、189、190、191、193、199
     * 虚拟运营商：1700、1701、1702、162
     */
    NSString *ct = @"(^1(33|49|53|62|73|77|8[019]|9[0139])\\d{8}$)|(^170[0-2]\\d{7}$)";
//    NSString *CT = @"(^153\[0-9]]{8}$)";
    /**
     25     * 大陆地区固话及小灵通
     26     * 区号：010,020,021,022,023,024,025,027,028,029
     27     * 号码：七位或八位
     28     */
    NSString * mt = @"^(0[0-9]{2})\\d{8}$|^(0[0-9]{3}(\\d{7,8}))$";
    
    NSPredicate *regextestcm = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", cm];
    NSPredicate *regextestcu = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", cu];
    NSPredicate *regextestct = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", ct];
    NSPredicate *regextestmt = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", mt];

    if ([regextestcm evaluateWithObject:phoneNum] || [regextestcu evaluateWithObject:phoneNum] || [regextestct evaluateWithObject:phoneNum])
    {
        return YES;
    }

    return NO;
}

// 字典转字符串
+ (NSString*)toNSStringWithDictionary:(NSDictionary *)dict
{
    // NSDictionary转成json字符串
    /**
     *NSJSONWritingPrettyPrinted的意思是将生成的json数据格式化输出，
     *这样可读性高，不设置则输出的json字符串就是一整行。
     */
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dict
                                                       options:NSJSONWritingPrettyPrinted
                                                         error:&error];
    NSString *jsonString;
    jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    return jsonString;
}

// 字符串转字典
+ (NSDictionary *)toDictionaryWithNSString:(NSString *)string
{
    if (string == nil)
    {
        return nil;
    }
    
    NSData *jsonData = [string dataUsingEncoding:NSUTF8StringEncoding];
    NSError *err;
    NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:jsonData
                                                        options:NSJSONReadingMutableContainers
                                                          error:&err];
    if(err)
    {
        NSLog(@"json解析失败：%@",err);
        return nil;
    }
    return dict;
}


@end

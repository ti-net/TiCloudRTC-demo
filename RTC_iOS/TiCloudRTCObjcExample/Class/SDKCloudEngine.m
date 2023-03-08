//
//  SDKEngine.m
//  TiCloudRTCObjcExample
//
//  Created by 马迪 on 2022/9/6.
//

#import "SDKCloudEngine.h"

@implementation SDKCloudEngine

+ (instancetype)sharedInstance
{
    static SDKCloudEngine *SDKEngine;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        SDKEngine  = [[SDKCloudEngine alloc] init];
    });
    return SDKEngine;
}

@end

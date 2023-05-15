//
//  SDKEngine.h
//  TiCloudRTCObjcExample
//
//  Created by 马迪 on 2022/9/6.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface SDKCloudEngine : NSObject

+ (instancetype)sharedInstancet;

// SDK实例
@property(nonatomic, strong) TiCloudRTCEngine * tiCloudEngine;

// 初始化SDK
-(void)initSDK;

@end

NS_ASSUME_NONNULL_END

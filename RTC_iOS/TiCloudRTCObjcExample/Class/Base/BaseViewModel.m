//
//  BaseViewModel.m
//  SmartHome
//
//  Created by 赵言 on 2019/7/4.
//  Copyright © 2019 赵言. All rights reserved.
//

#import "BaseViewModel.h"
#import "LoginModel.h"
#import "AppDelegate.h"

@implementation BaseViewModel

- (instancetype)init {
    if (self = [super init]) {
        self.networkState = NetworkStateBegin;
        
        if (IS_IPHONE_5 || IS_IPHONE_4_OR_LESS) {
            self.scale = SCREEN_WIDTH / 375.f;
        } else if (IS_IPHONE_6P) {
            self.scale = 414.0/375.f;
        } else if (IS_IPAD) {
            self.scale = SCREEN_WIDTH / 375.f;
        } else if (IS_IPHONE_X) {
            self.scale = SCREEN_WIDTH / 375.f;
            self.heightScale = SCREEN_HEIGHT/667.f;
        } else if (IS_IPHONE_XsMax) {
            self.scale = 414 / 375.f;
            self.heightScale = SCREEN_HEIGHT/667.f;
        } else {
            self.scale = SCREEN_WIDTH / 375.f;
        }
        
        //监听error等于403时，账号验证失败，退出登录
        [RACObserve(self, connectError) subscribeNext:^(ErrorModel *connectError) {
            if (connectError) {
                if ([connectError.status isEqual:@403]) {
                    self.errorMessage = @"登录已过期，请重新登录";
                    [[LoginModel loginModel] removeLoginModel];
                }
            }
        }];
    }
    return self;
}

- (YTKRequestSerializerType)requestSerializerType {
    return YTKRequestSerializerTypeJSON;
}

/// 验证服务器返回的数据是否存在
- (id)jsonValidator {
    return @{
             @"code": [NSNumber class],
             @"message": [NSString class],
             @"result": [NSObject class]
             };
}

/// 请求超时时长
- (NSTimeInterval)requestTimeoutInterval {
    return kNetworkRequestTimeout;
}

/// 设置header头，传入语言
- (NSDictionary<NSString *,NSString *> *)requestHeaderFieldValueDictionary {
//    DomainNameSave *domainName = [DomainNameSave shareDomainNameSave];
    /// KT 环境需要添加标识符
//    if ([domainName.domainName isEqualToString:KTEnvironment_Variable]) {
//        return @{@"X-Virtual-Env" : @"dev.app"};
//        return @{@"X-Virtual-Env" : @"dev.chat"};
//        return @{@"X-Virtual-Env" : @"dev.crm.ycr"};
//        return @{@"X-Virtual-Env" : @"dev.crm.haoyong"};
//        return @{@"X-Virtual-Env" : @"dev.wangli"};
//        return @{@"X-Virtual-Env" : @"dev.chenyi"};
//        return @{@"X-Virtual-Env" : @"dev.xuzhixiong"};
//    }
    return @{};
}

- (void)requestData {
    [[AppDelegate shareAppDelegate].window endEditing:YES];
    self.networkState = NetworkStateBegin;
}

- (void)requestDataWithReload:(BOOL)reload {
    [[AppDelegate shareAppDelegate].window endEditing:YES];
    self.networkState = NetworkStateBegin;
}


#pragma mark - 懒加载

- (RACSubject *)callBackSubject{
    if (!_callBackSubject) {
        _callBackSubject = [RACSubject subject];
    }
    return _callBackSubject;
}

@end

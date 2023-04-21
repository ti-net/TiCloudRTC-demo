//
//  LoginViewModel.m
//  mobileCMS
//
//  Created by 赵言 on 2019/12/10.
//  Copyright © 2019 赵言. All rights reserved.
//

#import "LoginViewModel.h"
#import "LoginModel.h"
#import "CommonConfig.h"

@interface LoginViewModel ()

@end

@implementation LoginViewModel

+ (instancetype)sharedInstance
{
    static LoginViewModel *loginViewModel;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        loginViewModel  = [[LoginViewModel alloc] init];
    });
    return loginViewModel;
}

- (id)requestArgument
{
    return @{
        @"username": self.username,
        @"password": self.password,
        @"enterpriseId": @(self.enterpriseId),
    };
}

- (YTKRequestMethod)requestMethod
{
    return YTKRequestMethodPOST;
}

- (NSString *)requestUrl
{
    return @"/interface/demo/login";
}

- (void)requestData
{
    [super requestData];
    
    if (!self.baseUrl)
    {
        self.baseUrl = kBaseUrl_Test;
    }
    
    LoginModel *model = [LoginModel loginModel];
    
    model.enterpriseId = self.enterpriseId;
    model.userName = self.username;
    model.baseUrl = self.baseUrl;
    
    [[LoginModel loginModel] saveLoginModel:model];
    
    //猿题库网络框架，设置url的域名
    YTKNetworkConfig *config = [YTKNetworkConfig sharedConfig];
    config.baseUrl = self.baseUrl;

    [self startWithCompletionBlockWithSuccess:^(__kindof YTKBaseRequest * _Nonnull request) {
        if ([request.responseObject[@"code"] isEqual:@200]) {
            NSLog(@"request.responseObject =  %@",request.responseObject);
            LoginModel *model = [LoginModel loginModel];
            model.accessToken = request.responseObject[@"result"][@"accessToken"];
            [[LoginModel loginModel] saveLoginModel:model];
            self.networkState = NetworkStateSuccess;
        } else {
            self.errorMessage = request.responseObject[@"message"];
            self.networkState = NetworkStateFail;
        }
    } failure:^(__kindof YTKBaseRequest * _Nonnull request) {
        NSLog(@"request.responseObject = %@",request.responseObject);
        if ([request.responseObject by_ObjectForKey:@"result"]) {
            self.errorMessage = request.responseObject[@"message"];
            self.networkState = NetworkStateFail;
        } else {
            self.connectError = [ErrorModel yy_modelWithJSON:request.responseObject];
            self.errorMessage = [request.responseObject by_ObjectForKey:@"message"] ? : @"networking_connectFailed";
            self.networkState = NetworkStateConnectFail;
        }
    }];
}

@end

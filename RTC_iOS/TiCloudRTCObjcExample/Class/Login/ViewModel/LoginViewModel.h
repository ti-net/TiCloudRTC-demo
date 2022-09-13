//
//  LoginViewModel.h
//  mobileCMS
//
//  Created by 赵言 on 2019/12/10.
//  Copyright © 2019 赵言. All rights reserved.
//

#import "BaseViewModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface LoginViewModel : BaseViewModel

/// 企业编号
@property (nonatomic, copy) NSString *identifier;

/// 座席编号
@property (nonatomic, copy) NSString *cno;

/// 密码(需要md5加密,32位小写)
@property (nonatomic, copy) NSString *password;

@property (nonatomic, copy) NSString *username;

@property (nonatomic, assign) NSInteger enterpriseId;

@property (nonatomic, copy) NSString *baseUrl;

+ (instancetype)sharedInstance;

@end

NS_ASSUME_NONNULL_END

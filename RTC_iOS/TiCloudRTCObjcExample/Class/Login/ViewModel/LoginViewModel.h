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

@property (nonatomic, copy, nullable) NSString *password;

@property (nonatomic, copy, nullable) NSString *username;

@property (nonatomic, assign) NSInteger enterpriseId;

@property (nonatomic, copy) NSString *baseUrl;

+ (instancetype)sharedInstance;

@end

NS_ASSUME_NONNULL_END

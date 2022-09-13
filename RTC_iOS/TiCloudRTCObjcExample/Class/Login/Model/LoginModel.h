//
//  LoginModel.h
//  TOSClientKitDemo
//
//  Created by 言 on 2022/6/29.
//  Copyright © 2022 YanBo. All rights reserved.
//

#import "BaseModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface LoginModel : BaseModel

+ (LoginModel *)loginModel;

// 保存LoginModel
- (BOOL)saveLoginModelWithDic:(NSDictionary *)dic;
- (BOOL)saveLoginModel:(LoginModel *)loginModel;
// 删除LoginModel
- (void)removeLoginModel;

@property (nonatomic, copy) NSString * accessToken;

@property (nonatomic, assign) NSInteger enterpriseId;
@property (nonatomic, copy) NSString * userName;

@property (nonatomic, copy) NSString * baseUrl;


@end

NS_ASSUME_NONNULL_END

//
//  CHResolutionView.h
//  CloudHub Active
//
//  Created by 马迪 on 2021/4/23.
//
//   Copyright 2019 The CloudHub project authors. All Rights Reserved.
//  Use of this source code is governed by a BSD-style license that can be found in the LICENSE file in the root of the source tree. An additional intellectual property rights grant can be found in the file PATENTS.  All contributing project authors may be found in the AUTHORS file in the root of the source tree.

#import <UIKit/UIKit.h>
#import "TextFieldView.h"

NS_ASSUME_NONNULL_BEGIN

@interface CHResolutionView : UIView

@property (nonatomic, copy) void(^resolutionViewButtonClick)(EnvironmentType environmentType);

@property (nonatomic, assign) EnvironmentType environmentType;

@property (nonatomic, assign, readonly) CGFloat cellHeight;

- (instancetype)initWithFrame:(CGRect)frame withData:(NSArray *)dataArray;

@end

NS_ASSUME_NONNULL_END

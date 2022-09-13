//
//  DrawShapeView.h
//  test
//
//  Created by 马迪 on 2022/9/5.
//  Copyright © 2022 apple. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface DrawShapeView : UIView

- (instancetype)initWithFrame:(CGRect)frame withPoints:(NSArray *)pointArray;

@end

NS_ASSUME_NONNULL_END

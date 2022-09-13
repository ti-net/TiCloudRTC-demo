//
//  DrawShapeView.m
//  test
//
//  Created by 马迪 on 2022/9/5.
//  Copyright © 2022 apple. All rights reserved.
//

#import "DrawShapeView.h"

@interface DrawShapeView ()

@property (nonatomic, copy) NSArray *pointArray;

@end

@implementation DrawShapeView

- (instancetype)initWithFrame:(CGRect)frame withPoints:(NSArray *)pointArray
{
    if (self = [super initWithFrame:frame])
    {
        self.pointArray = pointArray;
    }
    return self;
}

- (void)drawRect:(CGRect)rect
{
    [self drawLine:self.pointArray[0]];
    
    [self drawLine:self.pointArray[1]];
}

-(void)drawLine:(NSArray *)pointsArray
{
    //贝瑟尔路径
    //1、创建路径
    UIBezierPath *path = [UIBezierPath bezierPath];
    
    for (int i = 0; i < pointsArray.count; i++)
    {
        if (!i)
        {
            //2、设置起点
            [path moveToPoint:[pointsArray[0] CGPointValue]];
        }
        else
        {
            [path addLineToPoint:[pointsArray[i] CGPointValue]];
        }
    }

    [path setLineWidth:1.0];
    [path setLineJoinStyle:kCGLineJoinBevel];
    [path setLineCapStyle:kCGLineCapButt];

    //3、渲染上下文到View的layer
    [path stroke];
}

@end

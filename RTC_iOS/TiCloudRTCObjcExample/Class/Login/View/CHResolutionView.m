//
//  CHResolutionView.m
//  CloudHub Active
//
//  Created by 马迪 on 2021/4/23.
//
//   Copyright 2019 The CloudHub project authors. All Rights Reserved.
//  Use of this source code is governed by a BSD-style license that can be found in the LICENSE file in the root of the source tree. An additional intellectual property rights grant can be found in the file PATENTS.  All contributing project authors may be found in the AUTHORS file in the root of the source tree.

#import "CHResolutionView.h"

#define leftMargin 5.0f

@interface CHResolutionView ()

@property(nonatomic, strong) NSArray *dataArray;

@property(nonatomic, strong) NSMutableArray *valueArray;

@property(nonatomic, strong) NSMutableArray *lineArray;

@property (nonatomic, assign) CGFloat cellHeight;

@property (nonatomic, weak) UILabel *titleLable;

@end

@implementation CHResolutionView

- (instancetype)initWithFrame:(CGRect)frame withData:(NSArray *)dataArray
{
    self = [super initWithFrame:frame];
    if (self)
    {
        self.backgroundColor = UIColor.whiteColor;
        
        self.valueArray = [NSMutableArray array];
        self.lineArray = [NSMutableArray array];

        self.dataArray = dataArray;
                
        [self setupView];
        
        self.cellHeight = 50;
        
        self.frame = frame;
        
        self.layer.borderWidth = 1.0;
        self.layer.borderColor = UIColor.grayColor.CGColor;
        self.layer.cornerRadius = 8;
    }
    
    return self;
}

- (void)setupView
{
    for (int i = 0 ; i < self.dataArray.count; i++)
    {
        UIButton *valueButton = [self creatbuttonWithTitle:self.dataArray[i]];
        valueButton.tag = i;
        
        UIView *lineView = [[UIView alloc]init];
        lineView.backgroundColor = UIColor.grayColor;
        [self addSubview:lineView];
        
        [self.valueArray addObject: valueButton];
        [self.lineArray addObject:lineView];
    }
}

- (void)setFrame:(CGRect)frame
{
    [super setFrame:frame];
    
    if (frame.size.height)
    {
        for (int i = 0 ; i < self.dataArray.count; i++)
        {
            UIButton *valueButton = self.valueArray[i];
            
            valueButton.frame = CGRectMake(leftMargin, i * self.cellHeight, frame.size.width - 2 * leftMargin, self.cellHeight - 1);
            
            UIView *lineView = self.lineArray[i];
            lineView.frame = CGRectMake(leftMargin, valueButton.bottom , frame.size.width - 2 * leftMargin, 1);
        }
    }
    else
    {
        for (int i = 0 ; i < self.dataArray.count; i++)
        {
            UIButton *valueButton = self.valueArray[i];
            
            valueButton.frame = CGRectMake(0,0,0,0);
            
            UIView *lineView = self.lineArray[i];
            lineView.frame =  CGRectMake(0,0,0,0);;
        }
    }
}

- (UIButton *)creatbuttonWithTitle:(NSString *)title
{
    UIButton *button = [[UIButton alloc] init];
    [button setTitle:title forState:UIControlStateNormal];
    button.titleLabel.font = CHFont15;
    [button setTitleColor:UIColor.blackColor forState:UIControlStateNormal];
    [button setTitleColor:UIColor.redColor forState:UIControlStateSelected];
    [button addTarget:self action:@selector(buttonsClick:) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:button];
    return button;
}

- (void)buttonsClick:(UIButton *)button
{
    if (!button.selected)
    {
        UIButton *lastButton = self.valueArray[self.environmentType];
        lastButton.selected = NO;
        
        button.selected = YES;
        self.environmentType = button.tag;
        if (_resolutionViewButtonClick)
        {
            _resolutionViewButtonClick(button.tag);
        }
    }
}

- (void)setEnvironmentType:(EnvironmentType)environmentType
{
    UIButton *lastButton = self.valueArray[self.environmentType];
    lastButton.selected = NO;
    
    _environmentType = environmentType;
    
    UIButton *button = self.valueArray[environmentType];
    button.selected = YES;
}

@end

//
//  TelephoneView.m
//  TiCloudRTCObjcExample
//
//  Created by 马迪 on 2022/9/3.
//

#import "TelephoneView.h"
#import "AppConfig.h"

#define MarginH  30
#define ButtonWidth  72
#define LeftMargin  (self.width - 3 * ButtonWidth - 2 * MarginH)/2
#define MarginV  15

@interface TelephoneView ()

@property(nonatomic, weak) UILabel *phoneLabel;

@property(nonatomic, weak) UILabel *inputLabel;

@property(nonatomic, weak) UILabel *timeLabel;

@property(nonatomic, weak) UIButton *silentBtn;

@property(nonatomic, weak) UIButton *dialBtn;

@property(nonatomic, weak) UIButton *handsFreeBtn;

@property(nonatomic, weak) UIButton *HangUpBtn;

@property(nonatomic, weak) UIButton *hiddenBtn;

@property(nonatomic, weak) UIView *bgNumberView;

@property(nonatomic, weak) NSTimer *timer;

@end

@implementation TelephoneView

+ (instancetype)sharedInstance
{
    static TelephoneView *telephoneView;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        telephoneView  = [[TelephoneView alloc] initWithFrame:CGRectMake(0, 0, kScreenWidth, kScreenHeight)];
        
        [[AppConfig getKeyWindow] addSubview:telephoneView];
    });
    return telephoneView;
}

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self)
    {
        self.backgroundColor = UIColor.grayColor;
                
        [self setupUIView];
    }
    return self;
}

- (void)setupUIView
{
    UIImageView *bgImageView = [[UIImageView alloc]initWithFrame:self.bounds];
    bgImageView.image = [UIImage imageNamed:@"Frame 139"];
    [self addSubview:bgImageView];
    
    UILabel *phoneLabel = [[UILabel alloc]initWithFrame:CGRectMake(20, 80, self.width - 40, 35)];
    phoneLabel.text = self.phoneNumber;
    phoneLabel.font = CHFont(36);
    phoneLabel.textColor = UIColor.whiteColor;
    phoneLabel.textAlignment = NSTextAlignmentCenter;
    [self addSubview:phoneLabel];
    self.phoneLabel = phoneLabel;
    
    UILabel *inputLabel = [[UILabel alloc]initWithFrame:CGRectMake(20, 80, self.width - 40, 35)];
    inputLabel.text = @"";
    inputLabel.font = CHFont(36);
    inputLabel.textColor = UIColor.whiteColor;
    inputLabel.textAlignment = NSTextAlignmentCenter;
    [self addSubview:inputLabel];
    self.inputLabel = inputLabel;
    inputLabel.hidden = YES;

    UILabel *timeLabel = [[UILabel alloc]initWithFrame:CGRectMake(20, phoneLabel.bottom + 5, self.width - 40, 25)];
    timeLabel.text = @"正在呼叫...";
    timeLabel.font = CHFont(20);
    timeLabel.textColor = kHexAColor(0xFFFFFF, 0.65);
    timeLabel.textAlignment = NSTextAlignmentCenter;
    [self addSubview:timeLabel];
    self.timeLabel = timeLabel;
    
    CGFloat TitleH = 30;
    
    UIButton *HangUpBtn = [[UIButton alloc]initWithFrame:CGRectMake((self.width - ButtonWidth) * 0.5, self.height - 60 - ButtonWidth, ButtonWidth, ButtonWidth)];
    HangUpBtn.contentMode = UIViewContentModeCenter;
    [HangUpBtn setBackgroundImage:[UIImage imageNamed:@"Frame 121"] forState:UIControlStateNormal];
    [HangUpBtn addTarget:self action:@selector(HangUpBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:HangUpBtn];
    self.HangUpBtn = HangUpBtn;
        
    UIButton *dialBtn = [self creatbuttonWithframe:CGRectMake(LeftMargin, HangUpBtn.top - 40 - (ButtonWidth + TitleH), ButtonWidth, ButtonWidth + TitleH) title:@"拨号键盘" Image:@"Frame 135" selectImage:@"Frame 134" action:@selector(dialBtnClick)];
    self.dialBtn = dialBtn;
 
    UIButton *silentBtn = [self creatbuttonWithframe:CGRectMake(dialBtn.right + MarginH, dialBtn.top, ButtonWidth, ButtonWidth + TitleH) title:@"静音" Image:@"Frame 136" selectImage:@"Frame 133" action:@selector(silentBtnClick:)];
    self.silentBtn = silentBtn;
     
    UIButton *handsFreeBtn = [self creatbuttonWithframe:CGRectMake(silentBtn.right + MarginH, silentBtn.top, ButtonWidth, ButtonWidth + TitleH) title:@"扬声器" Image:@"Frame 137" selectImage:@"Frame 132" action:@selector(handsFreeBtnClick:)];
    self.handsFreeBtn = handsFreeBtn;
    
    UIButton *hiddenBtn = [[UIButton alloc]initWithFrame:CGRectMake(self.width - LeftMargin - 42 - 10 , 0, 42, 42)];
    hiddenBtn.centerY = HangUpBtn.centerY;
    [hiddenBtn setTitle:@"隐藏" forState:UIControlStateNormal];
    [hiddenBtn setTitleColor:kHexAColor(0xFFFFFF, 0.85) forState:UIControlStateNormal];
    hiddenBtn.titleLabel.font = CHFont(20);
    [hiddenBtn addTarget:self action:@selector(hiddenBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:hiddenBtn];
    self.hiddenBtn = hiddenBtn;
    hiddenBtn.hidden = YES;
    
    // 数字键盘
    [self setupNumbersView];
}

- (UIButton *)creatbuttonWithframe:(CGRect)frame title:(NSString *)title Image:(NSString *)image selectImage:(NSString *)selectImage action:(SEL)action
{
    UIButton *button = [[UIButton alloc]initWithFrame:frame];
    [button setImage:[UIImage imageNamed:image] forState:UIControlStateNormal];
    [button setImage:[UIImage imageNamed:selectImage] forState:UIControlStateSelected];
    [button setTitle:title forState:UIControlStateNormal];
    [button setTitleColor:kHexAColor(0xFFFFFF, 0.65) forState:UIControlStateNormal];
    button.titleLabel.font = CHFont16;
    [button addTarget:self action:action forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:button];
    
    CGFloat offset = 12;
    
    button.titleEdgeInsets = UIEdgeInsetsMake(0, -button.imageView.frame.size.width, -button.imageView.frame.size.height- offset * 0.5, 0);
    button.imageEdgeInsets = UIEdgeInsetsMake(-button.titleLabel.intrinsicContentSize.height - offset * 0.5, 0, 0, -button.titleLabel.intrinsicContentSize.width);
    return button;
}

- (void)setPhoneNumber:(NSString *)phoneNumber
{
    _phoneNumber = phoneNumber;
    
    self.phoneLabel.text = phoneNumber;
}

- (void)dialBtnClick
{
    self.phoneLabel.hidden = self.timeLabel.hidden = YES;
    
    self.inputLabel.hidden = NO;
    
    self.dialBtn.hidden = self.silentBtn.hidden = self.handsFreeBtn.hidden = YES;
    
    self.hiddenBtn.hidden = self.bgNumberView.hidden = NO;
}

// YES： 开启本地音频（取消静音） NO：关闭本地音频(静音)
- (void)silentBtnClick:(UIButton *)button
{
    if ([self.delegate respondsToSelector:@selector(localAudioButtonClick:)])
    {
        [self.delegate localAudioButtonClick:button.selected];
    }
    
    button.selected = !button.selected;
}


// YES: 开启扬声器。音频路由为扬声器  NO: 关闭扬声器。音频路由为听筒。
- (void)handsFreeBtnClick:(UIButton *)button
{
    if ([self.delegate respondsToSelector:@selector(speakphoneButtonClick:)])
    {
        [self.delegate speakphoneButtonClick:!button.selected];
    }
}

- (void)setSpeakEnable:(BOOL)speakEnable
{
    self.handsFreeBtn.selected = speakEnable;
}

- (void)HangUpBtnClick
{
    if ([self.delegate respondsToSelector:@selector(hangupButtonClick)])
    {
        [self.delegate hangupButtonClick];
    }
    
    [self hiddenBtnClick];
    
    self.dialBtn.selected = self.silentBtn.selected = self.handsFreeBtn.selected = NO;
}

- (void)hiddenBtnClick
{
    self.phoneLabel.hidden = self.timeLabel.hidden = NO;
    
    self.inputLabel.hidden = YES;
    
    self.hiddenBtn.hidden = self.bgNumberView.hidden = YES;
    
    self.dialBtn.hidden = self.silentBtn.hidden = self.handsFreeBtn.hidden =NO;
}

/// 创建数字键盘
- (void)setupNumbersView
{
    CGFloat bgHeight = 4 * ButtonWidth + 3 * MarginV;
    
    UIView *bgNumberView = [[UIView alloc]initWithFrame:CGRectMake(0, self.HangUpBtn.top - 20 - bgHeight, self.width, bgHeight)];
    [self addSubview:bgNumberView];
    self.bgNumberView = bgNumberView;
    bgNumberView.hidden = YES;
    
    for (int i = 0; i < 12; i++)
    {
        NSInteger tempX = i % 3;  // 小格子所在的列
        NSInteger tempY = i / 3; // 小格子所在的行
        
        UIButton *button = [[UIButton alloc]initWithFrame:CGRectMake(LeftMargin + tempX * (ButtonWidth +MarginH), tempY * (ButtonWidth + MarginV), ButtonWidth, ButtonWidth)];
        [button setTitleColor:UIColor.whiteColor forState:UIControlStateNormal];
        button.titleLabel.font = CHFont(36);
        [button setBackgroundColor:kHexAColor(0xF0F0F0, 0.25)];
        [button addTarget:self action:@selector(numbersButtonsClick:) forControlEvents:UIControlEventTouchUpInside];
        button.tag = i + 1;
        [bgNumberView addSubview:button];
        button.layer.cornerRadius = ButtonWidth *0.5;
        
        if (i < 9)
        {
            [button setTitle:[NSString stringWithFormat:@"%d",i+1] forState:UIControlStateNormal];
        }
        else if (i == 9)
        {
            [button setTitle:@"*" forState:UIControlStateNormal];
        }
        else if (i == 10)
        {
            [button setTitle:@"0" forState:UIControlStateNormal];
        }
        else if (i == 11)
        {
            [button setTitle:@"#" forState:UIControlStateNormal];
        }
    }
}

- (void)numbersButtonsClick:(UIButton *)button
{
    NSString *number = nil;
    
    if (button.tag < 10)
    {
        number = [NSString stringWithFormat:@"%ld",button.tag];
    }
    else if (button.tag == 10)
    {
        number = @"*";
    }
    else if (button.tag == 11)
    {
        number = @"0";
    }
    else if (button.tag == 12)
    {
        number = @"#";
    }
    NSLog(@"打印数字--%@",number);
        
    self.inputLabel.text = [self.inputLabel.text stringByAppendingString:number];
    
    if ([self.delegate respondsToSelector:@selector(numberButtonsClick:)])
    {
        [self.delegate numberButtonsClick:number];
    }
}

#pragma mark - 外部事件
- (void)callingStart
{
    self.timeLabel.text = @"正在呼叫...";
}

- (void)callingReceive
{
    CHWeakSelf
    __block NSInteger timeNumber = 0;
    self.timer = [NSTimer scheduledTimerWithTimeInterval:1.0 repeats:YES block:^(NSTimer * _Nonnull timer) {
        timeNumber ++;
        
        if (timeNumber < 60)
        {
            weakSelf.timeLabel.text = [NSString stringWithFormat:@"00:%0.2ld",timeNumber];
        }
        else if (timeNumber < 3600)
        {
            weakSelf.timeLabel.text = [NSString stringWithFormat:@"%0.2ld:%0.2ld",timeNumber/60,timeNumber%60];
        }
        else
        {
            weakSelf.timeLabel.text = [NSString stringWithFormat:@"%0.2ld:%0.2ld:%0.2ld",timeNumber/3600,(timeNumber%3600)/60,timeNumber%60];
        }
    }];
    
    if (self.silentBtn.selected)
    {
        if ([self.delegate respondsToSelector:@selector(localAudioButtonClick:)])
        {
            [self.delegate localAudioButtonClick:NO];
        }
    }
}

- (void)callingEnd
{
    self.timeLabel.text = @"通话结束";
    self.inputLabel.text = @"";
    
    [self.timer invalidate];
    self.timer = nil;
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self hiddenBtnClick];
    });
}



@end

//
//  CustomerField.m
//  TiCloudRTCObjcExample
//
//  Created by 马迪 on 2022/9/6.
//

#import "CustomerField.h"

@interface CustomerField ()
<
    UITextFieldDelegate
>

@property (nonatomic, assign) CustomerFieldType fieldType;

@property (nonatomic, weak) UITextField *textField;

@property (nonatomic, weak) UIButton *rightBtn;

@end

@implementation CustomerField

- (instancetype)initWithFrame:(CGRect)frame withType:(CustomerFieldType)fieldType
{
    if (self = [super initWithFrame:frame])
    {
        self.fieldType = fieldType;
        
        [self creatSubviews];
    }
    return self;
}

- (void)creatSubviews
{
    UIView *lineView = [[UIView alloc]initWithFrame:CGRectMake(0, self.height - 1, self.width, 1)];
    lineView.backgroundColor = [UIColor colorWithWhite:0 alpha:0.06];
    [self addSubview:lineView];
    
    UIButton *rightBtn = [[UIButton alloc]initWithFrame:CGRectMake(self.width - 28, (self.height - 28)/2, 28, 28)];
    rightBtn.imageView.contentMode = UIViewContentModeCenter;
    [rightBtn setImage:[UIImage imageNamed:@"Frame 140"] forState:UIControlStateNormal];
    [rightBtn addTarget:self action:@selector(rightBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:rightBtn];
    self.rightBtn = rightBtn;
    
    UITextField *textField = [[UITextField alloc]initWithFrame:CGRectMake(0, 1, self.width - rightBtn.width - 5, self.height - 2)];
    textField.font = CHFont15;
    textField.textColor = kHexColor(0x262626);
    [self addSubview:textField];
    self.textField = textField;
    
    [textField addTarget:self action:@selector(textFieldEditChanged:) forControlEvents:UIControlEventEditingChanged];
    
    if (self.fieldType == CustomerFieldType_One)
    {
        self.textField.text = @"VIP客户";
        self.rightBtn.userInteractionEnabled = NO;
    }
    else if (self.fieldType == CustomerFieldType_Two)
    {
        self.textField.text = @"有意向客户";
        self.rightBtn.userInteractionEnabled = NO;
    }
    else if (self.fieldType == CustomerFieldType_Three)
    {
        self.textField.placeholder = @"请输入客户号码";
    }
}

- (void)textFieldEditChanged:(UITextField*)textField
{
    if ([self.delegate respondsToSelector:@selector(textFieldEdited:)])
    {
        [self.delegate textFieldEdited:textField];
    }
}

- (void)rightBtnClick
{
    if ([self.delegate respondsToSelector:@selector(startCall)])
    {
        [self.delegate startCall];
    }
}



@end

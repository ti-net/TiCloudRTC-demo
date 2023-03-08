//
//  TextFieldView.m
//  TiCloudRTCObjcExample
//
//  Created by 马迪 on 2022/9/2.
//

#import "TextFieldView.h"

@interface TextFieldView ()
<
    UITextFieldDelegate
>

@property(nonatomic, assign) TextFieldViewType viewType;

@property (nonatomic, weak) UIImageView *logoImageView;

@property (nonatomic, weak) UIButton *rightBtn;

@end

@implementation TextFieldView

- (instancetype)initWithFrame:(CGRect)frame withType:(TextFieldViewType)viewType
{
    if (self = [super initWithFrame:frame])
    {
        self.viewType = viewType;
        
        [self creatSubviews];
    }
    return self;
}

- (void)creatSubviews
{
    UIView *lineView = [[UIView alloc]initWithFrame:CGRectMake(0, self.height - 1, self.width, 1)];
    lineView.backgroundColor = [UIColor colorWithWhite:0 alpha:0.06];
    [self addSubview:lineView];
    
    UIImageView *logoImageView = [[UIImageView alloc]initWithFrame:CGRectMake(0, 1, (self.height - 2)/2, (self.height - 2)/2)];
    logoImageView.contentMode = UIViewContentModeCenter;
    [self addSubview:logoImageView];
    self.logoImageView = logoImageView;
    
    CGFloat rightX = 0;
    
    if (self.viewType == TextFieldViewType_Environment || self.viewType == TextFieldViewType_Password)
    {
        UIButton *rightBtn = [[UIButton alloc]initWithFrame:CGRectMake(self.width - 30, 0, 30, self.height)];
        rightBtn.imageView.contentMode = UIViewContentModeCenter;
        [rightBtn setImage:[UIImage imageNamed:@"browse-off"] forState:UIControlStateNormal];
        [rightBtn setImage:[UIImage imageNamed:@"browse"] forState:UIControlStateSelected];
        [rightBtn addTarget:self action:@selector(rightBtnClick:) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:rightBtn];
        self.rightBtn = rightBtn;
        
        rightX = 30 + 10;
    }
                                                       
    UITextField *textField = [[UITextField alloc]initWithFrame:CGRectMake(logoImageView.right + 5, 1, self.width - (logoImageView.right +5) - rightX, logoImageView.height)];
    textField.font = [UIFont systemFontOfSize:14];
    textField.textColor = UIColor.blackColor;
    textField.delegate = self;
    [self addSubview:textField];
    self.textField = textField;
    [textField addTarget:self action:@selector(textFieldEditChanged:) forControlEvents:UIControlEventEditingChanged];
    
//    NSAttributedString *attString1 = [[NSAttributedString alloc]initWithString:@"请输入企业ID" attributes:@{NSForegroundColorAttributeName:[UIColor grayColor]}];
//
////    NSAttributedString *attString2 = [[NSAttributedString alloc]initWithString:@"请输入用户名" attributes:@{NSForegroundColorAttributeName:[UIColor grayColor]}];
//    NSAttributedString *attString2 = [[NSAttributedString alloc]initWithString:@"请输入accessToken" attributes:@{NSForegroundColorAttributeName:[UIColor grayColor]}];
//
//    NSAttributedString *attString3 = [[NSAttributedString alloc]initWithString:@"请输入密码" attributes:@{NSForegroundColorAttributeName:[UIColor grayColor]}];
    
    
    UIImage *orImage = nil;
    switch (self.viewType)
    {
        case TextFieldViewType_Environment:
            textField.text = @"测试环境";
            orImage = [UIImage imageNamed:@"Frame"];
            logoImageView.image = [orImage imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
            [logoImageView setTintColor:kRGBColor(73, 129, 96)]; ;
            [self.rightBtn setImage:[UIImage imageNamed:@"tableViewCell_downArrow"] forState:UIControlStateNormal];
            [self.rightBtn setImage:[UIImage imageNamed:@"tableViewCell_upArrow"] forState:UIControlStateSelected];
            
            break;
            
        case TextFieldViewType_EnterprisesId:
                        
//            textField.attributedPlaceholder = attString1;
            orImage = [UIImage imageNamed:@"building-2-line 1"];
            logoImageView.image = [orImage imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
            [logoImageView setTintColor:kRGBColor(73, 129, 96)];
            break;
            
        case TextFieldViewType_UserName:
            
//            textField.attributedPlaceholder = attString2;
            orImage = [UIImage imageNamed:@"user"];
            logoImageView.image = [orImage imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
            [logoImageView setTintColor:kRGBColor(73, 129, 96)];
            break;
            
        case TextFieldViewType_Password:
            
//            textField.attributedPlaceholder = attString3;
            orImage = [UIImage imageNamed:@"lock-on"];
            logoImageView.image = [orImage imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
            [logoImageView setTintColor:kRGBColor(73, 129, 96)];
            
            [self.rightBtn setImage:[UIImage imageNamed:@"browse-off"] forState:UIControlStateNormal];
            [self.rightBtn setImage:[UIImage imageNamed:@"browse"] forState:UIControlStateSelected];
            
            break;
            
        default:
            break;
    }
}

- (void)rightBtnClick:(UIButton *)button
{
    button.selected = !button.selected;
    
    if (self.viewType == TextFieldViewType_Environment)
    {         
        if ([self.delegate respondsToSelector:@selector(rightButtonClick:)])
        {
            [self.delegate rightButtonClick:button.selected];;
        }
    }
    else if (self.viewType == TextFieldViewType_Password)
    {
        self.textField.secureTextEntry = !button.selected;
    }
    else
    {
        self.textField.text = nil;
    }
}

- (void)setPlaceholder:(NSString *)placeholder
{
    _placeholder = placeholder;
    
    NSAttributedString *attString = [[NSAttributedString alloc]initWithString:placeholder attributes:@{NSForegroundColorAttributeName:[UIColor grayColor]}];
    
    self.textField.attributedPlaceholder = attString;
}

- (void)setEnvironmentType:(EnvironmentType)environmentType
{
    _environmentType = environmentType;
    
    switch (environmentType)
    {
        case EnvironmentType_Test:
            self.textField.text = @"测试环境";
            break;
            
        case EnvironmentType_Develop:
            self.textField.text = @"开发环境";
            break;
            
        case EnvironmentType_Formal:
            self.textField.text = @"正式环境";
            break;
            
        default:
            break;
    }
}

- (void)textFieldEditChanged:(UITextField*)textField
{
    if ([self.delegate respondsToSelector:@selector(textFieldEditing:)])
    {
        [self.delegate textFieldEditing:self];;
    }
}

- (void)setString:(NSString *)string
{
    _string = string;
    
    self.textField.text = string;
}

- (void)setRightBtnSelect:(BOOL)rightBtnSelect
{
    _rightBtnSelect = rightBtnSelect;
    
    if (self.rightBtn.selected != rightBtnSelect)
    {
        self.rightBtn.selected = rightBtnSelect;
    }
}

- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    if ([self.delegate respondsToSelector:@selector(textFieldBeginEditing)])
    {
        [self.delegate textFieldBeginEditing];
    }
}

- (void)textFieldDidEndEditing:(UITextField *)textField
{
    if ([self.delegate respondsToSelector:@selector(textFieldEndEditing)])
    {
        [self.delegate textFieldEndEditing];
    }
}


@end

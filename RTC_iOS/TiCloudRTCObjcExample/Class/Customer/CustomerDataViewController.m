//
//  CustomerDataViewController.m
//  TiCloudRTCObjcExample
//
//  Created by 马迪 on 2022/9/6.
//

#import "CustomerDataViewController.h"
#import "CustomerField.h"
#import "AppConfig.h"
#import "DialPageViewController.h"

@interface CustomerDataViewController ()
<
    CustomerFieldDelegate
>

@property (nonatomic, weak) CustomerField *field3;

@property (nonatomic, weak) UILabel *errorPhoneLabel;

@end

@implementation CustomerDataViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.view.backgroundColor = UIColor.whiteColor;
    
    UIButton *backBtn = [[UIButton alloc]initWithFrame:CGRectMake(0 , kStatusBarHeight + 5, 70, 42)];
    backBtn.imageView.contentMode = UIViewContentModeCenter;
    [backBtn setImage:[UIImage imageNamed:@"backPrevViewC"] forState:UIControlStateNormal];
    backBtn.imageEdgeInsets = UIEdgeInsetsMake(0, -15, 0, 10);
    [backBtn addTarget:self action:@selector(backBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:backBtn];

    [self initSubViews];
}

- (void)backBtnClick
{
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)initSubViews
{
    UILabel *titleLabel = [[UILabel alloc]initWithFrame:CGRectMake(32, 100, 90, 28)];
    titleLabel.text = @"客户资料";
    titleLabel.font = CHFont(20);
    titleLabel.textColor = kHexColor(0x262626);
    titleLabel.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:titleLabel];
    
    UIView *lineView = [[UIView alloc]initWithFrame:CGRectMake(32, titleLabel.bottom + 5, titleLabel.width, 5)];
    lineView.layer.cornerRadius = 2.5;
    lineView.backgroundColor = kHexColor(0x4385FF);
    [self.view addSubview:lineView];
    
    CGFloat Margin = 32;
    
    CustomerField *field1 = [[CustomerField alloc]initWithFrame:CGRectMake(Margin, lineView.bottom + 50, self.view.width - 2 * Margin, 50) withType:CustomerFieldType_One];
    [self.view addSubview:field1];
    
    CustomerField *field2 = [[CustomerField alloc]initWithFrame:CGRectMake(Margin, field1.bottom + 10, self.view.width - 2 * Margin, 50) withType:CustomerFieldType_Two];
    [self.view addSubview:field2];
    
    CustomerField *field3 = [[CustomerField alloc]initWithFrame:CGRectMake(Margin, field2.bottom + 10, self.view.width - 2 * Margin, 50) withType:CustomerFieldType_Three];
    field3.delegate = self;
    [self.view addSubview:field3];
    self.field3 = field3;
    
    UILabel *errorPhoneLabel = [[UILabel alloc]initWithFrame:CGRectMake(Margin, field3.bottom +10, 100, 20)];
    errorPhoneLabel.text = @"号码格式不正确";
    errorPhoneLabel.font = CHFont12;
    errorPhoneLabel.textColor = kHexColor(0xFF4D4F);
    [self.view addSubview:errorPhoneLabel];
    self.errorPhoneLabel = errorPhoneLabel;
    errorPhoneLabel.hidden = YES;
}

#pragma mark - CustomerFieldDelegate

- (void)textFieldEdited:(UITextField *)textField
{
    if (!textField.text.length)
    {
        self.errorPhoneLabel.hidden = YES;
    }
    else
    {
        self.errorPhoneLabel.hidden = NO;
//        self.errorPhoneLabel.hidden = [AppConfig isValidatePhoneNumber:textField.text];
    }
}

- (void)startCall
{
    if (!self.field3.textField.text.length)
    {
        [self showErrorView:@"请输入客户号码"];
        return;
    }
    else if (!self.errorPhoneLabel.hidden)
    {
        [self showErrorView:@"号码格式不正确"];
        return;
    }
    
    DialPageViewController *dialPageVC = [[DialPageViewController alloc]init];
    dialPageVC.modalPresentationStyle = UIModalPresentationFullScreen;
    dialPageVC.phoneNumber = self.field3.textField.text;
    [self presentViewController:dialPageVC animated:YES completion:nil];
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    [self.view endEditing:YES];
}

@end

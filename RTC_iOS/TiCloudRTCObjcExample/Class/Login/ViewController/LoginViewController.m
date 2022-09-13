//
//  LoginViewController.m
//  mobileCMS
//
//  Created by 赵言 on 2019/12/10.
//  Copyright © 2019 赵言. All rights reserved.
//

#import "LoginViewController.h"
#import "MainTabBarController.h"
#import "LoginModel.h"
#import "LoginViewModel.h"

#import "TextFieldView.h"
#import "CommonConfig.h"
#import "CHResolutionView.h"
#import "CustomerDataViewController.h"

@interface LoginViewController ()
<
    YBPopupMenuDelegate,
    TextFieldViewDelegate
>
{
    UIActivityIndicatorView *_loading;
}

@property (strong, nonatomic) LoginViewModel *viewModel;

@property (nonatomic, weak) UIView *bgView;

@property (nonatomic, weak) TextFieldView *environmentField;

@property (nonatomic, weak) TextFieldView *enterprisesField;

@property (nonatomic, weak) TextFieldView *userNameField;

@property (nonatomic, weak) TextFieldView *passwordField;

@property (nonatomic, weak) UIButton *loginBtn;

@property (nonatomic, weak) UIButton *developerButton ;

@property(nonatomic, strong) NSArray *environmentArray;

@property (nonatomic, weak) CHResolutionView *environmentChooseView;

@property(nonatomic, assign) EnvironmentType environmentType;
 
@end

@implementation LoginViewController

-(void)viewDidLoad
{
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];
    
    self.environmentArray = @[@"测试环境",@"开发环境",@"正式环境"];
      
    [self initViews];
    
    _loading = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
    _loading.center = CGPointMake(UIScreen.mainScreen.bounds.size.width/2, UIScreen.mainScreen.bounds.size.height/2);
    [_loading setColor:[UIColor blueColor]];
    [self.bgView addSubview:_loading];
     
    //登录请求
    @weakify(self);
    self.viewModel = [LoginViewModel sharedInstance];
    [RACObserve(self.viewModel, networkState) subscribeNext:^(NSNumber *networkState) {
        @strongify(self);
        if (self.viewModel.networkState == NetworkStateSuccess) {
            [self->_loading stopAnimating];
            if (self.developerButton.selected)
            {
                NSLog(@"NetworkStateSuccess ");
                MainTabBarController *tabBarC = [[MainTabBarController alloc] init];
                tabBarC.selectedIndex = 0;
                if (@available(iOS 13.0, *)) {
                    tabBarC.modalPresentationStyle = UIModalPresentationFullScreen;
                    [self presentViewController:tabBarC animated:NO completion:^{
        
                    }];
                }else{
                   [AppDelegate shareAppDelegate].window.rootViewController = tabBarC;
                }
            }
            else
            {
                CustomerDataViewController *customerVC = [[CustomerDataViewController alloc]init];
                
                customerVC.modalPresentationStyle = UIModalPresentationFullScreen;
                [self presentViewController:customerVC animated:NO completion:nil];
            }
        }
        else if (self.viewModel.networkState == NetworkStateFail)
        {
            [self->_loading stopAnimating];
            [self showErrorView:@"登录信息错误，请检查"];
        }
    }];
    
    [RACObserve(self.enterprisesField.textField, text) subscribeNext:^(NSString *text) {
        self.viewModel.enterpriseId = text.integerValue;
    }];
    
    [RACObserve(self.userNameField.textField, text) subscribeNext:^(NSString *text) {
        self.viewModel.username = text;
    }];
    
    [RACObserve(self.passwordField.textField, text) subscribeNext:^(NSString *text) {
        self.viewModel.password = text;
    }];
}

-(void)initViews
{
    CGFloat margin = 80;
    
    UIView *bgView = [[UIView alloc]initWithFrame:self.view.bounds];
    [self.view addSubview:bgView];
    self.bgView = bgView;
    
    UIImageView *imageView = [[UIImageView alloc]initWithFrame:CGRectMake((self.view.width - 64)/2, 118, 64, 64)];
    imageView.contentMode = UIViewContentModeCenter;
    imageView.image = [UIImage imageNamed:@"ic_launcher233 1"];
    [bgView addSubview:imageView];
        
    UILabel *titleLabel = [[UILabel alloc]initWithFrame:CGRectMake(0, imageView.bottom + 12, self.view.width, 34)];
    titleLabel.text = @"TI-RTC";
    titleLabel.textColor = kHexColor(0x595959);
    titleLabel.font = [UIFont fontWithName:kFontNameMedium size:24];
    titleLabel.textAlignment = NSTextAlignmentCenter;
    [bgView addSubview:titleLabel];
    
    UILabel *subtitleLabel = [[UILabel alloc]initWithFrame:CGRectMake(0, titleLabel.bottom + 5, self.view.width, 18)];
    subtitleLabel.text = @"让客户联络效率更高，体验更好";
    subtitleLabel.textColor = kHexColor(0x8C8C8C);
    subtitleLabel.font = CHFont12;
    subtitleLabel.textAlignment = NSTextAlignmentCenter;
    [bgView addSubview:subtitleLabel];
    
    TextFieldView *environmentField = [[TextFieldView alloc]initWithFrame:CGRectMake(margin, subtitleLabel.bottom + 50, self.view.width - 2 * margin, 35) withType:TextFieldViewType_Environment];

    environmentField.delegate = self;
    [bgView addSubview:environmentField];
    self.environmentField = environmentField;

    TextFieldView *enterprisesField = [[TextFieldView alloc]initWithFrame:CGRectMake(margin, environmentField.bottom + 20, self.view.width - 2 * margin, 35) withType:TextFieldViewType_EnterprisesId];
    enterprisesField.delegate = self;
    enterprisesField.textField.clearButtonMode = UITextFieldViewModeAlways;
    [bgView addSubview:enterprisesField];
    self.enterprisesField = enterprisesField;

    TextFieldView *userNameField = [[TextFieldView alloc]initWithFrame:CGRectMake(margin, enterprisesField.bottom + 20, self.view.width - 2 * margin, 35) withType:TextFieldViewType_UserName];
    userNameField.delegate = self;
    userNameField.textField.clearButtonMode = UITextFieldViewModeAlways;
    [bgView addSubview:userNameField];
    self.userNameField = userNameField;

    TextFieldView *passwordField = [[TextFieldView alloc]initWithFrame:CGRectMake(margin, userNameField.bottom + 20, self.view.width - 2 * margin, 35) withType:TextFieldViewType_Password];
    passwordField.delegate = self;
    passwordField.textField.secureTextEntry = YES;
    [bgView addSubview:passwordField];
    self.passwordField = passwordField;
     
    UIButton *developerButton = [[UIButton alloc]initWithFrame:CGRectMake(margin, passwordField.bottom + 10, 85, 20)];
    [developerButton setImage:[UIImage imageNamed:@"4-单选多选图标"] forState:UIControlStateNormal];
    [developerButton setImage:[UIImage imageNamed:@"4-单选多选图标-1"] forState:UIControlStateSelected];
    [developerButton setTitle:@"开发者模式" forState:UIControlStateNormal];
    [developerButton setTitleColor:kHexColor(0x262626) forState:UIControlStateNormal];
    developerButton.titleLabel.font = CHFont12;
    [developerButton addTarget:self action:@selector(developerButton:) forControlEvents:UIControlEventTouchUpInside];
    developerButton.titleEdgeInsets = UIEdgeInsetsMake(0, 5, 0, -5);
    developerButton.imageEdgeInsets = UIEdgeInsetsMake(0, -5, 0, 0);
    [bgView addSubview:developerButton];
    self.developerButton = developerButton;

    UIButton *loginBtn = [[UIButton alloc]initWithFrame:CGRectMake(margin, developerButton.bottom + 50, self.view.width - 2 * margin, 48)];
    [loginBtn setTitle:@"登录" forState:UIControlStateNormal];
    loginBtn.titleLabel.font = CHFont16;
    [loginBtn setTitleColor:UIColor.whiteColor forState:UIControlStateNormal];
    [loginBtn setBackgroundColor:kHexColor(0xBDD5FF)];
    loginBtn.layer.cornerRadius = 8.0;
    [loginBtn addTarget:self action:@selector(didClickLoginBtnAction) forControlEvents:UIControlEventTouchUpInside];
    [bgView addSubview:loginBtn];
    self.loginBtn = loginBtn;
    loginBtn.userInteractionEnabled = NO;
}

- (void)chooseEnvironment:(BOOL)isSelect
{
    if (isSelect)
    {
        [UIView animateWithDuration:0.25 animations:^{
            self.environmentChooseView.height = self.environmentArray.count *self.environmentChooseView.cellHeight;
        }];
    }
    else
    {
        [UIView animateWithDuration:0.25 animations:^{
            self.environmentChooseView.height = 0;
        }];
    }
}

- (void)developerButton:(UIButton *)button
{
    button.selected = !button.selected;
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    [self.view endEditing:YES];
    
    if (self.environmentChooseView.height)
    {
        [UIView animateWithDuration:0.25 animations:^{
            self.environmentChooseView.height = 0;
        }];
        self.environmentField.rightBtnSelect = NO;
    }
}

//登录按钮
- (void)didClickLoginBtnAction
{    
    // 校验输入的数据存在性
    if(self.viewModel.enterpriseId <= 0)
    {
        [self showErrorView:@"请输入企业账号"];
        return;
    }
    else if (self.viewModel.username.length <= 0)
    {
        [self showErrorView:@"请输入用户名"];
        return;
    }
    else if (self.viewModel.password.length <= 0)
    {
        [self showErrorView:@"请输入密码"];
        return;
    }
    
    //请求登录
    [_loading startAnimating];    
    
    [self.viewModel requestData];
}

- (CHResolutionView *)environmentChooseView
{
    if (!_environmentChooseView)
    {
        CHResolutionView *environmentChooseView = [[CHResolutionView alloc]initWithFrame:CGRectMake(self.environmentField.left, self.environmentField.bottom - 2, self.environmentField.width - 30, 0) withData:self.environmentArray];
        self.environmentChooseView = environmentChooseView;
        [self.bgView addSubview:environmentChooseView];

        CHWeakSelf
        environmentChooseView.resolutionViewButtonClick = ^(EnvironmentType environmentType) {
            weakSelf.environmentType = environmentType;
            weakSelf.environmentField.string = weakSelf.environmentArray[environmentType];
            
            if (environmentType == EnvironmentType_Test)
            {
                weakSelf.viewModel.baseUrl = kBaseUrl_Test;
            }
            else if (environmentType == EnvironmentType_Develop)
            {
                weakSelf.viewModel.baseUrl = kBaseUrl_Develop;
            }
            else if (environmentType == EnvironmentType_Test)
            {
                weakSelf.viewModel.baseUrl = kBaseUrl_Formal;
            }
        };
    }

    return _environmentChooseView;
}

- (BOOL)checkInputItem
{
    [NSData dataWithContentsOfFile:@""];
    // 校验输入的数据存在性
    if(self.viewModel.enterpriseId <= 0)
    {
        [self showErrorView:@"请输入企业账号"];
        return NO;
    }
    else if (self.viewModel.username.length <= 0)
    {
        [self showErrorView:@"请输入用户名"];
        return NO;
    }
    else if (self.viewModel.password.length <= 0)
    {
        [self showErrorView:@"请输入密码"];
        return NO;
    }
    
    return YES;
}

#pragma mark - TextFieldViewDelegate
- (void)rightButtonClick:(BOOL)isSelect
{
    [self chooseEnvironment:isSelect];
}

- (void)textFieldEditing:(UITextField *)textField
{
    if (!self.environmentField.textField.text.length || !self.enterprisesField.textField.text.length || !self.userNameField.textField.text.length || !self.passwordField.textField.text.length)
    {
        [self.loginBtn setBackgroundColor:kHexColor(0xBDD5FF)];
        self.loginBtn.userInteractionEnabled = NO;
    }
    else
    {
        [self.loginBtn setBackgroundColor:kHexColor(0x4385FF)];
        self.loginBtn.userInteractionEnabled = YES;
    }
}

- (void)textFieldBeginEditing
{
    [UIView animateWithDuration:0.25 animations:^{
        self.bgView.y = - 216;
    }];
}

- (void)textFieldEndEditing
{
    [UIView animateWithDuration:0.25 animations:^{
        self.bgView.y = 0;
    }];
}

- (void)dealloc {
    NSLog(@"login vc dealloc!!!!!");
}


@end

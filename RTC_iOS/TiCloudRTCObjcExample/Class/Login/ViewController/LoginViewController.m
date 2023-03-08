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
#import "AppConfig.h"
#import "TextFieldView.h"
#import "CommonConfig.h"
#import "CHResolutionView.h"
#import "CustomerDataViewController.h"
#import "SDKCloudEngine.h"

@interface LoginViewController ()
<
    TextFieldViewDelegate
>
{
    UIActivityIndicatorView *_loading;
}

@property (nonatomic, weak) UIView *bgView;

@property (nonatomic, weak) TextFieldView *enterprisesField;

@property (nonatomic, weak) TextFieldView *userIdField;

@property (nonatomic, weak) TextFieldView *tokenField;

@property (nonatomic, weak) UIButton *loginBtn;

@property (nonatomic, weak) UIButton *rememberBtn;
 
@end

@implementation LoginViewController

- (void)viewWillAppear:(BOOL)animated
{
    NSDictionary *dictInfomation = [[NSUserDefaults standardUserDefaults] valueForKey:kLoginPath];

    if (dictInfomation)
    {
        [self loginDataStored:dictInfomation];
    }
    else
    {
        self.rememberBtn.selected = NO;
        self.loginBtn.userInteractionEnabled = NO;
        [self.loginBtn setBackgroundColor:kHexAColor(0x00865C, 0.5)];
    }
}

-(void)viewDidLoad
{
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];
 
    [self initViews];
    
    _loading = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
    _loading.center = CGPointMake(UIScreen.mainScreen.bounds.size.width/2, UIScreen.mainScreen.bounds.size.height/2);
    [_loading setColor:[UIColor blueColor]];
    [self.bgView addSubview:_loading];
}

-(void)initViews
{
    CGFloat margin = 40;
    
    UIView *bgView = [[UIView alloc]initWithFrame:self.view.bounds];
    [self.view addSubview:bgView];
    self.bgView = bgView;
    
    UIImageView *imageView = [[UIImageView alloc]initWithFrame:CGRectMake((self.view.width - 50)/2, 100, 50, 50)];
    imageView.contentMode = UIViewContentModeCenter;
    imageView.image = [UIImage imageNamed:@"topLogo"];
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

    TextFieldView *enterprisesField = [[TextFieldView alloc]initWithFrame:CGRectMake(margin, subtitleLabel.bottom + 50, self.view.width - 2 * margin, 35) withType:TextFieldViewType_EnterprisesId];
    enterprisesField.placeholder = @"请输入企业ID";
    enterprisesField.delegate = self;
    enterprisesField.textField.clearButtonMode = UITextFieldViewModeAlways;
    [bgView addSubview:enterprisesField];
    self.enterprisesField = enterprisesField;
    
    TextFieldView *userIdField = [[TextFieldView alloc]initWithFrame:CGRectMake(margin, enterprisesField.bottom + 20, self.view.width - 2 * margin, 35) withType:TextFieldViewType_UserName];
    userIdField.placeholder = @"请输入用户Id";
    userIdField.delegate = self;
    userIdField.textField.clearButtonMode = UITextFieldViewModeAlways;
    [bgView addSubview:userIdField];
    self.userIdField = userIdField;

    TextFieldView *tokenField = [[TextFieldView alloc]initWithFrame:CGRectMake(margin, userIdField.bottom + 20, self.view.width - 2 * margin, 35) withType:TextFieldViewType_UserName];
    tokenField.placeholder = @"请输入accessToken";
    tokenField.delegate = self;
    tokenField.textField.clearButtonMode = UITextFieldViewModeAlways;
    [bgView addSubview:tokenField];
    self.tokenField = tokenField;
    
    UIButton *rememberBtn = [[UIButton alloc]initWithFrame:CGRectMake(tokenField.right - 80, tokenField.bottom + 10, 80, 20)];
    [rememberBtn setTitle:@"记住密码" forState:UIControlStateNormal];
    rememberBtn.titleLabel.font = CHFont14;
    [rememberBtn setTitleColor:kRGBColor(73, 129, 96) forState:UIControlStateNormal];
    [rememberBtn setImage:[UIImage imageNamed:@"4-单选多选图标"] forState:UIControlStateNormal];
    [rememberBtn setImage:[UIImage imageNamed:@"4-单选多选图标-1"] forState:UIControlStateSelected];
    [rememberBtn addTarget:self action:@selector(didClickToRememberPassword:) forControlEvents:UIControlEventTouchUpInside];
    [bgView addSubview:rememberBtn];
    self.rememberBtn = rememberBtn;
    
    rememberBtn.titleEdgeInsets = UIEdgeInsetsMake(0, -(rememberBtn.imageView.frame.size.width + 2.5), 0, (rememberBtn.imageView.frame.size.width + 2.5));
    rememberBtn.imageEdgeInsets = UIEdgeInsetsMake(0, (rememberBtn.titleLabel.frame.size.width + 2.5), 0, -(rememberBtn.titleLabel.frame.size.width + 2.5));
    
    UIButton *loginBtn = [[UIButton alloc]initWithFrame:CGRectMake(20, self.view.height - 100 , self.view.width - 40, 48)];
    [loginBtn setTitle:@"登录" forState:UIControlStateNormal];
    loginBtn.titleLabel.font = CHFont16;
    [loginBtn setTitleColor:UIColor.whiteColor forState:UIControlStateNormal];
    loginBtn.layer.cornerRadius = 8.0;
    [loginBtn addTarget:self action:@selector(didClickLoginBtnAction) forControlEvents:UIControlEventTouchUpInside];
    [bgView addSubview:loginBtn];
    self.loginBtn = loginBtn;
}

//登录按钮
- (void)didClickLoginBtnAction
{
    // 校验输入的数据存在性
    if(self.enterprisesField.textField.text.length <= 0)
    {
        [self showErrorView:@"请输入企业账号"];
        return;
    }
    else if (self.userIdField.textField.text.length <= 0)
    {
        [self showErrorView:@"请输入accessToken"];
        return;
    }
    else if (self.tokenField.textField.text.length <= 0)
    {
        [self showErrorView:@"请输入accessToken"];
        return;
    }
    
    // 保存登录数据
    LoginModel *model = [LoginModel loginModel];
    model.enterpriseId = [self.enterprisesField.textField.text integerValue];
    model.userName = self.userIdField.textField.text;
    model.accessToken = self.tokenField.textField.text;
    model.baseUrl = kBaseUrl_Test;
    [[LoginModel loginModel] saveLoginModel:model];
 
    //请求登录
    [_loading startAnimating];    
    
    // 登录SDK
    [self login];
    
    NSDictionary *dict = @{@"enterprises":self.enterprisesField.textField.text,@"userId":self.userIdField.textField.text,@"accessToken":self.tokenField.textField.text};
    [[NSUserDefaults standardUserDefaults] setValue:dict forKey:kLoginPath];
}

- (void)login
{
    TiCloudRTCEngineConfig * config = [[TiCloudRTCEngineConfig alloc] init];
    config.rtcEndpoint = kBaseUrl_Test;
    config.enterpriseId = [self.enterprisesField.textField.text integerValue];
    config.userId = self.userIdField.textField.text;
    
    config.accessToken = self.tokenField.textField.text;
    
    __weak typeof(self) weakSelf = self;
    TiCloudRTCEngine *tiCloudEngine = [TiCloudRTCEngine createClient:config success:^{
            NSLog(@"createClient success..");
        [self->_loading stopAnimating];
        
        MainTabBarController *tabBarC = [[MainTabBarController alloc] init];
        tabBarC.selectedIndex = 0;
        if (@available(iOS 13.0, *)) {
            tabBarC.modalPresentationStyle = UIModalPresentationFullScreen;
            [weakSelf presentViewController:tabBarC animated:NO completion:^{
                
            }];
        }else{
            [AppDelegate shareAppDelegate].window.rootViewController = tabBarC;
        }
        
        weakSelf.enterprisesField.textField.text = nil;
        weakSelf.tokenField.textField.text = nil;
        
    } error:^(TiCloudRtcErrCode nErrorCode, NSString * _Nonnull errorDes) {
        NSLog(@"createClient error %@ ",errorDes);
        
        [self->_loading stopAnimating];
        [weakSelf showErrorView:[NSString stringWithFormat:@"登录失败：%ld--%@",nErrorCode,errorDes]];
    }];
    
    [SDKCloudEngine sharedInstance].tiCloudEngine = tiCloudEngine;
    
    NSString *SDKVersion = [TiCloudRTCEngine getVersion];
    [[NSUserDefaults standardUserDefaults] setValue:SDKVersion forKey:kSDKVersonPath];
}

- (void)didClickToRememberPassword:(UIButton *)button
{
    button.selected = !button.selected;
}

#pragma mark - TextFieldViewDelegate

- (void)textFieldEditing:(TextFieldView *)textFieldView
{
    if (self.enterprisesField.textField.text.length && self.userIdField.textField.text.length  && self.tokenField.textField.text.length)
    {
        [self.loginBtn setBackgroundColor:kHexColor(0x00865C)];
        self.loginBtn.userInteractionEnabled = YES;
    }
    else
    {
        [self.loginBtn setBackgroundColor:kHexAColor(0x00865C, 0.5)];
        self.loginBtn.userInteractionEnabled = NO;
    }
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    [self.view endEditing:YES];
}

- (void)loginDataStored:(NSDictionary *)dictInfomation
{
    self.enterprisesField.string = dictInfomation[@"enterprises"];
    self.userIdField.string = dictInfomation[@"userId"];
    self.tokenField.string = dictInfomation[@"accessToken"];
    
    if (self.enterprisesField.string.length && self.userIdField.string.length && self.tokenField.string.length )
    {
        self.rememberBtn.selected = YES;
        
        self.loginBtn.userInteractionEnabled = YES;
        [self.loginBtn setBackgroundColor:kHexColor(0x00865C)];
    }
    else
    {
        self.rememberBtn.selected = NO;
        self.loginBtn.userInteractionEnabled = NO;
        [self.loginBtn setBackgroundColor:kHexAColor(0x00865C, 0.5)];
    }
}

- (void)dealloc {
    NSLog(@"login vc dealloc!!!!!");
}

@end

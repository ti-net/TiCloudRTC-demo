//
//  MineViewController.m
//  mobileCMS
//
//  Created by 赵言 on 2019/12/11.
//  Copyright © 2019 赵言. All rights reserved.
//

#import "MineViewController.h"
#import "SDKCloudEngine.h"
#import "LoginViewController.h"

@interface MineViewController () <UITableViewDelegate, UITableViewDataSource>

@property (nonatomic, weak) UITableView *tableView;

@property (weak, nonatomic) UITextField *textField;

@property (nonatomic, copy) NSString *appVerson;

@property (nonatomic, copy) NSString *SDKVerson;

@end

@implementation MineViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    //获取app版本信息
    NSDictionary *infoDictionary = [[NSBundle mainBundle] infoDictionary];
    
    self.appVerson = [infoDictionary objectForKey:@"CFBundleShortVersionString"];
    self.SDKVerson = [[NSUserDefaults standardUserDefaults] valueForKey:kSDKVersonPath];
}

- (void)setupSubviews
{
    self.navigationItem.title = @"我的";
    
    UITableView *tableView = [[UITableView alloc]initWithFrame:CGRectMake(0.f, 0, self.view.width, self.view.height) style:UITableViewStyleGrouped];
    
    tableView.delegate = self;
    tableView.dataSource = self;
    tableView.sectionHeaderHeight = 0.f;
    tableView.sectionFooterHeight = 15.f;
    tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.tableView = tableView;
    tableView.tableHeaderView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, self.view.width, 5)];
    [self.view addSubview:tableView];

    UIButton *logoutBtn = [[UIButton alloc]initWithFrame:CGRectMake(50, self.view.height - kNavTop - kTabBarHeight - 30 - 45, self.view.width - 100, 45)];
    [logoutBtn setTitle:@"退出登录" forState:UIControlStateNormal];
    logoutBtn.titleLabel.font = CHFont16;
    [logoutBtn setBackgroundColor:kHexColor(0x4385FF)];
    [logoutBtn addTarget:self action:@selector(logoutButtonClick) forControlEvents:UIControlEventTouchUpInside];
    logoutBtn.layer.cornerRadius = 8;
    [self.view addSubview:logoutBtn];
}

- (void)logoutButtonClick
{
    [[SDKCloudEngine sharedInstancet].tiCloudEngine destroyClient:^{
        if (@available(iOS 13.0, *))
        {
            [self dismissViewControllerAnimated:YES completion:nil];
        }
        else
        {
            LoginViewController *loginVC = [[LoginViewController alloc]init];
            
            [AppDelegate shareAppDelegate].window.rootViewController = loginVC;
        }
        
    } error:^(TiCloudRtcErrCode errorCode, NSString * _Nonnull errorMessage) {
        [self showErrorView:[NSString stringWithFormat:@"退出失败:%@",errorMessage]];
    }];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    static NSString *key = @"MineViewControllerCell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:key];
    
    if (!cell)
    {
        cell = [[UITableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:key];
    }
    
    if (!indexPath.section)
    {
        cell.textLabel.text = [NSString stringWithFormat:@"应用版本：%@",self.appVerson];
    }
    else if (indexPath.section)
    {
        cell.textLabel.text = [NSString stringWithFormat:@"SDK 版本：%@",self.SDKVerson];
    }
    
    return cell;
}

@end

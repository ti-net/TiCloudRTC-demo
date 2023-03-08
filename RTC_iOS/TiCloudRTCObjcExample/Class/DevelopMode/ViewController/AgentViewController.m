//
//  AgentViewController.m
//  TiCloudRTCObjcExample
//
//  Created by 高延波 on 2022/8/23.
//

#import "AgentViewController.h"
#import "DrawShapeView.h"

@interface AgentViewController ()

/// 客服号码
@property(nonatomic, weak) UIButton *serviceNumber;

@property(nonatomic, weak) UIButton *rootBtn;

@property(nonatomic, weak) UIButton *chooseBtn;

@property(nonatomic, weak) UIButton *queueBtn;

@property(nonatomic, weak) UIButton *directBtn;

@property(nonatomic, weak) UITextField *alongRoadTF;

@property(nonatomic, assign) NSInteger selectNode;


@end

@implementation AgentViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (void)setupSubviews
{
    CGFloat LeftMargin = 20;
    CGFloat ButtonHeight = 30;
    CGFloat nodeButtonW = 70;
    
    CGFloat nodeMargin = (self.view.width - 3 * nodeButtonW)/4;
    
    UIButton *serviceNumber = [[UIButton alloc]initWithFrame:CGRectMake(LeftMargin, 50, 150, 40)];
    [serviceNumber setTitle:@"95092" forState:UIControlStateNormal];
    [serviceNumber setTitleColor:UIColor.blackColor forState:UIControlStateNormal];
    serviceNumber.titleLabel.font = CHFont(35);
    serviceNumber.imageEdgeInsets = UIEdgeInsetsMake(0, -10, 0, 0);
    [self.view addSubview:serviceNumber];
    self.serviceNumber = serviceNumber;
    
    UIButton *topCallBtn = [self creatButtonWithFrame:CGRectMake(self.view.width - LeftMargin - 100, serviceNumber.top, 100, ButtonHeight) title:@"联系客服" action:@selector(callButtonsClick:)];
    [topCallBtn setBackgroundColor:kHexColor(0x00865C)];
    topCallBtn.tag = 100;
        
    UIView *dividerLine = [[UIView alloc]initWithFrame:CGRectMake(0, topCallBtn.bottom + 30, self.view.width, 1.0)];
    dividerLine.backgroundColor = UIColor.grayColor;
    [self.view addSubview:dividerLine];
    
    UIButton *rootBtn = [self creatButtonWithFrame:CGRectMake(0, dividerLine.bottom + 20, nodeButtonW, ButtonHeight) title:@"根节点" action:@selector(nodeButtonsClick:)];
    [rootBtn setBackgroundColor:kRGBColor(89, 154, 210)];
    rootBtn.tag = 0;
    self.rootBtn = rootBtn;
    rootBtn.centerX = self.view.centerX;
       
    UIButton *directBtn = [self creatButtonWithFrame:CGRectMake(nodeMargin, rootBtn.bottom + 50, nodeButtonW, ButtonHeight) title:@"直呼节点" action:@selector(nodeButtonsClick:)];
    directBtn.tag = 3;
    self.directBtn = directBtn;
    
    
    UIButton *chooseBtn = [self creatButtonWithFrame:CGRectMake(directBtn.right +nodeMargin, directBtn.top, nodeButtonW, ButtonHeight) title:@"播放节点" action:@selector(nodeButtonsClick:)];
    chooseBtn.tag = 1;
    self.chooseBtn = chooseBtn;
    
    UIButton *queueBtn = [self creatButtonWithFrame:CGRectMake(chooseBtn.right +nodeMargin, chooseBtn.top, nodeButtonW, ButtonHeight) title:@"队列节点" action:@selector(nodeButtonsClick:)];
    queueBtn.tag = 2;
    self.queueBtn = queueBtn;
    
    /*
    UIButton *chooseBtn = [self creatButtonWithFrame:CGRectMake(nodeMargin, rootBtn.bottom + 50, nodeButtonW, ButtonHeight) title:@"选择" action:@selector(nodeButtonsClick:)];
    chooseBtn.tag = 1;
    self.chooseBtn = chooseBtn;

    UIButton *queueBtn = [self creatButtonWithFrame:CGRectMake(chooseBtn.right +nodeMargin, chooseBtn.top, nodeButtonW, ButtonHeight) title:@"队列" action:@selector(nodeButtonsClick:)];
    queueBtn.tag = 2;
    self.queueBtn = queueBtn;


    UIButton *directBtn = [self creatButtonWithFrame:CGRectMake(queueBtn.right +nodeMargin, queueBtn.top, nodeButtonW, ButtonHeight) title:@"直呼" action:@selector(nodeButtonsClick:)];
    directBtn.tag = 3;
    self.directBtn = directBtn;
     */
    
    UIButton *buttonCallBtn = [self creatButtonWithFrame:CGRectMake(LeftMargin, self.view.height - kNavTop - kTabBarHeight - 100 - 45, self.view.width - 2 *LeftMargin, 40) title:@"联系客服" action:@selector(callButtonsClick:)];
    [buttonCallBtn setBackgroundColor:kHexColor(0x00865C)];
    buttonCallBtn.tag = 101;

    UITextField *alongRoadTF = [[UITextField alloc] init];
    alongRoadTF.frame = CGRectMake(LeftMargin, directBtn.bottom + 20, self.view.width - 2 *LeftMargin, 40.f);
    alongRoadTF.font = CHFont13;
    alongRoadTF.textAlignment = NSTextAlignmentCenter;
    alongRoadTF.placeholder = @"请输入随路数据(可选)";
    alongRoadTF.layer.borderWidth = 1.0;
    alongRoadTF.layer.borderColor = UIColor.grayColor.CGColor;
    alongRoadTF.layer.cornerRadius = 6;
    [self.view addSubview:alongRoadTF];
    self.alongRoadTF = alongRoadTF;
        
    [self nodesLine];
}

- (UIButton *)creatButtonWithFrame:(CGRect)frame title:(NSString *)title action:(SEL)action
{
    UIButton *button = [[UIButton alloc]initWithFrame:frame];
    [button setTitle:title forState:UIControlStateNormal];
    button.titleLabel.font = CHFont13;
    [button setBackgroundColor:UIColor.grayColor];
    [button addTarget:self action:action forControlEvents:UIControlEventTouchUpInside];
    button.layer.cornerRadius = 6;
    [self.view addSubview:button];
    return button;
}

/// 划节点间的线

- (void)nodesLine
{
    UIView *line1 = [[UIView alloc]initWithFrame:CGRectMake(_rootBtn.centerX, _rootBtn.bottom, 1.0, _chooseBtn.top - _rootBtn.bottom)];
    line1.backgroundColor = UIColor.grayColor;
    [self.view addSubview:line1];
    
    UIView *line2 = [[UIView alloc]initWithFrame:CGRectMake(_directBtn.centerX, _directBtn.top - 20, 1.0, 20)];
    line2.backgroundColor = UIColor.grayColor;
    [self.view addSubview:line2];
    
    UIView *line3 = [[UIView alloc]initWithFrame:CGRectMake(_directBtn.centerX, line2.top, _queueBtn.centerX - _directBtn.centerX, 1.0)];
    line3.backgroundColor = UIColor.grayColor;
    [self.view addSubview:line3];
    
    UIView *line4 = [[UIView alloc]initWithFrame:CGRectMake(line3.right, line3.bottom, 1.0, _queueBtn.top - line3.bottom)];
    line4.backgroundColor = UIColor.grayColor;
    [self.view addSubview:line4];
}

- (void)callButtonsClick:(UIButton *)button
{
    TiCloudRTCCallConfig * callConf = [[TiCloudRTCCallConfig alloc] init];
    if (button.tag == 100)
    {
        callConf.type = TiCloudRtcScence_AGENTSCENCE;
    }
    else if (button.tag == 101)
    {
        callConf.type = TiCloudRtcScence_AGENTSCENCE;
        
        if (self.selectNode)
        {// 节点呼叫
            NSDictionary *userField = @{@"name":@"ivrNode",@"value":[NSString stringWithFormat:@"%ld",self.selectNode],@"type":@1};
            
            NSString *string = [AppConfig toNSStringWithDictionary:userField];
            
            string = [string stringByReplacingOccurrencesOfString:@"\r\n"withString:@""];
            string = [string stringByReplacingOccurrencesOfString:@"\n"withString:@""];
            
            if (self.alongRoadTF.text.length)
            {
                string = [NSString stringWithFormat:@"%@%@",string,self.alongRoadTF.text];
            }
            
            callConf.userField = string;
        }
    }
    
    CHWeakSelf
    [[SDKCloudEngine sharedInstance].tiCloudEngine call:callConf success:^{
        NSLog(@"call ... success");
        [weakSelf showTelephoneView:callConf.tel];
    } error:^(TiCloudRtcErrCode nErrorCode, NSString * _Nonnull errorDes) {
        NSLog(@"call ... error = %@",errorDes);
    }];
}

- (void)nodeButtonsClick:(UIButton *)button
{
    self.selectNode = button.tag;
    
    if (button.tag == 0)
    {
        self.rootBtn.backgroundColor = kRGBColor(89, 154, 210);
        self.chooseBtn.backgroundColor = UIColor.grayColor;
        self.queueBtn.backgroundColor = UIColor.grayColor;
        self.directBtn.backgroundColor = UIColor.grayColor;
    }
    else if (button.tag == 1)
    {
        self.rootBtn.backgroundColor =  UIColor.grayColor;
        self.chooseBtn.backgroundColor = kRGBColor(89, 154, 210);
        self.queueBtn.backgroundColor = UIColor.grayColor;
        self.directBtn.backgroundColor = UIColor.grayColor;
    }
    else if (button.tag == 2)
    {
        self.rootBtn.backgroundColor =  UIColor.grayColor;
        self.chooseBtn.backgroundColor = UIColor.grayColor;
        self.queueBtn.backgroundColor = kRGBColor(89, 154, 210);
        self.directBtn.backgroundColor = UIColor.grayColor;
    }
    else if (button.tag == 3)
    {
        self.rootBtn.backgroundColor = UIColor.grayColor;
        self.chooseBtn.backgroundColor = UIColor.grayColor;
        self.queueBtn.backgroundColor = UIColor.grayColor;
        self.directBtn.backgroundColor = kRGBColor(89, 154, 210);
    }
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    [self.view endEditing:YES];
}

@end

//
//  DemoPageController.m
//  TiCloudRTCObjcExample
//
//  Created by 高延波 on 2022/8/23.
//

#import "DemoPageController.h"
#import "OutCallViewController.h"
#import "AgentViewController.h"
#import "SDKCloudEngine.h"

@interface DemoPageController ()
<
    WMPageControllerDelegate,
    WMPageControllerDataSource
>

// 客服场景
@property (nonatomic, strong) AgentViewController *agentVC;
// 外呼场景
@property (nonatomic, strong) OutCallViewController *outCallVC;
// page列表数据
@property (nonatomic, strong) NSArray *pageTitleArray;

@end

@implementation DemoPageController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = @"演示";
    
    self.pageTitleArray = @[@"外呼场景", @"客服场景"];
        
    [self.view addSubview:self.pageController.view];
        
    self.outCallVC.isSelectPage = YES;
}

#pragma mark - WMPageControllerDataSource
- (NSInteger)numbersOfChildControllersInPageController:(WMPageController *)pageController {
    return self.pageTitleArray.count;
}

- (NSString *)pageController:(WMPageController *)pageController titleAtIndex:(NSInteger)index {
    return self.pageTitleArray[index];
}

- (UIViewController *)pageController:(WMPageController *)pageController viewControllerAtIndex:(NSInteger)index
{
    OutCallViewController *outCallVC = [[OutCallViewController alloc] init];        switch (index) {
            case 0:
                self.outCallVC = outCallVC;
                break;
            case 1: {
                AgentViewController * agentVC = [[AgentViewController alloc] init];
                self.agentVC = agentVC;
                return agentVC;
                break;
            }
            default:
                self.outCallVC = outCallVC;
                break;
        }
    return outCallVC;
}

- (void)pageController:(WMPageController *)pageController didEnterViewController:(__kindof UIViewController *)viewController withInfo:(NSDictionary *)info
{
    
    if ([info[@"index"] intValue])
    {
        self.agentVC.isSelectPage = YES;
    }
    else
    {
        self.outCallVC.isSelectPage = YES;
    }
}

- (CGRect)pageController:(WMPageController *)pageController preferredFrameForMenuView:(WMMenuView *)menuView {
    return CGRectMake(0.f, 0.f, self.view.width, kWMPageHeight - 1.f);
}

- (CGRect)pageController:(WMPageController *)pageController preferredFrameForContentView:(WMScrollView *)contentView {
    return CGRectMake(0,  kWMPageHeight, self.view.width, self.view.height - kWMPageHeight);
}

- (WMPageController *)pageController {
    if (!_pageController) {
        _pageController = [[WMPageController alloc] init];
        _pageController.titles = self.pageTitleArray;
        _pageController.delegate            = self;
        _pageController.dataSource          = self;
        [_pageController tr_SetupStyle];
    }
    return _pageController;
}

@end

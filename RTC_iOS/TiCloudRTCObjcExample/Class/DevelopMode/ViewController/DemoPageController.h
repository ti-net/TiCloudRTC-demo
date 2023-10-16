//
//  DemoPageController.h
//  TiCloudRTCObjcExample
//
//  Created by 高延波 on 2022/8/23.
//

#import "BaseViewController.h"
#import <WMPageController/WMPageController.h>

NS_ASSUME_NONNULL_BEGIN

@interface DemoPageController : BaseViewController

@property (nonatomic, strong) WMPageController *pageController;

//@property (nonatomic, copy) void(^dissmisPage)(void);

@end

NS_ASSUME_NONNULL_END

//
//  MainTabBarController.m
//  TIMClientDemo
//
//  Created by YanBo on 2020/3/28.
//  Copyright © 2020 YanBo. All rights reserved.
//

#import "MainTabBarController.h"
#import "BaseNavigationController.h"
#import "MineViewController.h"


#define kClassKey   @"rootVCClassString"
#define kTitleKey   @"title"
#define kImgKey     @"imageName"
#define kSelImgKey  @"selectedImageName"


@interface MainTabBarController ()

@end

@implementation MainTabBarController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
  NSArray *childItemsArray = @[
      
                              @{kClassKey  : @"DemoPageController",
                                kTitleKey  : @"拨号键盘",
                                kImgKey    : @"Frame 128",
                                kSelImgKey : @"Frame 128-1"},
                              @{kClassKey  : @"MineViewController",
                                kTitleKey  : @"我的",
                                kImgKey    : @"线性／people",
                                kSelImgKey : @"线性／people-1"},
  ];
   
   [childItemsArray enumerateObjectsUsingBlock:^(NSDictionary *dict, NSUInteger idx, BOOL *stop) {
       UIViewController *vc = [NSClassFromString(dict[kClassKey]) new];
       vc.title = dict[kTitleKey];
       BaseNavigationController *nav = [[BaseNavigationController alloc] initWithRootViewController:vc];
       UITabBarItem *item = nav.tabBarItem;
       item.title = dict[kTitleKey];
       
       item.image = [UIImage imageNamed:dict[kImgKey]];
       
       UIImage * homeSelectImge = [UIImage imageNamed:dict[kSelImgKey]];
           //第一种解决方法:因为系统默认是将我们选中的图片渲染为蓝色的,所以在这里我们可以将选中的图片设置为初始值, 使其不被渲染就可以;这种方法需要我们设置每一个tabBarItem的selectedImage属性,比较繁琐;
       homeSelectImge = [homeSelectImge imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
       item.selectedImage = homeSelectImge;
       [item setTitleTextAttributes:@{NSForegroundColorAttributeName : kRGBColor(73, 129, 96)} forState:UIControlStateSelected];
       [self addChildViewController:nav];
   }];
}

- (void)dealloc {
    NSLog(@"tab vc dealloc!!!!!");
}

@end

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
                                kTitleKey  : @"演示",
                                kImgKey    : @"icon_main_keyboard",
                                kSelImgKey : @"icon_main_keyboard_select"},
                              @{kClassKey  : @"MineViewController",
                                kTitleKey  : @"我的",
                                kImgKey    : @"icon_main_mine",
                                kSelImgKey : @"icon_main_mine_select"},
  ];
   
   [childItemsArray enumerateObjectsUsingBlock:^(NSDictionary *dict, NSUInteger idx, BOOL *stop) {
       UIViewController *vc = [NSClassFromString(dict[kClassKey]) new];
       vc.title = dict[kTitleKey];
       BaseNavigationController *nav = [[BaseNavigationController alloc] initWithRootViewController:vc];
       UITabBarItem *item = nav.tabBarItem;
       item.title = dict[kTitleKey];
       item.image = [UIImage imageNamed:dict[kImgKey]];
       item.selectedImage = [[UIImage imageNamed:dict[kSelImgKey]] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
       [item setTitleTextAttributes:@{NSForegroundColorAttributeName : [UIColor colorWithRed:0 green:(190 / 255.0) blue:(12 / 255.0) alpha:1]} forState:UIControlStateSelected];
       [self addChildViewController:nav];
   }];
}

- (void)dealloc {
    NSLog(@"tab vc dealloc!!!!!");
}

@end

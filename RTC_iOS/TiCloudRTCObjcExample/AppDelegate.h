//
//  AppDelegate.h
//  TiCloudRTCObjcExample
//
//  Created by 高延波 on 2022/8/19.
//

#import <UIKit/UIKit.h>
#import "LoginViewController.h"

@interface AppDelegate : UIResponder <UIApplicationDelegate>

@property (strong, nonatomic) UIWindow *window;

@property (strong, nonatomic) LoginViewController *loginVC;

/**
 是否已经弹出键盘 用于知识圈的判断
 */
@property (nonatomic, assign, getter = isShowKeyboard) BOOL showKeyboard;

+ (AppDelegate* )shareAppDelegate;
@end


//
//  NSObject+ShowError.m
//  SmartHome
//
//  Created by 赵言 on 2019/7/4.
//  Copyright © 2019 赵言. All rights reserved.
//

#import "NSObject+ShowError.h"
#import "MBProgressHUD+GeneralConfiguration.h"
#import "AppDelegate.h"

@implementation NSObject (ShowError)

- (void)showErrorView:(NSString *)str {
    if ([str isEqualToString:@"Access Denied"] || [str isEqualToString:@"OK"] || [str isEqualToString:@"ok"]) {
        return;
    }
    dispatch_async(dispatch_get_main_queue(), ^{
        MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:[self getKeyWindow] animated:YES];
        hud.mode = MBProgressHUDModeText;
        hud.margin = 10.f;
        hud.detailsLabel.text = str;
        hud.detailsLabel.textColor = kHexColor(0xFF4D4F);
//        hud.offset = CGPointMake(kWindowWidth/2 - hud.width/2, kWindowHeight/4);
//        [hud setupMBProgress];
        hud.removeFromSuperViewOnHide = YES;
        [hud hideAnimated:YES afterDelay:2.f];
    });
}

- (UIWindow *)getKeyWindow
{
    if (@available(iOS 13.0, *)) {
        for (UIWindowScene *scene in UIApplication.sharedApplication.connectedScenes) {
            for (UIWindow *window in scene.windows) {
                if (window.isKeyWindow) {
                    return window;
                }
            }
        }
        
    } else {
        return [UIApplication sharedApplication].keyWindow;
    }
    return nil;
}

@end

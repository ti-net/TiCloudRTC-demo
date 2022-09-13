//
//  BaseCallViewController.h
//  TiCloudRTCObjcExample
//
//  Created by 马迪 on 2022/9/5.
//

#import "BaseViewController.h"
#import "TelephoneView.h"
#import "AppConfig.h"
#import "SDKCloudEngine.h"

NS_ASSUME_NONNULL_BEGIN

@interface BaseCallViewController : BaseViewController
<
    TiCloudRTCEventDelegate,
    TelephoneViewDelegate
>

// SDK引擎实例
@property(nonatomic, strong) SDKCloudEngine * SDKEngine;

@property (nonatomic, weak)TelephoneView *telephoneView;

@property (nonatomic, assign) BOOL isSelectPage;

- (void)showTelephoneView:(NSString *)phoneNumber;

@end

NS_ASSUME_NONNULL_END

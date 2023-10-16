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

@property (nonatomic, weak)TelephoneView *telephoneView;

@property (nonatomic, assign) BOOL isSelectPage;

@property (nonatomic, copy) void(^dismissPage)(void);

- (void)showTelephoneView:(NSString *)phoneNumber;

@end

NS_ASSUME_NONNULL_END

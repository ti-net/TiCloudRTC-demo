//
//  TelephoneView.h
//  TiCloudRTCObjcExample
//
//  Created by 马迪 on 2022/9/3.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@protocol TelephoneViewDelegate <NSObject>

-(void)hangupButtonClick;

-(void)numberButtonsClick:(NSString *)number;

-(void)localAudioButtonClick:(BOOL)isSelect;

-(void)speakphoneButtonClick:(BOOL)isSelect;

@end

@interface TelephoneView : UIView

@property(nonatomic, copy) NSString *phoneNumber;

@property(nonatomic, weak) id<TelephoneViewDelegate> delegate;

@property(nonatomic, assign) BOOL speakEnable;

/// 开始呼叫
- (void)callingStart;
/// 开始通话
- (void)callingReceive;
/// 通话结束
- (void)callingEnd;

+ (instancetype)sharedInstance;

@end

NS_ASSUME_NONNULL_END

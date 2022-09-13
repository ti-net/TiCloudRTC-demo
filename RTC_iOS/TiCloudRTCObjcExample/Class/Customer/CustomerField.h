//
//  CustomerField.h
//  TiCloudRTCObjcExample
//
//  Created by 马迪 on 2022/9/6.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN


typedef enum : NSUInteger
{
    CustomerFieldType_One,
    CustomerFieldType_Two,
    CustomerFieldType_Three,
} CustomerFieldType;

@protocol CustomerFieldDelegate <NSObject>

- (void)startCall;

- (void)textFieldEdited:(UITextField *)textField;

@end

@interface CustomerField : UIView

@property (nonatomic, weak, readonly) UITextField *textField;

@property (nonatomic, weak) id<CustomerFieldDelegate> delegate;

- (instancetype)initWithFrame:(CGRect)frame withType:(CustomerFieldType)fieldType;

@end

NS_ASSUME_NONNULL_END

//
//  TextFieldView.h
//  TiCloudRTCObjcExample
//
//  Created by 马迪 on 2022/9/2.
//

#import <UIKit/UIKit.h>
@class TextFieldView;
NS_ASSUME_NONNULL_BEGIN

typedef enum : NSUInteger
{
    TextFieldViewType_Environment,
    TextFieldViewType_EnterprisesId,
    TextFieldViewType_UserName,
    TextFieldViewType_Password,
} TextFieldViewType;

typedef enum : NSUInteger
{
    EnvironmentType_Test,
    EnvironmentType_Develop,
    EnvironmentType_Formal,
} EnvironmentType;

@protocol TextFieldViewDelegate <NSObject>

- (void)rightButtonClick:(BOOL)isSelect;

- (void)textFieldBeginEditing;

- (void)textFieldEndEditing;

- (void)textFieldEditing:(TextFieldView *)textFieldView;

@end

@interface TextFieldView : UIView

- (instancetype)initWithFrame:(CGRect)frame withType:(TextFieldViewType)viewType;

@property(nonatomic, assign) EnvironmentType environmentType;

@property (nonatomic, weak) UITextField *textField;

@property(nonatomic, copy, nullable) NSString *string;

@property(nonatomic, assign) BOOL rightBtnSelect;

@property (nonatomic, weak) id<TextFieldViewDelegate> delegate;


@end

NS_ASSUME_NONNULL_END

//
//  BaseTableViewCell.h
//  SmartHome
//
//  Created by 赵言 on 2019/7/4.
//  Copyright © 2019 赵言. All rights reserved.
//  所有TableViewCell的基类

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@protocol CustomCellTextFieldTableCellDelegate <NSObject>

- (void)getNumDict:(NSDictionary*)dict;//主表单计算
- (void)getChildNumDict:(NSDictionary*)dict childModelId:(NSString*)childModelId;//子表单计算

@end

@interface BaseTableViewCell : UITableViewCell

/// 普通的cell创建
/// @param tableView tableView
/// @param reuseIdentifie cell的标识符
+ (instancetype)cellWithTableView:(UITableView *)tableView reuseIdentifie:(NSString *)reuseIdentifie;

/// xib类型的cell创建
/// @param tableView tableView
/// @param reuseIdentifie cell的标识符
+ (instancetype)cellXibWithTableView:(UITableView *)tableView reuseIdentifie:(NSString *)reuseIdentifie;

//工单合计
@property (weak, nonatomic) id <CustomCellTextFieldTableCellDelegate> numTextdelegate;

//@property(nonatomic,strong)NSIndexPath*index;

- (void)setWithModel:(id)model index:(NSIndexPath*)index;

- (void)setWithModel:(id)model markDict:(NSDictionary*)dict;

- (void)setWithModel:(id)model topic:(NSString*)topic levelArr:(NSArray*)levelArr levelName:(NSString*)levelName;

- (void)setupSubviews;
- (void)defineLayout;

/// 设置Model数据
/// @param model 数据
- (void)setWithModel:(id)model;

@property (nonatomic, strong) BaseViewController *currentVC;

@end

NS_ASSUME_NONNULL_END

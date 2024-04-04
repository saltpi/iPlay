//
//  PlayerNumberValueView.h
//  iPlayClient
//
//  Created by 赫拉 on 2024/4/3.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface PlayerNumberValueView : UIView
@property (nonatomic, strong) UISlider *sliderBar;
@property (nonatomic, strong) UIImageView *iconView;

@property (nonatomic, copy) NSString *iconName;
@property (nonatomic) NSUInteger maxValue;
@property (nonatomic) NSUInteger minValue;
@property (nonatomic) NSUInteger progress;
@end

NS_ASSUME_NONNULL_END

//
//  PlayerEventView.h
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/20.
//

#import <UIKit/UIKit.h>
#import "PlayerNumberValueView.h"

NS_ASSUME_NONNULL_BEGIN

@protocol PlayerEventDelegate
- (void)playerGestureEvent:(UIGestureRecognizer *)gesture
                  location:(CGPoint)location;
@end

@interface PlayerEventView : UIView
@property (nonatomic, strong) PlayerNumberValueView *numberValueView;
@property (nonatomic, copy) NSArray<UIView *> *ignoreViews;
@property (nonatomic, weak) id<PlayerEventDelegate> eventDelegate;

- (void)showBrightnessIndicator:(BOOL)visible;
- (void)showVolumeIndicator:(BOOL)visible;
@end

NS_ASSUME_NONNULL_END

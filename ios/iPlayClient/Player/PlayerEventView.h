//
//  PlayerEventView.h
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/20.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@protocol PlayerEventDelegate
- (void)playerGestureEvent:(UIGestureRecognizer *)gesture
                  location:(CGPoint)location;
@end

@interface PlayerEventView : UIView
@property (nonatomic, copy) NSArray<UIView *> *ignoreViews;
@property (nonatomic, weak) id<PlayerEventDelegate> eventDelegate;
@end

NS_ASSUME_NONNULL_END

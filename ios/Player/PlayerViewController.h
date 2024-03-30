//
//  PlayerViewController.h
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/21.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface PlayerViewController : UIViewController
@property (nonatomic, strong) UIView *contentView;
@property (nonatomic, strong) UIView *controlView;
@property (nonatomic, strong) UIView *eventsView;

- (void)layoutPlayerView;
@end

NS_ASSUME_NONNULL_END

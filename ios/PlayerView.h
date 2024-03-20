//
//  PlayerView.h
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/20.
//

#import <UIKit/UIKit.h>
#import <MobileVLCKit/MobileVLCKit.h>
#import <React/RCTViewManager.h>

NS_ASSUME_NONNULL_BEGIN

@interface PlayerView : UIView<VLCMediaPlayerDelegate>
@property (nonatomic, strong) VLCMediaPlayer *player;
@property (nonatomic, weak) id<VLCMediaPlayerDelegate> delegate;
@property (nonatomic, strong) UIView *contentView;
@property (nonatomic, strong) UIView *controlView;

@property (nonatomic, copy) RCTDirectEventBlock onPlayStateChange;
@end

NS_ASSUME_NONNULL_END

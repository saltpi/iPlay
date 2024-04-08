//
//  PlayerView.h
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/20.
//

#import <UIKit/UIKit.h>
#import <React/RCTViewManager.h>
#import <React/RCTInvalidating.h>
#import "PlayerEventView.h"
#import "PlayerControlView.h"
#import "PlayerContentView.h"
#import "VideoPlayer.h"

NS_ASSUME_NONNULL_BEGIN

@interface PlayerView : UIView<RCTInvalidating>
@property (nonatomic, strong) id<VideoPlayer> player;
@property (nonatomic, copy) NSString *title;
@property (nonatomic, assign) NSUInteger iconSize;
@property (nonatomic, strong) NSString *subtitleFontName;
@property (nonatomic, weak) id delegate;
@property (nonatomic, strong) PlayerContentView *contentView;
@property (nonatomic, strong) PlayerControlView *controlView;
@property (nonatomic, strong) PlayerEventView *eventsView;

@property (nonatomic, copy) RCTDirectEventBlock onPlayStateChange;

- (void)onFullscreenTap:(id)sender;
@end

NS_ASSUME_NONNULL_END

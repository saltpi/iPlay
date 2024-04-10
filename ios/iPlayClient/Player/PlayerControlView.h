//
//  PlayerControlView.h
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/30.
//

#import <UIKit/UIKit.h>
#import <MediaPlayer/MediaPlayer.h>
#import "VideoPlayer.h"
#import "PlayerNumberValueView.h"
#import "PlayerSlider.h"

NS_ASSUME_NONNULL_BEGIN

@interface PlayerControlView : UIView
@property (nonatomic, copy) NSString *title;
@property (nonatomic, assign) NSUInteger iconSize;
@property (nonatomic, weak) id delegate;
@property (nonatomic, strong) id<VideoPlayer> player;
@property (nonatomic, weak) UIView *parentView;
@property (nonatomic, strong) UIView *playButton;
@property (nonatomic, strong) UIView *audioButton;
@property (nonatomic, strong) UIView *captionButton;
@property (nonatomic, strong) UIView *fullscreenButton;
@property (nonatomic, strong) UIProgressView *progressBar;
@property (nonatomic, strong) NSProgress *progress;
@property (nonatomic, strong) PlayerSlider *sliderBar;
@property (nonatomic, strong) UILabel *durationLabel;
@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) UIActivityIndicatorView *indicator;
@property (nonatomic, strong) NSDateComponentsFormatter *timeFormatter;
@property (nonatomic, assign) BOOL isControlsVisible;
@property (nonatomic, assign) BOOL isFullscreen;
@property (nonatomic, assign) CGFloat volumeValue;
@property (nonatomic, assign) CGFloat brightnessValue;

- (void)hideControls;
- (void)showControls;
- (void)updateVolume:(CGFloat)volume;
- (void)updatePlayState:(BOOL)isPlaying;
@end

NS_ASSUME_NONNULL_END

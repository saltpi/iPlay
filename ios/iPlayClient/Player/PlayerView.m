//
//  PlayerView.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/20.
//

#import "PlayerEventView.h"
#import "PlayerView.h"
#import "PlayerViewController.h"
#import "UIView+FindViewController.h"
#import <AVFoundation/AVAudioSession.h>
#import "PlayerContentView.h"
#import "IPLUIModule.h"

typedef NS_ENUM(NSUInteger, PlayerGestureType) {
    PlayerGestureTypeNone,
    PlayerGestureTypeHideControl,
    PlayerGestureTypeSeek,
    PlayerGestureTypeVolume,
    PlayerGestureTypeBrightness,
};


@interface PlayerView () <PlayerEventDelegate, VideoPlayerDelegate>
@property (nonatomic, assign) BOOL isFullscreen;
@end

@implementation PlayerView

- (instancetype)init {
    self = [super init];

    if (self) {
        [self _setupUI];
        [self _layout];
        [self _bind];
    }

    return self;
}

#pragma mark - Layout
- (void)_setupUI {
    [self addSubview:self.contentView];
    [self addSubview:self.controlView];
    [self addSubview:self.eventsView];
}

- (void)_layout {
    @weakify(self);
    [self.contentView remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.edges.equalTo(self);
    }];

    [self.controlView remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.edges.equalTo(self);
    }];

    [self.eventsView remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.left.equalTo(self);
        make.right.equalTo(self);
        make.top.equalTo(self.top).with.offset(100);
        make.bottom.equalTo(self.bottom).with.offset(-100);
    }];
}

- (void)_bind {
    self.player.delegate = self;
    self.eventsView.eventDelegate = self;
    self.controlView.player = self.player;
    self.controlView.parentView = self;
}


#pragma mark - Volume and Brightness
- (void)playerGestureEvent:(UIGestureRecognizer *)sender location:(CGPoint)location {
    PlayerGestureType type = [self gestureType:sender];
    switch (type) {
        case PlayerGestureTypeVolume: {
            if (sender.state == UIGestureRecognizerStateChanged ||
                sender.state == UIGestureRecognizerStateEnded) {
                CGFloat delta = [self deltaYForGesture:(UIPanGestureRecognizer *)sender];
                [self adjustVolumeWithDelta:-delta];
            }
            
            if (sender.state == UIGestureRecognizerStateBegan) {
                [self.controlView showVolumeIndicator:YES];
            } else if (sender.state == UIGestureRecognizerStateEnded) {
                [self.controlView showVolumeIndicator:NO];
            }
            break;
        }
        case PlayerGestureTypeBrightness: {
            if (sender.state == UIGestureRecognizerStateChanged ||
                sender.state == UIGestureRecognizerStateEnded) {
                CGFloat delta = [self deltaYForGesture:(UIPanGestureRecognizer *)sender];
                [self adjustBrightnessWithDelta:-delta];
            }
            
            if (sender.state == UIGestureRecognizerStateBegan) {
                [self.controlView showBrightnessIndicator:YES];
            } else if (sender.state == UIGestureRecognizerStateEnded) {
                [self.controlView showBrightnessIndicator:NO];
            }
            break;
        }
        case PlayerGestureTypeHideControl: {
            if (self.controlView.isControlsVisible) {
                [self.controlView hideControls];
            } else {
                [self.controlView showControls];
            }
            break;
        }
        case PlayerGestureTypeSeek: {
            CGFloat delta = [self deltaXForGesture:(UIPanGestureRecognizer *)sender];
            if (sender.state == UIGestureRecognizerStateEnded) {
                UISwipeGestureRecognizerDirection direction = delta > 0 ?
                UISwipeGestureRecognizerDirectionRight : UISwipeGestureRecognizerDirectionLeft;
                [self adjustPorgressWithDirection:direction];
            }
        }
        default:
            break;
    }
}

- (CGFloat)deltaYForGesture:(UIPanGestureRecognizer *)gesture {
    CGPoint velocity = [gesture velocityInView:self.eventsView];
    CGFloat speed = UIDevice.currentDevice.userInterfaceIdiom == UIUserInterfaceIdiomPad ? 1.0 / 70000 : 1.0 / 20000;
    CGFloat delta = velocity.y * speed;
    return delta;
}

- (CGFloat)deltaXForGesture:(UIPanGestureRecognizer *)gesture {
    CGPoint velocity = [gesture velocityInView:self.eventsView];
    CGFloat speed = UIDevice.currentDevice.userInterfaceIdiom == UIUserInterfaceIdiomPad ? 1.0 / 70000 : 1.0 / 20000;
    CGFloat delta = velocity.x * speed;
    return delta;
}

- (PlayerGestureType)gestureType:(UIGestureRecognizer *)sender {
    if ([sender isKindOfClass:UIPanGestureRecognizer.class]) {
        UIPanGestureRecognizer *gesture = (UIPanGestureRecognizer *)sender;
        CGPoint position = [gesture locationInView:self.eventsView];
        CGPoint velocity = [gesture velocityInView:self.eventsView];
        CGFloat windowWidth = IPLUIModule.windowSize.width;
        BOOL isLeftSide = position.x < windowWidth / 3;
        BOOL isRightSide = position.x > windowWidth * 2 / 3;
        BOOL isVerticalDirection = ABS(velocity.y) > ABS(velocity.x);
        if (isVerticalDirection && isLeftSide) {
            return PlayerGestureTypeBrightness;
        } else if (isVerticalDirection && isRightSide) {
            return PlayerGestureTypeVolume;
        } else if (isVerticalDirection) {
            return PlayerGestureTypeNone;
        } else {
            return PlayerGestureTypeSeek;
        }
    } else if ([sender isKindOfClass:UITapGestureRecognizer.class]) {
        return PlayerGestureTypeHideControl;
    }
    return PlayerGestureTypeNone;
}

- (void)adjustPorgressWithDirection:(UISwipeGestureRecognizerDirection)direction {
    if (direction == UISwipeGestureRecognizerDirectionLeft) {
        [self.player jumpBackward:10];
    } else {
        [self.player jumpForward:10];
    }
}

- (void)adjustVolumeWithDelta:(CGFloat)delta {
    CGFloat oldValue = self.controlView.volumeValue;
    CGFloat newValue = oldValue + delta;
    newValue = MIN(MAX(newValue, 0.f), 1.f);
    self.controlView.volumeValue = newValue;
}

- (void)adjustBrightnessWithDelta:(CGFloat)delta {
    CGFloat oldValue = self.controlView.brightnessValue;
    CGFloat newValue = oldValue + delta;
    newValue = MIN(MAX(newValue, 0.f), 1.f);
    self.controlView.brightnessValue = newValue;
}

#pragma mark - VideoPlayerDelegate
- (void)onPlayEvent:(PlayEventType)event data:(NSDictionary *)data {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self onPlayEventImpl:event data:data];
    });
}

- (void)onPlayEventImpl:(PlayEventType)event data:(NSDictionary *)data {
    NSDictionary *payload = nil;
    switch (event) {
        case PlayEventTypeDuration: {
            [UIApplication sharedApplication].idleTimerDisabled = NO;
            break;
        }
        case PlayEventTypeOnProgress: {
            NSNumber *duration = @(self.player.duration);
            NSNumber *current = data[@"time"];
            
            [self.controlView.progress setTotalUnitCount:duration.intValue];
            [self.controlView.progress setCompletedUnitCount:current.intValue];
            
            if (self.controlView.sliderBar.state == UIControlStateNormal) {
                [self.controlView.sliderBar setMaximumValue:duration.intValue];
                [self.controlView.sliderBar setValue:current.intValue animated:YES];
            }
        
            NSString *currentTimeStr = [self.controlView.timeFormatter stringFromTimeInterval:current.unsignedIntValue];
            NSString *totalTimeStr = [self.controlView.timeFormatter stringFromTimeInterval:duration.unsignedIntValue];
            NSString *durationText = [NSString stringWithFormat:@"%@ / %@", currentTimeStr, totalTimeStr];
            self.controlView.durationLabel.text = durationText;
            payload = @{
                @"type": @(PlayEventTypeOnProgress),
                @"duration": duration ?: @0,
                @"position": current ?: @0
            };
            break;
        }
        case PlayEventTypeOnPause: {
            payload = @{
                @"type": @(PlayEventTypeOnPause),
            };
            break;
        }
        case PlayEventTypeOnPauseForCache: {
            BOOL isPlaying = ![data[@"state"] boolValue];
            if (isPlaying) {
                [self.controlView.indicator stopAnimating];
            } else {
                [self.controlView.indicator startAnimating];
            }
            break;
        }
        default:
            break;
    }
    
    BLOCK_INVOKE(self.onPlayStateChange, payload ?: @{
        @"type": @(event)
    });
}

- (void)onFullscreenTap:(id)sender {
    UIViewController *currentController = [self.superview firstAvailableUIViewController];
    [self.eventsView removeFromSuperview];
    [self.controlView removeFromSuperview];
    [self.contentView removeFromSuperview];
    if (self.isFullscreen) {
        [self _setupUI];
        [self _layout];
        [currentController dismissViewControllerAnimated:YES completion:^{
            self.isFullscreen = NO;
            self.controlView.isFullscreen = NO;
        }];
    } else {
        PlayerViewController *controller = [PlayerViewController new];
        controller.modalPresentationStyle = UIModalPresentationFullScreen;
        controller.contentView = self.contentView;
        controller.controlView = self.controlView;
        controller.eventsView = self.eventsView;
        [controller layoutPlayerView];
        [currentController presentViewController:controller animated:YES completion:^{
            self.isFullscreen = YES;
            self.controlView.isFullscreen = YES;
        }];
    }
    [self.player keepaspect];
}

- (void)removeFromSuperview {
    [self.player stop];
    [self.player quit];
    [UIApplication sharedApplication].idleTimerDisabled = NO;
    [super removeFromSuperview];
}

#pragma mark - Setter
- (void)setSubtitleFontName:(NSString *)subtitleFontName {
    _subtitleFontName = subtitleFontName;
    [self.player setSubtitleFont:subtitleFontName];
}

#pragma mark - Getter
- (id<VideoPlayer>)player {
    if (!_player) {
        _player = self.contentView.viewModel;
    }
    return _player;
}

- (PlayerContentView *)contentView {
    if (!_contentView) {
        PlayerContentView *view = [PlayerContentView new];
        view.backgroundColor = UIColor.blackColor;
        _contentView = view;
    }
    return _contentView;
}


- (UIView *)controlView {
    if (!_controlView) {
        _controlView = [PlayerControlView new];
    }
    return _controlView;
}

- (PlayerEventView *)eventsView {
    if (!_eventsView) {
        PlayerEventView *view = [PlayerEventView new];
        view.ignoreViews = @[
            self.controlView.playButton,
            self.controlView.fullscreenButton,
            self.controlView.captionButton,
            self.controlView.settingButton,
            self.controlView.sliderBar
        ];
        _eventsView = view;
    }
    return _eventsView;
}

@end

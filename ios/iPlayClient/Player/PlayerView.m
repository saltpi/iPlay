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
#import "PlayerSeekableImageView.h"
#import "IPLScreen.h"

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
        make.top.equalTo(self.top);
        make.bottom.equalTo(self.bottom);
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
                [self.eventsView showVolumeIndicator:YES];
            } else if (sender.state == UIGestureRecognizerStateEnded) {
                [self.eventsView showVolumeIndicator:NO];
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
                [self.eventsView showBrightnessIndicator:YES];
            } else if (sender.state == UIGestureRecognizerStateEnded) {
                [self.eventsView showBrightnessIndicator:NO];
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
                [self.eventsView showVolumeIndicator:NO];
                [self.eventsView showBrightnessIndicator:NO];
            }
        }
        case PlayerGestureTypeNone: {
            if ([self.eventsView isNumberValueViewPresent]) {
                [self.eventsView showVolumeIndicator:NO];
                [self.eventsView showBrightnessIndicator:NO];
            }
            break;
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
        CGFloat windowWidth = self.eventsView.frame.size.width;
        BOOL isLeftSide = position.x < windowWidth / 3;
        BOOL isRightSide = position.x > windowWidth * 2 / 3;
        BOOL isVerticalDirection = ABS(velocity.y) > ABS(velocity.x);
        if (isVerticalDirection && isLeftSide) {
            return PlayerGestureTypeBrightness;
        } else if (isVerticalDirection && isRightSide) {
            return PlayerGestureTypeVolume;
        } else if (!isLeftSide && !isRightSide && !isVerticalDirection) {
            return PlayerGestureTypeSeek;
        } else {
            return PlayerGestureTypeNone;
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
    self.eventsView.numberValueView.progress = newValue * 100;
}

- (void)adjustBrightnessWithDelta:(CGFloat)delta {
    CGFloat oldValue = self.controlView.brightnessValue;
    CGFloat newValue = oldValue + delta;
    newValue = MIN(MAX(newValue, 0.f), 1.f);
    self.controlView.brightnessValue = newValue;
    self.eventsView.numberValueView.progress = newValue * 100;
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
            [IPLScreen keepScreenOn];
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
        case PlayEventTypeEnd: {
            [self.controlView updatePlayState:[data[@"state"] isEqual:@(0)]];
            break;
        }
        case PlayEventTypeOnSeekableRanges: {
            NSArray<PlayerSeekableModel *> *ranges = self.player.seekableRanges;
            [self.controlView.sliderBar setSeekableRanges:ranges maxValue:self.player.duration];
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

- (void)onSelectAudioTap:(id)sender {
    [self.controlView hideControls];
    @weakify(self);
    self.eventsView.selectView.onSelectCallback = ^(PlayerMediaSelectItemModel<PlayerTrackModel *> * _Nullable model) {
        @strongify(self);
        [self.player useAudio:model.item.ID];
    };
    [self.eventsView showMediaSelectView:self.player.audios currentID:self.player.currentAudioID];
}

- (void)onSelectCaptionTap:(id)sender {
    [self.controlView hideControls];
    @weakify(self);
    self.eventsView.selectView.onSelectCallback = ^(PlayerMediaSelectItemModel<PlayerTrackModel *> * _Nullable model) {
        @strongify(self);
        [self.player useSubtitle:model.item.ID];
    };
    [self.eventsView showMediaSelectView:self.player.subtitles currentID:self.player.currentSubtitleID];
}

- (void)clean {
    [self.player stop];
    [self.player quit];
    [IPLScreen keepScreenOff];
}

- (void)invalidate {
    [self clean];
    [self removeFromSuperview];
}

- (void)dealloc {
    [self clean];
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
            self.controlView.audioButton,
            self.controlView.sliderBar
        ];
        _eventsView = view;
    }
    return _eventsView;
}

@end

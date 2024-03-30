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
- (void)playerGestureEvent:(UIGestureRecognizer *)gesture location:(CGPoint)location {
    if ([gesture isKindOfClass:UISwipeGestureRecognizer.class]) {
        UISwipeGestureRecognizer *swipe = (UISwipeGestureRecognizer *)gesture;
        UISwipeGestureRecognizerDirection direction = swipe.direction;
        BOOL isLeftSide = location.x < self.bounds.size.width / 2;
        if (isLeftSide) {
              NSLog(@"Left side swipe up detected");
        } else {
              NSLog(@"Right side swipe up detected");
        }
        if (direction == UISwipeGestureRecognizerDirectionLeft ||
            direction == UISwipeGestureRecognizerDirectionRight) {
            [self adjustPorgressWithDirection:direction];
        } else if (direction == UISwipeGestureRecognizerDirectionUp) {
            if (isLeftSide) {
              [self adjustBrightnessWithDirection:direction];
            } else {
              [self adjustVolumeWithDirection:direction];
            }
        } else if (direction == UISwipeGestureRecognizerDirectionDown) {
            if (isLeftSide) {
              [self adjustBrightnessWithDirection:direction];
            } else {
              [self adjustVolumeWithDirection:direction];
            }
        }
    } else if ([gesture isKindOfClass:UITapGestureRecognizer.class]) {
        // TODO
    }
    
    if (self.controlView.isControlsVisible) {
        [self.controlView hideControls];
    } else {
        [self.controlView showControls];
    }
}

- (void)adjustPorgressWithDirection:(UISwipeGestureRecognizerDirection)direction {
    if (direction == UISwipeGestureRecognizerDirectionLeft) {
        [self.player jumpBackward:10];
    } else {
        [self.player jumpForward:10];
    }
}

- (void)adjustVolumeWithDirection:(UISwipeGestureRecognizerDirection)direction {
    if (direction == UISwipeGestureRecognizerDirectionUp) {
        [self.player volumeUp:0.05];
    } else {
        [self.player volumeDown:0.05];
    }
}

- (void)adjustBrightnessWithDirection:(UISwipeGestureRecognizerDirection)direction {
    CGFloat delta = 0.05;
    if (direction == UISwipeGestureRecognizerDirectionUp) {
        [UIScreen mainScreen].brightness += delta;
    } else {
        [UIScreen mainScreen].brightness -= delta;
    }
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
    self.onPlayStateChange(payload ?: @{
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
        [currentController dismissViewControllerAnimated:YES completion:^{
            [self _layout];
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
    [self.player destroy];
    [super removeFromSuperview];
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
        view.backgroundColor = UIColor.clearColor;
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

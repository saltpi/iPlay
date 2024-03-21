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

static NSUInteger const kIconSize = 28;

@interface PlayerView () <PlayerEventDelegate>
@property (nonatomic, strong) UIImageView *playButton;
@property (nonatomic, strong) UIImageView *fullscreenButton;
@property (nonatomic, strong) UIImageView *gobackButton;
@property (nonatomic, strong) UIImageView *settingButton;
@property (nonatomic, strong) UIProgressView *progressBar;
@property (nonatomic, strong) NSProgress *progress;
@property (nonatomic, strong) UISlider *sliderBar;
@property (nonatomic, strong) UILabel *watchedLabel;
@property (nonatomic, strong) UILabel *remainingLabel;
@property (nonatomic, assign) CGRect initialBounds;
@property (nonatomic, weak) NSTimer *timer;
@property (nonatomic, assign) BOOL isControlsVisible;
@property (nonatomic, assign) BOOL isFullscreen;
@property (nonatomic, strong) UIActivityIndicatorView *indicator;
@end

@implementation PlayerView

- (instancetype)init {
    self = [super init];

    if (self) {
        self.isControlsVisible = YES;
        [self setupUI];
        [self layout];
        [self bind];
    }

    return self;
}

#pragma mark - Layout
- (void)setupUI {
    self.initialBounds = CGRectZero;
    [self addSubview:self.contentView];
    [self addSubview:self.controlView];
    [self.controlView addSubview:self.playButton];
    [self.controlView addSubview:self.fullscreenButton];
    [self.controlView addSubview:self.gobackButton];
    [self.controlView addSubview:self.settingButton];
    [self.controlView addSubview:self.progressBar];
    [self.controlView addSubview:self.sliderBar];
    [self.controlView addSubview:self.watchedLabel];
    [self.controlView addSubview:self.remainingLabel];
    [self addSubview:self.eventsView];
    [self addSubview:self.indicator];
}

- (void)layout {
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
        make.edges.equalTo(self);
    }];

    UIView *superview = self.controlView;
    @weakify(superview);
    [self.progressBar remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(superview);
        make.bottom.equalTo(superview).offset(-50);
        make.height.equalTo(4);
        make.centerX.equalTo(superview);
        make.width.equalTo(superview).with.multipliedBy(0.75);
    }];

    [self.sliderBar remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(superview);
        make.bottom.equalTo(superview).offset(-50);
        make.height.equalTo(4);
        make.centerX.equalTo(superview);
        make.width.equalTo(superview).with.multipliedBy(0.75);
    }];

    [self.watchedLabel remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.centerY.equalTo(self.sliderBar);
        make.right.equalTo(self.sliderBar.left).with.offset(-5);
    }];

    [self.remainingLabel remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.centerY.equalTo(self.sliderBar);
        make.left.equalTo(self.sliderBar.right).with.offset(5);
    }];

    [self.playButton remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        @strongify(superview);
        make.centerY.equalTo(self.sliderBar);
        make.size.equalTo(@(kIconSize));
        make.left.equalTo(superview).with.offset(10);
        make.right.lessThanOrEqualTo(self.watchedLabel.left);
    }];

    [self.gobackButton remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        @strongify(superview);
        make.centerX.equalTo(self.playButton);
        make.top.equalTo(superview).with.offset(10);
        make.size.equalTo(@(kIconSize));
    }];

    [self.settingButton remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        @strongify(superview);
        make.centerX.equalTo(self.fullscreenButton);
        make.centerY.equalTo(self.gobackButton);
        make.size.equalTo(@(kIconSize));
    }];

    [self.fullscreenButton remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        @strongify(superview);
        make.centerY.equalTo(self.sliderBar);
        make.size.equalTo(@(kIconSize));
        make.left.greaterThanOrEqualTo(self.remainingLabel.right);
        make.right.equalTo(superview).with.offset(-10);
    }];

    [self.indicator remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.center.equalTo(self);
    }];
}

- (void)bind {
    self.player.drawable = self.contentView;
    self.player.delegate = self;
    self.eventsView.eventDelegate = self;
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(onPlayTap:)];
    self.playButton.userInteractionEnabled = YES;
    [self.playButton addGestureRecognizer:tap];

    UITapGestureRecognizer *fullscreenTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(onFullscreenTap:)];
    self.fullscreenButton.userInteractionEnabled = YES;
    [self.fullscreenButton addGestureRecognizer:fullscreenTap];
  
    UITapGestureRecognizer *gobackTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(onFullscreenTap:)];
    self.gobackButton.userInteractionEnabled = YES;
    [self.gobackButton addGestureRecognizer:gobackTap];

    self.progressBar.observedProgress = self.progress;
    [self.sliderBar addTarget:self action:@selector(_seekToPlay:) forControlEvents:UIControlEventValueChanged];

    if (self.isControlsVisible) {
        self.timer = [NSTimer scheduledTimerWithTimeInterval:5.0 target:self selector:@selector(hideControls) userInfo:nil repeats:NO];
    }
  
    @weakify(self);
    [RACObserve(self, isFullscreen) subscribeNext:^(id  _Nullable x) {
        @strongify(self);
        self.fullscreenButton.tintColor = self.isFullscreen ? UIColor.grayColor : UIColor.blueColor;
        self.fullscreenButton.userInteractionEnabled = !self.isFullscreen;
        self.gobackButton.userInteractionEnabled = self.isFullscreen;
        self.gobackButton.tintColor = !self.isFullscreen ? UIColor.grayColor : UIColor.blueColor;
    }];
}

- (void)showControls {
    [UIView animateWithDuration:0.5
                     animations:^{
        self.controlView.alpha = 1.0;
    }];
    self.isControlsVisible = YES;
    [self.timer invalidate];
    self.timer = [NSTimer scheduledTimerWithTimeInterval:5.0 target:self selector:@selector(hideControls) userInfo:nil repeats:NO];
}

- (void)hideControls {
    [UIView animateWithDuration:0.5
                     animations:^{
        self.controlView.alpha = 0.0;
    }];
    self.isControlsVisible = NO;
}

- (void)onPlayTap:(id)sender {
    [self _changePlayButtonIcon];

    if (self.player.isPlaying) {
        [self.player pause];
    } else {
        [self.player play];
    }
}

- (void)onFullscreenTap:(id)sender {
    UIViewController *currentController = [self.superview firstAvailableUIViewController];
    if (self.isFullscreen) {
        [self removeFromSuperview];
        [self.parentView addSubview:self];
        [self remakeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self.superview);
        }];
        [currentController dismissViewControllerAnimated:YES completion:^{
            self.isFullscreen = NO;
        }];
    } else {
        self.parentView = self.superview;
        PlayerViewController *controller = [PlayerViewController new];
        controller.modalPresentationStyle = UIModalPresentationFullScreen;
        [self removeFromSuperview];
        [controller.view addSubview:self];
        [self remakeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self.superview);
        }];
        [currentController presentViewController:controller animated:YES completion:^{
            self.isFullscreen = YES;
        }];
    }
}

- (void)_seekToPlay:(id)sender {
    if (![self.player isSeekable]) {
        return;
    }

    if ([sender isKindOfClass:UISlider.class]) {
        UISlider *slider = sender;
        CGFloat time = slider.value;

        if (slider.continuous) {
            return;
        }

        CGFloat position = time / slider.maximumValue;
        self.player.position = position;
    }
}

- (void)_changePlayButtonIcon {
    NSString *imageName = self.player.isPlaying ? @"play" : @"pause";

    self.playButton.image = [UIImage systemImageNamed:imageName];
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
        [self showControls];
    }
}

- (void)adjustPorgressWithDirection:(UISwipeGestureRecognizerDirection)direction {
    
}

- (void)adjustVolumeWithDirection:(UISwipeGestureRecognizerDirection)direction {
    float volume = [[AVAudioSession sharedInstance] outputVolume];
    if (direction == UISwipeGestureRecognizerDirectionUp) {
      [self.player.audio volumeUp];
    } else {
      [self.player.audio volumeDown];
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

#pragma mark - VLCMediaPlayerDelegate
- (void)mediaPlayerStateChanged:(NSNotification *)aNotification {
    NSLog(@"%ld %d", (long)self.player.state, self.player.isPlaying);
    switch (self.player.state) {
        case VLCMediaPlayerStateStopped: {
            NSLog(@"stopped");
            break;
        }    
        case VLCMediaPlayerStateOpening: {
            NSLog(@"opening");
            [self.indicator startAnimating];
            break;
        }    
        case VLCMediaPlayerStateBuffering: {
            NSLog(@"buffering");
            break;
        }  
        case VLCMediaPlayerStateEnded: {
            NSLog(@"ended");
            break;
        }    
        case VLCMediaPlayerStateError: {
            NSLog(@"error");
            break;
        }    
        case VLCMediaPlayerStatePlaying: {
            NSLog(@"playing");
            [self.indicator stopAnimating];
            break;
        }    
        case VLCMediaPlayerStatePaused: {
            NSLog(@"paused");
            break;
        }
        case VLCMediaPlayerStateESAdded: {
            NSLog(@"esadded");
            break;
        }
    }

    if (!self.onPlayStateChange) {
        return;
    }

    self.onPlayStateChange(@{
        @"state": @(self.player.state)
    });
}

- (void)mediaPlayerTimeChanged:(NSNotification *)aNotification {
    NSUInteger duration = self.player.media.length.intValue;
    NSUInteger remaining = self.player.remainingTime.intValue;
    NSUInteger current = self.player.time.value.intValue;

    [self.progress setTotalUnitCount:duration / 1000];
    [self.progress setCompletedUnitCount:current / 1000];

    if (self.sliderBar.state == UIControlStateNormal) {
        [self.sliderBar setMaximumValue:duration / 1000];
        [self.sliderBar setValue:current / 1000 animated:YES];
    }

    self.watchedLabel.text = self.player.time.stringValue;
    self.remainingLabel.text = self.player.remainingTime.stringValue;
}

#pragma mark - Getter
- (VLCMediaPlayer *)player {
    BeginLazyPropInit(player)
    player = [[VLCMediaPlayer alloc] init];
    EndLazyPropInit(player)
}

- (UIView *)contentView {
    BeginLazyPropInit(contentView)
    UIView *view = [UIView new];
    view.backgroundColor = UIColor.whiteColor;
    contentView = view;
    EndLazyPropInit(contentView)
}

- (UIImageView *)playButton {
    BeginLazyPropInit(playButton)
    UIImage *icon = [UIImage systemImageNamed:@"pause"];
    icon = [icon imageWithTintColor:UIColor.blueColor];
    UIImageView *imageView = [[UIImageView alloc] initWithImage:icon];
    imageView.contentMode = UIViewContentModeScaleAspectFit;
    playButton = imageView;
    EndLazyPropInit(playButton)
}

- (UIImageView *)fullscreenButton {
    BeginLazyPropInit(fullscreenButton)
    UIImage *icon = [UIImage systemImageNamed:@"viewfinder.rectangular"];
    icon = [icon imageWithTintColor:UIColor.blueColor];
    UIImageView *imageView = [[UIImageView alloc] initWithImage:icon];
    imageView.contentMode = UIViewContentModeScaleAspectFit;
    fullscreenButton = imageView;
    EndLazyPropInit(fullscreenButton)
}

- (UIImageView *)gobackButton {
    BeginLazyPropInit(gobackButton)
    UIImage *icon = [UIImage systemImageNamed:@"chevron.backward"];
    icon = [icon imageWithTintColor:UIColor.blueColor];
    UIImageView *imageView = [[UIImageView alloc] initWithImage:icon];
    imageView.contentMode = UIViewContentModeScaleAspectFit;
    gobackButton = imageView;
    EndLazyPropInit(gobackButton)
}

- (UIImageView *)settingButton {
    BeginLazyPropInit(settingButton)
    UIImage *icon = [UIImage systemImageNamed:@"gear"];
    icon = [icon imageWithTintColor:UIColor.grayColor];
    UIImageView *imageView = [[UIImageView alloc] initWithImage:icon];
    imageView.contentMode = UIViewContentModeScaleAspectFit;
    settingButton = imageView;
    EndLazyPropInit(settingButton)
}

- (UIProgressView *)progressBar {
    BeginLazyPropInit(progressBar)
    progressBar = [[UIProgressView alloc] init];
    [progressBar setProgressViewStyle:UIProgressViewStyleBar];
    progressBar.progressTintColor = UIColor.whiteColor;
    progressBar.trackTintColor = [UIColor.whiteColor colorWithAlphaComponent:0.3];
    progressBar.layer.cornerRadius = 3;
    progressBar.clipsToBounds = YES;
    progressBar.hidden = YES;
    EndLazyPropInit(progressBar)
}

- (NSProgress *)progress {
    BeginLazyPropInit(progress)
    progress = [[NSProgress alloc] init];
    EndLazyPropInit(progress)
}

- (UISlider *)sliderBar {
    BeginLazyPropInit(sliderBar)
    sliderBar = [[UISlider alloc] init];
    sliderBar.continuous = NO;
    EndLazyPropInit(sliderBar)
}

- (UILabel *)watchedLabel {
    BeginLazyPropInit(watchedLabel)
    watchedLabel = [UILabel new];
    watchedLabel.font = [UIFont systemFontOfSize:13];
    watchedLabel.text = @"0:00";
    watchedLabel.textColor = UIColor.blueColor;
    EndLazyPropInit(watchedLabel)
}

- (UILabel *)remainingLabel {
    BeginLazyPropInit(remainingLabel)
    remainingLabel = [UILabel new];
    remainingLabel.font = [UIFont systemFontOfSize:13];
    remainingLabel.text = @"0:00";
    remainingLabel.textColor = UIColor.blueColor;
    EndLazyPropInit(remainingLabel)
}

- (UIView *)controlView {
    BeginLazyPropInit(controlView)
    controlView = [UIView new];
    EndLazyPropInit(controlView)
}

- (PlayerEventView *)eventsView {
    BeginLazyPropInit(eventsView)
    PlayerEventView *view = [PlayerEventView new];
    view.ignoreViews = @[
        self.playButton,
        self.fullscreenButton,
        self.gobackButton,
        self.settingButton,
        self.sliderBar
    ];
    eventsView = view;
    EndLazyPropInit(eventsView)
}

- (UIActivityIndicatorView *)indicator {
    BeginLazyPropInit(indicator)
    indicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleLarge];
    EndLazyPropInit(indicator)
}

@end

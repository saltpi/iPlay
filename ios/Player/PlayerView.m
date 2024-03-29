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
#import "MPVView.h"

static NSUInteger const kIconSize = 48;

@interface PlayerView () <PlayerEventDelegate, VideoPlayerDelegate>
@property (nonatomic, strong) UIView *playButton;
@property (nonatomic, strong) UIView *fullscreenButton;
@property (nonatomic, strong) UIView *captionButton;
@property (nonatomic, strong) UIView *settingButton;
@property (nonatomic, strong) UIProgressView *progressBar;
@property (nonatomic, strong) NSProgress *progress;
@property (nonatomic, strong) UISlider *sliderBar;
@property (nonatomic, strong) UILabel *durationLabel;
@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, assign) CGRect initialBounds;
@property (nonatomic, assign) BOOL isControlsVisible;
@property (nonatomic, assign) BOOL isFullscreen;
@property (nonatomic, strong) UIActivityIndicatorView *indicator;
@property (nonatomic, strong) NSDateComponentsFormatter *timeFormatter;
@end

@implementation PlayerView

- (instancetype)init {
    self = [super init];

    if (self) {
        _isControlsVisible = YES;
        _iconSize = kIconSize;
        [self _setupUI];
        [self _layout];
        [self _bind];
    }

    return self;
}

#pragma mark - Layout
- (void)_setupUI {
    self.initialBounds = CGRectZero;
    [self addSubview:self.contentView];
    [self addSubview:self.controlView];
    [self.controlView addSubview:self.playButton];
    [self.controlView addSubview:self.fullscreenButton];
    [self.controlView addSubview:self.captionButton];
    [self.controlView addSubview:self.settingButton];
    [self.controlView addSubview:self.progressBar];
    [self.controlView addSubview:self.sliderBar];
    [self.controlView addSubview:self.durationLabel];
    [self.controlView addSubview:self.titleLabel];
    [self addSubview:self.eventsView];
    [self addSubview:self.indicator];
}

- (void)_layout {
    UIEdgeInsets insets = [self safeAreaInsets];
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
        make.top.equalTo(self.settingButton.bottom).with.offset(5);
        make.bottom.equalTo(self.titleLabel.top).with.offset(-5);
    }];

    UIView *superview = self.controlView;
    @weakify(superview);
    [self.progressBar remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(superview);
        make.bottom.equalTo(superview).multipliedBy(0.85);
        make.centerX.equalTo(superview);
        make.width.equalTo(superview).with.multipliedBy(0.90);
    }];

    [self.sliderBar remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.width.equalTo(self.progressBar);
        make.center.equalTo(self.progressBar);
    }];
  
    [self.durationLabel remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.bottom.equalTo(self.sliderBar.top).with.offset(-12);
        make.right.equalTo(self.sliderBar.right).with.offset(-2);
    }];
    
    [self.titleLabel remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.left.equalTo(self.progressBar);
        make.bottom.equalTo(self.durationLabel);
    }];

    [self.playButton remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(superview);
        make.center.equalTo(superview);
        make.size.equalTo(@(self.iconSize));
    }];

    [self.settingButton remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        @strongify(superview);
        make.size.equalTo(@(self.iconSize));
        make.top.equalTo(superview).with.offset(insets.top ?: 24);
        make.right.equalTo(self.progressBar);
    }];
    
    [self.fullscreenButton remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.size.equalTo(@(self.iconSize));
        make.centerY.equalTo(self.settingButton);
        make.right.equalTo(self.settingButton.left).with.offset(-10);
    }];
  
    [self.captionButton remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.size.equalTo(@(self.iconSize));
        make.centerY.equalTo(self.fullscreenButton);
        make.right.equalTo(self.fullscreenButton.left).with.offset(-10);
    }];

    [self.indicator remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.center.equalTo(self);
    }];
}

- (void)_bind {
    self.player.delegate = self;
    self.eventsView.eventDelegate = self;
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(onPlayTap:)];
    self.playButton.userInteractionEnabled = YES;
    [self.playButton addGestureRecognizer:tap];

    UITapGestureRecognizer *fullscreenTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(onFullscreenTap:)];
    self.fullscreenButton.userInteractionEnabled = YES;
    [self.fullscreenButton addGestureRecognizer:fullscreenTap];
  
//    UITapGestureRecognizer *gobackTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(onFullscreenTap:)];
//    self.captionButton.userInteractionEnabled = YES;
//    [self.captionButton addGestureRecognizer:gobackTap];

    self.progressBar.observedProgress = self.progress;
    [self.sliderBar addTarget:self action:@selector(_seekToPlay:) forControlEvents:UIControlEventValueChanged];
  
    @weakify(self);
    [RACObserve(self, isFullscreen) subscribeNext:^(id  _Nullable x) {
        @strongify(self);
        NSString *iconName = self.isFullscreen ? @"arrow.up.right.and.arrow.down.left" : @"viewfinder";
        [self _updateIcon:self.fullscreenButton icon:iconName];
    }];
    
    [RACObserve(self, title) subscribeNext:^(id  _Nullable x) {
        @strongify(self);
        self.titleLabel.text = self.title;
    }];
}

- (void)showControls {
    [UIView animateWithDuration:0.5
                     animations:^{
        self.controlView.alpha = 1.0;
    } completion:^(BOOL finished) {
        self.controlView.hidden = NO;
    }];
    self.isControlsVisible = YES;
}

- (void)hideControls {
    [UIView animateWithDuration:0.5
                     animations:^{
        self.controlView.alpha = 0.0;
    } completion:^(BOOL finished) {
        self.controlView.hidden = YES;
    }];
    self.isControlsVisible = NO;
}

- (void)onPlayTap:(id)sender {
    [self _changePlayButtonIcon:self.player.isPlaying];
    if (self.player.isPlaying) {
        [self.player pause];
    } else {
        [self.player resume];
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
            [self _layout];
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
            [self _layout];
            self.isFullscreen = YES;
        }];
    }
    [self.player keepaspect];
}

- (void)_seekToPlay:(id)sender {
    if ([sender isKindOfClass:UISlider.class]) {
        UISlider *slider = sender;
        CGFloat time = slider.value;

        [self.player seek:time];
    }
}

- (void)_changePlayButtonIcon:(BOOL)isPlaying {
    NSString *imageName = isPlaying ? @"play" : @"pause";
    [self _updateIcon:self.playButton icon:imageName];
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
    
    if (self.isControlsVisible) {
        [self hideControls];
    } else {
        [self showControls];
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
            
            [self.progress setTotalUnitCount:duration.intValue];
            [self.progress setCompletedUnitCount:current.intValue];
            
            if (self.sliderBar.state == UIControlStateNormal) {
                [self.sliderBar setMaximumValue:duration.intValue];
                [self.sliderBar setValue:current.intValue animated:YES];
            }
        
            NSString *currentTimeStr = [self.timeFormatter stringFromTimeInterval:current.unsignedIntValue];
            NSString *totalTimeStr = [self.timeFormatter stringFromTimeInterval:duration.unsignedIntValue];
            NSString *durationText = [NSString stringWithFormat:@"%@ / %@", currentTimeStr, totalTimeStr];
            self.durationLabel.text = durationText;
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
                [self.indicator stopAnimating];
            } else {
                [self.indicator startAnimating];
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

#pragma mark - Getter
- (id<VideoPlayer>)player {
    BeginLazyPropInit(player)
    if ([self.contentView isKindOfClass:MPVView.class]) {
        MPVView *view = (MPVView *)self.contentView;
        return view.viewModel;
    }
    EndLazyPropInit(player)
}

- (UIView *)contentView {
    BeginLazyPropInit(contentView)
    UIView *view = [MPVView new];
    view.backgroundColor = UIColor.clearColor;
    contentView = view;
    EndLazyPropInit(contentView)
}

- (UIView *)playButton {
    BeginLazyPropInit(playButton)
    UIView *view = [self _makeControlView:@"pause"];
    playButton = view;
    EndLazyPropInit(playButton)
}

- (UIView *)fullscreenButton {
    BeginLazyPropInit(fullscreenButton)
    UIView *view = [self _makeControlView:@"viewfinder"];
    fullscreenButton = view;
    EndLazyPropInit(fullscreenButton)
}

- (UIView *)captionButton {
    BeginLazyPropInit(captionButton)
    UIView *view = [self _makeControlView:@"captions.bubble"];
    captionButton = view;
    EndLazyPropInit(captionButton)
}

- (UIView *)settingButton {
    BeginLazyPropInit(settingButton)
    settingButton = [self _makeControlView:@"gear"];
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
    sliderBar.tintColor = UIColor.whiteColor;
    sliderBar.continuous = NO;
    EndLazyPropInit(sliderBar)
}


- (UILabel *)durationLabel {
    BeginLazyPropInit(durationLabel)
    durationLabel = [UILabel new];
    durationLabel.font = [UIFont systemFontOfSize:13];
    durationLabel.text = @"0:00";
    durationLabel.textColor = UIColor.whiteColor;
    EndLazyPropInit(durationLabel)
}

- (UILabel *)titleLabel {
    BeginLazyPropInit(titleLabel)
    titleLabel = [UILabel new];
    titleLabel.font = [UIFont systemFontOfSize:16];
    titleLabel.textColor = UIColor.whiteColor;
    titleLabel.text = nil;
    EndLazyPropInit(titleLabel)
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
        self.captionButton,
        self.settingButton,
        self.sliderBar
    ];
    eventsView = view;
    EndLazyPropInit(eventsView)
}

- (UIActivityIndicatorView *)indicator {
    BeginLazyPropInit(indicator)
    indicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleMedium];
    EndLazyPropInit(indicator)
}

- (UIView *)_makeControlView:(NSString *)iconName {
    UIImage *icon = nil;
    if (@available(iOS 15.0, *)) {
        UIImageSymbolConfiguration *config = [UIImageSymbolConfiguration configurationWithHierarchicalColor:UIColor.whiteColor];
        icon = [UIImage systemImageNamed:iconName withConfiguration:config];
    } else {
        icon = [[UIImage systemImageNamed:iconName] imageWithTintColor:UIColor.whiteColor renderingMode:UIImageRenderingModeAutomatic];
    }
    icon = [icon imageWithTintColor:UIColor.whiteColor];
    UIImageView *imageView = [[UIImageView alloc] initWithImage:icon];
    imageView.contentMode = UIViewContentModeScaleAspectFit;

    UIView *view = [UIView new];
    view.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.25];
    view.layer.borderWidth = 0.5;
    view.layer.borderColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.2].CGColor;
    view.layer.cornerRadius = self.iconSize / 2;
    view.clipsToBounds = YES;
    view.layer.masksToBounds = YES;
    view.userInteractionEnabled = YES;
    [view addSubview:imageView];
    @weakify(view);
    [imageView remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(view);
        make.center.equalTo(view);
        make.size.equalTo(view).multipliedBy(0.5);
    }];
    return view;
}

- (void)_updateIcon:(UIView *)view icon:(NSString *)iconName {
    UIImageView *imageView = ( UIImageView *)view.subviews.firstObject;
    if (![imageView isKindOfClass:UIImageView.class]) {
        return;
    }
    UIImage *icon = nil;
    if (@available(iOS 15.0, *)) {
        UIImageSymbolConfiguration *config = [UIImageSymbolConfiguration configurationWithHierarchicalColor:UIColor.whiteColor];
        icon = [UIImage systemImageNamed:iconName withConfiguration:config];
    } else {
        icon = [[UIImage systemImageNamed:iconName] imageWithTintColor:UIColor.whiteColor renderingMode:UIImageRenderingModeAutomatic];
    }
    imageView.image = icon;
}

- (NSDateComponentsFormatter *)timeFormatter {
    if (!_timeFormatter) {
        NSDateComponentsFormatter *dateComponentsFormatter = [[NSDateComponentsFormatter alloc] init];
        dateComponentsFormatter.allowedUnits = NSCalendarUnitHour | NSCalendarUnitMinute | NSCalendarUnitSecond;
        dateComponentsFormatter.zeroFormattingBehavior = NSDateComponentsFormatterZeroFormattingBehaviorPad;
        _timeFormatter = dateComponentsFormatter;
    }
    return _timeFormatter;
}

#pragma mark - NSObject
- (void)dealloc {
    [self.player stop];
    [self.player destroy];
}

#pragma mark - Setter

- (void)setIconSize:(NSUInteger)iconSize {
    _iconSize = iconSize;
    NSArray<UIView *> *views = @[
        self.playButton,
        self.fullscreenButton,
        self.captionButton,
        self.settingButton,
    ];
    for (UIView *view in views) {
        view.layer.cornerRadius = iconSize / 2;
    }
    [self _layout];
}
@end

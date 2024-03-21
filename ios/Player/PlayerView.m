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

static NSUInteger const kIconSize = 48;

@interface PlayerView () <PlayerEventDelegate>
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
@property (nonatomic, weak) NSTimer *timer;
@property (nonatomic, assign) BOOL isControlsVisible;
@property (nonatomic, assign) BOOL isFullscreen;
@property (nonatomic, strong) UIActivityIndicatorView *indicator;
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
        make.height.equalTo(4);
        make.centerX.equalTo(superview);
        make.width.equalTo(superview).with.multipliedBy(0.90);
    }];

    [self.sliderBar remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.width.equalTo(self.progressBar);
        make.height.equalTo(4);
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
    self.player.drawable = self.contentView;
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

    if (self.isControlsVisible) {
        self.timer = [NSTimer scheduledTimerWithTimeInterval:5.0 target:self selector:@selector(hideControls) userInfo:nil repeats:NO];
    }
  
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
    [self showControls];
}

- (void)adjustPorgressWithDirection:(UISwipeGestureRecognizerDirection)direction {
    if (direction == UISwipeGestureRecognizerDirectionLeft) {
        [self.player shortJumpBackward];
    } else {
        [self.player shortJumpForward];
    }
}

- (void)adjustVolumeWithDirection:(UISwipeGestureRecognizerDirection)direction {
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
    switch (self.player.state) {
        case VLCMediaPlayerStateStopped: {
            break;
        }    
        case VLCMediaPlayerStateOpening: {
            [self.indicator startAnimating];
            break;
        }    
        case VLCMediaPlayerStateBuffering: {
            break;
        }  
        case VLCMediaPlayerStateEnded: {
            break;
        }    
        case VLCMediaPlayerStateError: {
            break;
        }    
        case VLCMediaPlayerStatePlaying: {
            [self.indicator stopAnimating];
            break;
        }    
        case VLCMediaPlayerStatePaused: {
            break;
        }
        case VLCMediaPlayerStateESAdded: {
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
    NSUInteger current = self.player.time.value.intValue;

    [self.progress setTotalUnitCount:duration / 1000];
    [self.progress setCompletedUnitCount:current / 1000];

    if (self.sliderBar.state == UIControlStateNormal) {
        [self.sliderBar setMaximumValue:duration / 1000];
        [self.sliderBar setValue:current / 1000 animated:YES];
    }

    NSString *durationText = [NSString stringWithFormat:@"%@ / %@",
                              self.player.time.stringValue,
                              self.player.media.length.stringValue];
    self.durationLabel.text = durationText;
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

#pragma mark - NSObject
- (void)dealloc {
    [self.player stop];
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

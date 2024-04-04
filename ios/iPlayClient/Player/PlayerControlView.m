//
//  PlayerControlView.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/30.
//

#import "PlayerControlView.h"
#import "UIView+FindViewController.h"

static NSUInteger const kIconSize = 48;

@interface PlayerControlView ()
@property (nonatomic, strong) MPVolumeView *volumeView;
@end

@implementation PlayerControlView

- (instancetype)init {
    self = [super init];
    if (self) {
        _isControlsVisible = YES;
        _iconSize = kIconSize;
        _volumeValue = AVAudioSession.sharedInstance.outputVolume;
        _brightnessValue = UIScreen.mainScreen.brightness;
        [self _setupUI];
        [self _layout];
        [self _bind];
    }
    return self;
}

- (void)_setupUI {
    [self addSubview:self.playButton];
    [self addSubview:self.fullscreenButton];
    [self addSubview:self.captionButton];
    [self addSubview:self.settingButton];
    [self addSubview:self.progressBar];
    [self addSubview:self.sliderBar];
    [self addSubview:self.durationLabel];
    [self addSubview:self.titleLabel];
    [self addSubview:self.indicator];
    [self addSubview:self.numberValueView];
    [self addSubview:self.volumeView];
}

- (void)_layout {
    UIEdgeInsets insets = [self safeAreaInsets];
    @weakify(self);
    [self.progressBar remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.bottom.equalTo(self).multipliedBy(0.85);
        make.centerX.equalTo(self);
        make.width.equalTo(self).with.multipliedBy(0.90);
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
        @strongify(self);
        make.center.equalTo(self);
        make.size.equalTo(@(self.iconSize));
    }];

    [self.settingButton remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.size.equalTo(@(self.iconSize));
        make.top.equalTo(self).with.offset(insets.top ?: 24);
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
    
    [self.numberValueView remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.centerX.equalTo(self);
        make.top.equalTo(self.fullscreenButton.bottom).with.offset(10);
        make.width.greaterThanOrEqualTo(200);
    }];
    
    [self.volumeView remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.bottom.equalTo(self.top).with.offset(-40);
        make.centerX.equalTo(self);
        make.height.equalTo(@0);
        make.width.equalTo(@0);
    }];
}

- (void)_bind {
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(onPlayTap:)];
    self.playButton.userInteractionEnabled = YES;
    [self.playButton addGestureRecognizer:tap];

    UITapGestureRecognizer *fullscreenTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(onFullscreenTap:)];
    self.fullscreenButton.userInteractionEnabled = YES;
    [self.fullscreenButton addGestureRecognizer:fullscreenTap];
  

    self.progressBar.observedProgress = self.progress;
    [self.sliderBar addTarget:self action:@selector(_seekToPlay:) forControlEvents:UIControlEventValueChanged];
  
    @weakify(self);
    [RACObserve(self, isFullscreen) subscribeNext:^(id  _Nullable x) {
        @strongify(self);
        NSString *iconName = self.isFullscreen ? @"player/fullscreen_exit" : @"player/fullscreen";
        [self _updateIcon:self.fullscreenButton icon:iconName];
    }];
    
    [RACObserve(self, title) subscribeNext:^(id  _Nullable x) {
        @strongify(self);
        self.titleLabel.text = self.title;
    }];
    
    [RACObserve(self, brightnessValue) subscribeNext:^(id  _Nullable x) {
        @strongify(self);
        UIScreen.mainScreen.brightness = self.brightnessValue;
        self.numberValueView.progress = self.brightnessValue * 100;
    }];
    
    [RACObserve(self, volumeValue) subscribeNext:^(id  _Nullable x) {
        @strongify(self);
        [self updateVolume:self.volumeValue];
        self.numberValueView.progress = self.volumeValue * 100;
    }];
}

- (void)showControls {
    [UIView animateWithDuration:0.5
                     animations:^{
        self.alpha = 1.0;
    } completion:^(BOOL finished) {
        self.hidden = NO;
    }];
    self.isControlsVisible = YES;
}

- (void)hideControls {
    [UIView animateWithDuration:0.5
                     animations:^{
        self.alpha = 0.0;
    } completion:^(BOOL finished) {
        self.hidden = YES;
    }];
    self.isControlsVisible = NO;
}

- (void)updateVolume:(CGFloat)volume {
    UIView *view = self.volumeView.subviews.firstObject;
    if (![view isKindOfClass:UISlider.class]) return;
    UISlider *slider = (UISlider *)view;
    slider.value = volume;
}

- (void)onPlayTap:(id)sender {
    [self _changePlayButtonIcon:self.player.isPlaying];
    if (self.player.isPlaying) {
        [self.player pause];
    } else {
        [self.player resume];
    }
}

- (void)_seekToPlay:(id)sender {
    if ([sender isKindOfClass:UISlider.class]) {
        UISlider *slider = sender;
        CGFloat time = slider.value;

        [self.player seek:time];
    }
}

- (void)onFullscreenTap:(id)sender {
    if ([self.parentView respondsToSelector:@selector(onFullscreenTap:)]) {
        [self.parentView performSelector:@selector(onFullscreenTap:) withObject:sender];
    }
}

- (void)_changePlayButtonIcon:(BOOL)isPlaying {
    NSString *imageName = isPlaying ? @"player/play" : @"player/pause";
    [self _updateIcon:self.playButton icon:imageName];
}

- (void)showNumberValueIndicator:(BOOL)visible {
    [UIView animateWithDuration:visible ? 0.15 : 0.25
                     animations:^{
        self.numberValueView.alpha = visible ? 1.0 : 0;
    } completion:^(BOOL finished) {
        self.numberValueView.hidden = !visible;
    }];
}

- (void)showBrightnessIndicator:(BOOL)visible {
    self.numberValueView.iconName = @"player/brightness";
    [self showNumberValueIndicator:visible];
}

- (void)showVolumeIndicator:(BOOL)visible {
    self.numberValueView.iconName = @"player/volume";
    [self showNumberValueIndicator:visible];
}

#pragma mark - Getter
- (UIView *)playButton {
    if (!_playButton) {
        UIView *view = [self _makeControlView:@"player/pause"];
        _playButton = view;
    }
    return _playButton;
}

- (UIView *)fullscreenButton {
    if (!_fullscreenButton) {
        UIView *view = [self _makeControlView:@"player/fullscreen"];
        _fullscreenButton = view;
    }
    return _fullscreenButton;
}

- (UIView *)captionButton {
    if (!_captionButton) {
        UIView *view = [self _makeControlView:@"player/caption"];
        _captionButton = view;
    }
    return _captionButton;
}

- (UIView *)settingButton {
    if (!_settingButton) {
        _settingButton = [self _makeControlView:@"player/setting"];
    }
    return _settingButton;
}

- (UIProgressView *)progressBar {
    if (!_progressBar) {
        _progressBar = [[UIProgressView alloc] init];
        [_progressBar setProgressViewStyle:UIProgressViewStyleBar];
        _progressBar.progressTintColor = UIColor.whiteColor;
        _progressBar.trackTintColor = [UIColor.whiteColor colorWithAlphaComponent:0.3];
        _progressBar.layer.cornerRadius = 3;
        _progressBar.clipsToBounds = YES;
        _progressBar.hidden = YES;
    }
    return _progressBar;
}

- (NSProgress *)progress {
    if (!_progress) {
        _progress = [[NSProgress alloc] init];
    }
    return _progress;
}

- (UISlider *)sliderBar {
    if (!_sliderBar) {
        _sliderBar = [[UISlider alloc] init];
        _sliderBar.tintColor = UIColor.whiteColor;
        _sliderBar.continuous = NO;
    }
    return _sliderBar;
}


- (UILabel *)durationLabel {
    if (!_durationLabel) {
        _durationLabel = [UILabel new];
        _durationLabel.font = [UIFont systemFontOfSize:13];
        _durationLabel.text = @"0:00";
        _durationLabel.textColor = UIColor.whiteColor;
    }
    return _durationLabel;
}

- (UILabel *)titleLabel {
    if (!_titleLabel) {
        _titleLabel = [UILabel new];
        _titleLabel.font = [UIFont systemFontOfSize:16];
        _titleLabel.textColor = UIColor.whiteColor;
        _titleLabel.text = nil;
    }
    return _titleLabel;
}

- (UIActivityIndicatorView *)indicator {
    if (!_indicator) {
        _indicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleMedium];
    }
    return _indicator;
}

- (UIView *)_makeControlView:(NSString *)iconName {
    UIImage *icon = nil;
    icon = [[UIImage imageNamed:iconName] imageWithTintColor:UIColor.whiteColor renderingMode:UIImageRenderingModeAutomatic];
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
    UIImageView *imageView = (UIImageView *)view.subviews.firstObject;
    if (![imageView isKindOfClass:UIImageView.class]) {
        return;
    }
    UIImage *icon = [[UIImage imageNamed:iconName] imageWithTintColor:UIColor.whiteColor renderingMode:UIImageRenderingModeAutomatic];
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

- (PlayerNumberValueView *)numberValueView {
    if (!_numberValueView) {
        _numberValueView = [PlayerNumberValueView new];
        _numberValueView.maxValue = 100.f;
        _numberValueView.minValue = 0.f;
        _numberValueView.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.2];
        _numberValueView.layer.borderWidth = 1;
        _numberValueView.layer.cornerRadius = 7;
        _numberValueView.layer.masksToBounds = YES;
        _numberValueView.sliderBar.enabled = NO;
        _numberValueView.clipsToBounds = YES;
        _numberValueView.layer.borderColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.25].CGColor;
        _numberValueView.hidden = YES;
    }
    return _numberValueView;
}

- (MPVolumeView *)volumeView {
    if (!_volumeView) {
        _volumeView = [[MPVolumeView alloc] initWithFrame:CGRectZero];
        // hide system volume indicator
        _volumeView.alpha = 0.00001;
        _volumeView.hidden = NO;
    }
    return _volumeView;
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

//
//  PlayerControlView.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/30.
//

#import "PlayerControlView.h"
#import "UIView+FindViewController.h"
#import "PlayerSystemVolumeView.h"

static NSUInteger const kIconSize = 48;

@interface PlayerControlView ()
@property (nonatomic, strong) MPVolumeView<PlayerSystemVolumeView> *volumeView;
@end

@implementation PlayerControlView

- (instancetype)init {
    self = [super init];
    if (self) {
        _isControlsVisible = YES;
        _iconSize = kIconSize;
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
    [self addSubview:self.audioButton];
    [self addSubview:self.progressBar];
    [self addSubview:self.sliderBar];
    [self addSubview:self.durationLabel];
    [self addSubview:self.titleLabel];
    [self addSubview:self.indicator];
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
    
    [self.fullscreenButton remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.size.equalTo(@(self.iconSize));
        make.top.equalTo(self).with.offset(insets.top ?: 24);
        make.right.equalTo(self.progressBar);
    }];
  
    [self.captionButton remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.size.equalTo(@(self.iconSize));
        make.centerY.equalTo(self.fullscreenButton);
        make.right.equalTo(self.fullscreenButton.left).with.offset(-10);
    }];
    
    [self.audioButton remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.size.equalTo(@(self.iconSize));
        make.centerY.equalTo(self.fullscreenButton);
        make.right.equalTo(self.captionButton.left).with.offset(-10);
    }];

    [self.indicator remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.center.equalTo(self);
    }];
    
    [self.volumeView remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.top.equalTo(self.top);
        make.centerX.equalTo(self);
    }];
}

- (void)_bind {
    self.volumeValue = self.defaultVolumeValue;
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(onPlayTap:)];
    self.playButton.userInteractionEnabled = YES;
    [self.playButton addGestureRecognizer:tap];

    UITapGestureRecognizer *fullscreenTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(onFullscreenTap:)];
    self.fullscreenButton.userInteractionEnabled = YES;
    [self.fullscreenButton addGestureRecognizer:fullscreenTap];
  
    UITapGestureRecognizer *audioTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(onSelectAudioTap:)];
    self.audioButton.userInteractionEnabled = YES;
    [self.audioButton addGestureRecognizer:audioTap];
    
    UITapGestureRecognizer *captionTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(onSelectCaptionTap:)];
    self.captionButton.userInteractionEnabled = YES;
    [self.captionButton addGestureRecognizer:captionTap];

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
    
    [[RACObserve(self, brightnessValue) deliverOnMainThread] subscribeNext:^(id  _Nullable x) {
        @strongify(self);
        UIScreen.mainScreen.brightness = self.brightnessValue;
    }];
    
    [[[RACObserve(self, volumeValue) deliverOnMainThread] skip:1] subscribeNext:^(id  _Nullable x) {
        @strongify(self);
        [self updateVolume:self.volumeValue];
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
    UISlider *slider = nil;
    if ([self.volumeView respondsToSelector:@selector(volumeSlider)]) {
        slider = self.volumeView.volumeSlider;
    } else {
        UIView *view = self.volumeView.subviews.firstObject;
        if (![view isKindOfClass:UISlider.class]) return;
        slider = (UISlider *)view;
    }
    slider.value = volume;
}

- (CGFloat)defaultVolumeValue {
    UIView *view = self.volumeView.subviews.firstObject;
    if (![view isKindOfClass:UISlider.class]) return AVAudioSession.sharedInstance.outputVolume;
    UISlider *slider = (UISlider *)view;
    return slider.value;
}

- (void)onPlayTap:(id)sender {
    [self _changePlayButtonIcon:!self.player.isPlaying];
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

- (void)onSelectAudioTap:(id)sender {
    if ([self.parentView respondsToSelector:@selector(onSelectAudioTap:)]) {
        [self.parentView performSelector:@selector(onSelectAudioTap:) withObject:sender];
    }
}

- (void)onSelectCaptionTap:(id)sender {
    if ([self.parentView respondsToSelector:@selector(onSelectCaptionTap:)]) {
        [self.parentView performSelector:@selector(onSelectCaptionTap:) withObject:sender];
    }
}

- (void)updatePlayState:(BOOL)isPlaying {
    [self _changePlayButtonIcon:isPlaying];
}

- (void)_changePlayButtonIcon:(BOOL)isPlaying {
    NSString *imageName = isPlaying ? @"player/pause" : @"player/play";
    [self _updateIcon:self.playButton icon:imageName];
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

- (UIView *)audioButton {
    if (!_audioButton) {
        _audioButton = [self _makeControlView:@"player/volume"];
    }
    return _audioButton;
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

- (PlayerSlider *)sliderBar {
    if (!_sliderBar) {
        _sliderBar = [[PlayerSlider alloc] init];
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

- (MPVolumeView<PlayerSystemVolumeView> *)volumeView {
    if (!_volumeView) {
        _volumeView = (MPVolumeView<PlayerSystemVolumeView> *)[[MPVolumeView alloc] init];
        // hide system volume indicator
        _volumeView.alpha = 0.00001;
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
        self.audioButton,
    ];
    for (UIView *view in views) {
        view.layer.cornerRadius = iconSize / 2;
    }
    [self _layout];
}

@end

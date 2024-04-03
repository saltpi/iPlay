//
//  PlayerNumberValueView.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/4/3.
//

#import "PlayerNumberValueView.h"

@implementation PlayerNumberValueView

- (instancetype)init {
    self = [super init];
    if (self) {
        [self _setupUI];
        [self _layout];
        [self _bind];
    }
    return self;
}

- (void)_setupUI {
    [self addSubview:self.sliderBar];
    [self addSubview:self.iconView];
}

- (void)_layout {
    @weakify(self);
    [self.iconView remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.left.equalTo(self).with.offset(10);
        make.top.equalTo(self).with.offset(10);
        make.bottom.equalTo(self).with.offset(-10);
        make.size.equalTo(@48);
    }];
    
    [self.sliderBar remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.left.equalTo(self.iconView.right).with.offset(10);
        make.right.equalTo(self).with.offset(-10);
        make.centerY.equalTo(self);
    }];
}

- (void)_bind {
    @weakify(self);
    [[RACObserve(self, iconName) deliverOnMainThread] subscribeNext:^(id  _Nullable x) {
        @strongify(self);
        self.iconView.image = [[UIImage imageNamed:self.iconName] imageWithTintColor:UIColor.whiteColor];
    }];
    
    [RACObserve(self, maxValue) subscribeNext:^(id  _Nullable x) {
        @strongify(self);
        self.sliderBar.maximumValue = self.maxValue;
    }];
    
    [[RACObserve(self, minValue) deliverOnMainThread] subscribeNext:^(id  _Nullable x) {
        @strongify(self);
        self.sliderBar.minimumValue = self.minValue;
    }];
    
    [[RACObserve(self, progress) deliverOnMainThread] subscribeNext:^(id  _Nullable x) {
        @strongify(self);
        self.sliderBar.value = self.progress;
    }];
}



#pragma mark - Getter
- (UISlider *)sliderBar {
    if (!_sliderBar) {
        _sliderBar = [[UISlider alloc] init];
        _sliderBar.tintColor = UIColor.whiteColor;
        _sliderBar.continuous = NO;
    }
    return _sliderBar;
}

- (UIImageView *)iconView {
    if (!_iconView) {
        _iconView = [UIImageView new];
        _iconView.contentMode = UIViewContentModeScaleAspectFit;
    }
    return _iconView;
}
@end

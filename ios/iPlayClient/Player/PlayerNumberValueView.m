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
        make.left.equalTo(self).with.offset(6);
        make.top.equalTo(self).with.offset(6);
        make.bottom.equalTo(self).with.offset(-8);
        make.size.equalTo(@22);
    }];
    
    [self.sliderBar remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.left.equalTo(self.iconView.right).with.offset(8);
        make.right.equalTo(self).with.offset(-8);
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
    
    [RACObserve(self, progress) subscribeNext:^(id  _Nullable x) {
        @strongify(self);
        self.sliderBar.value = self.progress;
    }];
}


- (CGRect)thumbRectForBounds:(CGRect)bounds trackRect:(CGRect)rect value:(float)value {
    return CGRectMake(0, 0, 20, 20);
}

- (UIImage *)resizeImage:(UIImage *)image toSize:(CGSize)newSize {
    UIGraphicsBeginImageContextWithOptions(newSize, NO, 0.0);
    [image drawInRect:CGRectMake(0, 0, newSize.width, newSize.height)];
    UIImage *newImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return newImage;
}

#pragma mark - Getter
- (UISlider *)sliderBar {
    if (!_sliderBar) {
        UISlider *sliderBar = [[UISlider alloc] init];
        sliderBar.tintColor = UIColor.whiteColor;
        sliderBar.continuous = YES;
        UIImage *thumbImage = [self resizeImage:[sliderBar thumbImageForState:UIControlStateNormal] toSize:CGSizeMake(10, 10)];
        [sliderBar setThumbImage:thumbImage forState:UIControlStateNormal];
        _sliderBar = sliderBar;
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

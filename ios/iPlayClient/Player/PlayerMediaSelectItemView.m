//
//  PlayerMediaSelectItemView.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/4/8.
//

#import "PlayerMediaSelectItemView.h"

@interface PlayerMediaSelectItemView ()
@property (nonatomic, strong) UIImageView *iconView;
@property (nonatomic, strong) UILabel *labelView;
@end

@implementation PlayerMediaSelectItemView

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        [self _setupUI];
        [self _layout];
        [self _bind];
    }
    return self;
}

- (void)_setupUI {
    self.backgroundColor = UIColor.clearColor;
    [self addSubview:self.iconView];
    [self addSubview:self.labelView];
}

- (void)_layout {
    @weakify(self);
    [self.iconView remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.left.equalTo(self).with.offset(8);
        make.centerY.equalTo(self);
        make.size.equalTo(@18);
    }];
    
    [self.labelView remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.centerY.equalTo(self.iconView);
        make.left.equalTo(self.iconView.right).with.offset(6);
        make.right.equalTo(self).with.offset(-6);
    }];
}

- (void)_bind {
    @weakify(self);
    [RACObserve(self, isSelected) subscribeNext:^(id  _Nullable x) {
        @strongify(self);
        [self _updateSelectStateIcon:self.isSelected];
    }];
    
    [[RACObserve(self, title) deliverOnMainThread] subscribeNext:^(id  _Nullable x) {
        @strongify(self);
        self.labelView.text = self.title;
    }];
}

- (void)_updateSelectStateIcon:(BOOL)isSelected {
    NSString *iconName = isSelected ? @"player/checkmark" : nil;
    UIImage *icon = [UIImage imageNamed:iconName];
    self.iconView.image = [icon imageWithTintColor:UIColor.whiteColor];
}

#pragma mark - Getter
- (UIImageView *)iconView {
    if (!_iconView) {
        UIImageView *imageView = [[UIImageView alloc] init];
        imageView.contentMode = UIViewContentModeScaleAspectFit;
        _iconView = imageView;
    }
    return _iconView;
}

- (UILabel *)labelView {
    if (!_labelView) {
        UILabel *labelView = [UILabel new];
        labelView.font = [UIFont systemFontOfSize:16];
        labelView.textColor = UIColor.whiteColor;
        _labelView = labelView;
    }
    return _labelView;
}

@end

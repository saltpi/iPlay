//
//  PlayerSlider.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/4/10.
//

#import "PlayerSlider.h"

@interface PlayerSlider ()
@property (nonatomic, strong) UIView *cachedView;
@property (nonatomic, copy) NSArray<PlayerSeekableModel *> *ranges;
@property (nonatomic, assign) CGFloat maxValue;
@end

@implementation PlayerSlider

- (instancetype)init {
    self = [super init];
    if (self) {
        [self _setupUI];
        [self _layout];
    }
    return self;
}

- (void)_setupUI {
    [self addSubview:self.cachedView];
}

- (void)_layout {
    @weakify(self);
    [self.cachedView remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.edges.equalTo(self);
    }];
}

- (void)drawRect:(CGRect)rect {
    CGRect trackRect = [self trackRectForBounds:self.bounds];
    CGFloat width = trackRect.size.width;
    CGFloat offsetX = trackRect.origin.x;
    CGFloat offsetY = trackRect.origin.y + 2;
    UIBezierPath *path = [UIBezierPath new];
    [UIColor.greenColor setStroke];
    path.lineWidth = trackRect.size.height - 2;
    for (PlayerSeekableModel *range in self.ranges) {
        CGFloat start = offsetX + range.start * width / self.maxValue;
        CGFloat end = offsetX + range.end * width / self.maxValue;
        [path moveToPoint:CGPointMake(start, offsetY)];
        [path addLineToPoint:CGPointMake(end, offsetY)];
        [path stroke];
    }
}

- (void)setSeekableRanges:(NSArray<PlayerSeekableModel *> *)ranges maxValue:(double)maxValue {
    self.ranges = ranges;
    self.maxValue = maxValue;
    [self setNeedsDisplay];
}

- (UIView *)cachedView {
    if (!_cachedView) {
        _cachedView = [UIView new];
    }
    return _cachedView;
}

@end

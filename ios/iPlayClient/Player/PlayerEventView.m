//
//  PlayerEventView.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/20.
//

#import "PlayerEventView.h"

@interface PlayerEventView () <UIGestureRecognizerDelegate>

@end

@implementation PlayerEventView

- (instancetype)init {
    self = [super init];

    if (self) {
        [self _setupEvent];
    }

    return self;
}


- (void)_setupEvent {
    [self addSubview:self.numberValueView];
    @weakify(self);
    [self.numberValueView remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.centerX.equalTo(self);
        make.top.equalTo(self).with.offset(10);
        make.width.greaterThanOrEqualTo(200);
    }];
    
    
    self.userInteractionEnabled = YES;
    UIPanGestureRecognizer *panRecognizer = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(_handleSwipe:)];
    panRecognizer.delegate = self;
    [self addGestureRecognizer:panRecognizer];

    UITapGestureRecognizer *tapRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(_handleTap:)];
    [self addGestureRecognizer:tapRecognizer];
}

- (void)_handleTap:(UITapGestureRecognizer *)gesture {
    // ignore areas
    if (self.eventDelegate) {
        [self.eventDelegate playerGestureEvent:gesture location:CGPointZero];
    }
}

- (void)_handleSwipe:(UIPanGestureRecognizer *)gesture {
    if (self.eventDelegate) {
        [self.eventDelegate playerGestureEvent:gesture location:CGPointZero];
    }
}

#pragma mark - UIView
- (UIView *)hitTest:(CGPoint)point withEvent:(UIEvent *)event {
    for (UIView *view in self.ignoreViews) {
        CGPoint pointInView = [self convertPoint:point toView:view];
        if ([view pointInside:pointInView withEvent:event]) {
            return view;
        }
    }

    return [super hitTest:point withEvent:event];
}

#pragma mark - UIGestureRecognizerDelegate
- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldReceiveTouch:(UITouch *)touch {
    for (UIView *view in self.ignoreViews) {
        CGPoint point = [touch locationInView:view];
        if ([view pointInside:point withEvent:nil]) {
            return NO;
        }
    }
    return YES;
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

@end

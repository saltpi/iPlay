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

@end

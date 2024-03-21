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
    UISwipeGestureRecognizer *swipeLeft = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(_handleSwipe:)];
    swipeLeft.direction = UISwipeGestureRecognizerDirectionLeft;
    swipeLeft.delegate = self;
    [self addGestureRecognizer:swipeLeft];

    UISwipeGestureRecognizer *swipeRight = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(_handleSwipe:)];
    swipeRight.direction = UISwipeGestureRecognizerDirectionRight;
    swipeRight.delegate = self;
    [self addGestureRecognizer:swipeRight];

    UISwipeGestureRecognizer *swipeUp = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(_handleSwipe:)];
    swipeUp.direction = UISwipeGestureRecognizerDirectionUp;
    [self addGestureRecognizer:swipeUp];

    UISwipeGestureRecognizer *swipeDown = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(_handleSwipe:)];
    swipeDown.direction = UISwipeGestureRecognizerDirectionDown;
    [self addGestureRecognizer:swipeDown];

    UITapGestureRecognizer *tapRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(_handleTap:)];
    [self addGestureRecognizer:tapRecognizer];
}

- (void)_handleTap:(UITapGestureRecognizer *)gesture {
    // ignore areas
    if (self.eventDelegate) {
        CGPoint location = [gesture locationInView:self];
        [self.eventDelegate playerGestureEvent:gesture location:location];
    }
}

- (void)_handleSwipe:(UISwipeGestureRecognizer *)gesture {
    if (self.eventDelegate) {
        CGPoint location = [gesture locationInView:self];
        [self.eventDelegate playerGestureEvent:gesture location:location];
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
        if (CGRectContainsPoint(view.frame, point)) {
            return NO;
        }
    }
    return YES;
}

@end

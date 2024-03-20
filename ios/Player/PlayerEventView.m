//
//  PlayerEventView.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/20.
//

#import "PlayerEventView.h"

@implementation PlayerEventView

- (instancetype)init {
  self = [super init];
  if (self) {
    [self _setupEvent];
  }
  return self;
}

- (UIView *)hitTest:(CGPoint)point withEvent:(UIEvent *)event {
    for (UIView *view in self.ignoreViews) {
        CGPoint pointInView = [self convertPoint:point toView:view];

        if ([view pointInside:pointInView withEvent:event]) {
            return view;
        }
    }

    return [super hitTest:point withEvent:event];
}

- (void)_setupEvent {
  UISwipeGestureRecognizer *swipeLeft = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(_handleSwipe:)];
  swipeLeft.direction = UISwipeGestureRecognizerDirectionLeft;
  [self addGestureRecognizer:swipeLeft];

  UISwipeGestureRecognizer *swipeRight = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(_handleSwipe:)];
  swipeRight.direction = UISwipeGestureRecognizerDirectionRight;
  [self addGestureRecognizer:swipeRight];

  UISwipeGestureRecognizer *swipeUp = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(_handleSwipe:)];
  swipeUp.direction = UISwipeGestureRecognizerDirectionUp;
  [self addGestureRecognizer:swipeUp];

  UISwipeGestureRecognizer *swipeDown = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(_handleSwipe:)];
  swipeDown.direction = UISwipeGestureRecognizerDirectionDown;
  [self addGestureRecognizer:swipeDown];

}


- (void)_handleSwipe:(UISwipeGestureRecognizer *)gesture {
    CGPoint location = [gesture locationInView:self];
    if (location.x < self.bounds.size.width / 2) {
        NSLog(@"Left side swipe up detected");
    } else {
        NSLog(@"Right side swipe up detected");
    }
    if (gesture.direction == UISwipeGestureRecognizerDirectionLeft) {
        NSLog(@"Left swipe detected");
    } else if (gesture.direction == UISwipeGestureRecognizerDirectionRight) {
        NSLog(@"Right swipe detected");
    } else if (gesture.direction == UISwipeGestureRecognizerDirectionUp) {
        NSLog(@"Up swipe detected");
    } else if (gesture.direction == UISwipeGestureRecognizerDirectionDown) {
        NSLog(@"Down swipe detected");
    }
}

@end

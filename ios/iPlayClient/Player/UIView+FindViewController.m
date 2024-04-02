//
//  UIView+FindViewController.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/21.
//

#import "UIView+FindViewController.h"


@implementation UIView (FindViewController)

- (UIViewController *)firstAvailableUIViewController {
    UIResponder *responder = [self nextResponder];
    while (responder != nil) {
        if ([responder isKindOfClass:[UIViewController class]]) {
            return (UIViewController *)responder;
        }
        responder = [responder nextResponder];
    }
    return nil;
}

@end

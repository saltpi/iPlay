//
//  IPLUIModule.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/4/4.
//

#import "IPLUIModule.h"

@implementation IPLUIModule

RCT_EXPORT_MODULE(UIModule);

RCT_EXPORT_BLOCKING_SYNCHRONOUS_METHOD(statusBarHeight) {
    UIStatusBarManager *statusBarManager = [UIApplication sharedApplication].windows.firstObject.windowScene.statusBarManager;
    CGFloat statusBarHeight = statusBarManager.statusBarFrame.size.height;
    return @(statusBarHeight);
}

+ (CGSize)windowSize {
    return [UIApplication sharedApplication].windows.firstObject.bounds.size;
}
@end

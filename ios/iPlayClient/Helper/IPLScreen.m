//
//  IPLScreen.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/4/11.
//

#import "IPLScreen.h"
#import <UIKit/UIKit.h>

@implementation IPLScreen

+ (void)keepScreenOn {
    UIApplication.sharedApplication.idleTimerDisabled = YES;
}

+ (void)keepScreenOff {
    UIApplication.sharedApplication.idleTimerDisabled = NO;
}

@end

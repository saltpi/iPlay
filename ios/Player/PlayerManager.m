//
//  Player.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/20.
//

#import "PlayerManager.h"
#import "PlayerView.h"
#import <React/RCTViewManager.h>
#import <React/RCTUIManager.h>
#import <React/RCTLog.h>


@interface PlayerManager ()
@end

@implementation PlayerManager

RCT_EXPORT_MODULE(PlayerView)

RCT_EXPORT_VIEW_PROPERTY(onPlayStateChange, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(iconSize, NSUInteger)


- (UIView *)view {
    PlayerView *view = [PlayerView new];

    view.backgroundColor = UIColor.whiteColor;
    return view;
}

RCT_CUSTOM_VIEW_PROPERTY(bgcolor, NSString, UIView) {
    uint64_t hex = strtoul([json UTF8String], 0, 16);
    UIColor *color = UIColorFromRGB(hex);
    [view setBackgroundColor:color];
}

RCT_CUSTOM_VIEW_PROPERTY(url, NSString, UIView) {
    NSString *url = json;
    PlayerView *instance = (PlayerView *)view;

    [instance.player loadVideo:url];
    [instance.player play];
}

RCT_CUSTOM_VIEW_PROPERTY(title, NSString, UIView) {
    NSString *title = json;
    PlayerView *instance = (PlayerView *)view;
    instance.title = title;
}

RCT_EXPORT_METHOD(stop:(nonnull NSNumber*)reactTag) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        PlayerView *view = (PlayerView *)viewRegistry[reactTag];
        if (!view || ![view isKindOfClass:[PlayerView class]]) {
            RCTLogError(@"Cannot find NativeView with tag #%@", reactTag);
            return;
        }
        [view.player stop];
    }];
}

@end

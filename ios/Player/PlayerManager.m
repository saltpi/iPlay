//
//  Player.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/20.
//

#import <MobileVLCKit/MobileVLCKit.h>
#import "PlayerManager.h"
#import "PlayerView.h"

@interface PlayerManager ()<VLCMediaPlayerDelegate>
@end

@implementation PlayerManager

RCT_EXPORT_MODULE(PlayerView)

RCT_EXPORT_VIEW_PROPERTY(onPlayStateChange, RCTDirectEventBlock)

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

    instance.player.media = [VLCMedia mediaWithURL:[NSURL URLWithString:url]];
    [instance.player play];
}

@end

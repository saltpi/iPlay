//
//  Player.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/20.
//

#import "Player.h"
#import <MobileVLCKit/MobileVLCKit.h>

@implementation Player

RCT_EXPORT_MODULE(Player)

- (UIView *)view {
  UIView *view = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 100, 100)];
  view.backgroundColor = UIColor.redColor;
  self.player.drawable = view;
  return view;
}

RCT_CUSTOM_VIEW_PROPERTY(bgcolor, NSString, UIView) {
  uint64_t hex = strtoul([json UTF8String], 0, 16);
  UIColor *color = UIColorFromRGB(hex);
  [view setBackgroundColor:color];
}

RCT_CUSTOM_VIEW_PROPERTY(url, NSString, UIView) {
  NSString *url = json;
  self.player.media=[VLCMedia mediaWithURL:[NSURL URLWithString:url]];
  [self.player play];
}


#pragma mark - Getter
- (VLCMediaPlayer *)player {
  if (!_player) {
    _player = [[VLCMediaPlayer alloc] init];
  }
  return _player;
}

@end

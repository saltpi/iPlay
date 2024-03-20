//
//  Player.h
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/20.
//

#import <Foundation/Foundation.h>
#import <React/RCTViewManager.h>
#import <MobileVLCKit/MobileVLCKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface Player : RCTViewManager
@property (nonatomic, strong) VLCMediaPlayer *player;
@end

NS_ASSUME_NONNULL_END

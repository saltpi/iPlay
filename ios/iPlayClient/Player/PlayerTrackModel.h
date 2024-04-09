//
//  PlayerTrackModel.h
//  iPlayClient
//
//  Created by 赫拉 on 2024/4/8.
//

#import <Foundation/Foundation.h>

typedef NS_ENUM(NSUInteger, PlayerTrackType) {
    PlayerTrackTypeNone,
    PlayerTrackTypeVideo,
    PlayerTrackTypeAudio,
    PlayerTrackTypeSubtitle,
};

NS_ASSUME_NONNULL_BEGIN

@interface PlayerTrackModel : NSObject
@property (nonatomic) NSString *ID;
@property (nonatomic) NSString *lang;
@property (nonatomic) NSString *title;
@property (nonatomic) PlayerTrackType type;
@end

NS_ASSUME_NONNULL_END

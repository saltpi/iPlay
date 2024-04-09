//
//  MPVPlayer.h
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/25.
//

#import <Foundation/Foundation.h>
#import "PlayerTrackModel.h"

typedef enum : NSUInteger {
    PlayEventTypeOnProgress,
    PlayEventTypeOnPause,
    PlayEventTypeOnPauseForCache,
    PlayEventTypeDuration,
    PlayEventTypeEnd,
} PlayEventType;

NS_ASSUME_NONNULL_BEGIN

@protocol VideoPlayerDelegate <NSObject>
- (void)onPlayEvent:(PlayEventType)event
               data:(NSDictionary *)data;
@end

@protocol VideoPlayer <NSObject>
- (void)setDelegate:(id<VideoPlayerDelegate>)delegate;
- (id<VideoPlayerDelegate>)delegate;
- (NSUInteger)duration;

- (BOOL)isPlaying;
- (void)loadVideo:(NSString *)url;
- (void)jumpBackward:(NSInteger)seconds;
- (void)jumpForward:(NSInteger)seconds;
- (CGFloat)volume;
- (NSInteger)brightness;
- (void)volumeUp:(CGFloat)percent;
- (void)volumeDown:(CGFloat)percent;
- (void)brightnessUp:(NSInteger)delta;
- (void)brightnessDown:(NSInteger)delta;
- (void)play;
- (void)resume;
- (void)pause;
- (void)stop;
- (void)seek:(NSUInteger)timeSeconds;
- (void)resize:(CGSize)size;
- (void)setSubtitleFont:(NSString *)fontName;
- (void)quit;

@optional
- (NSArray<PlayerTrackModel *> *)subtitles;
- (NSArray<PlayerTrackModel *> *)audios;
- (NSArray<PlayerTrackModel *> *)videos;
- (NSArray<PlayerTrackModel *> *)tracks;
- (void)useSubtitle:(NSInteger)ID;
- (void)useAudio:(NSInteger)ID;
- (void)useVideo:(NSInteger)ID;
- (void)useTrack:(PlayerTrackModel *)model;

@optional
- (void)setDrawable:(id)view;
- (void)keepaspect;
@end

NS_ASSUME_NONNULL_END

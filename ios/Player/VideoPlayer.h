//
//  MPVPlayer.h
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/25.
//

#import <Foundation/Foundation.h>

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
- (void)jumpBackward:(NSUInteger)seconds;
- (void)jumpForward:(NSUInteger)seconds;
- (void)volumeUp:(CGFloat)percent;
- (void)volumeDown:(CGFloat)percent;
- (void)play;
- (void)resume;
- (void)pause;
- (void)stop;
- (void)seek:(NSUInteger)timeSeconds;
- (void)resize:(CGSize)size;

@optional
- (void)setDrawable:(id)view;
- (void)keepaspect;
@end

NS_ASSUME_NONNULL_END

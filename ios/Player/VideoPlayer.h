//
//  MPVPlayer.h
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/25.
//

#import <Foundation/Foundation.h>

typedef enum : NSUInteger {
    PlayEventTypeOnProgress,
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

- (void)loadVideo:(NSString *)url;
- (void)play;
- (void)pause;
- (void)stop;
- (void)seek:(NSUInteger)timeSeconds;

@optional
- (void)setDrawable:(id)view;
@end

NS_ASSUME_NONNULL_END

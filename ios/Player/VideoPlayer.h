//
//  MPVPlayer.h
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/25.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@protocol VideoPlayer <NSObject>
- (void)loadVideo:(NSString *)url;
- (void)play;
- (void)pause;
- (void)stop;

@optional
- (void)setDrawable:(id)view;
@end

NS_ASSUME_NONNULL_END

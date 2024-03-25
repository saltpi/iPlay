//
//  MPVPlayer.h
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/25.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@protocol MPVPlayer <NSObject>
- (void)loadVideo:(NSString *)url;
- (void)play;
- (void)pause;
@end

NS_ASSUME_NONNULL_END

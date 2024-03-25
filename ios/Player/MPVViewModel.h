//
//  MPVViewModel.h
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/25.
//

#import <Foundation/Foundation.h>
#import "MPVPlayer.h"

NS_ASSUME_NONNULL_BEGIN

@interface MPVViewModel : NSObject<MPVPlayer>
- (void)initWithLayer:(CAMetalLayer *)layer;
@end

NS_ASSUME_NONNULL_END

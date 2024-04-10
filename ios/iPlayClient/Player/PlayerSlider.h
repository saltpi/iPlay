//
//  PlayerSlider.h
//  iPlayClient
//
//  Created by 赫拉 on 2024/4/10.
//

#import <UIKit/UIKit.h>
#import "PlayerSeekableModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface PlayerSlider : UISlider
- (void)setSeekableRanges:(NSArray<PlayerSeekableModel *> *)ranges
                 maxValue:(double)maxValue;
@end

NS_ASSUME_NONNULL_END

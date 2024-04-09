//
//  PlayerSeekableImageView.h
//  iPlayClient
//
//  Created by 赫拉 on 2024/4/9.
//

#import <UIKit/UIKit.h>
#import "PlayerSeekableModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface PlayerSeekableImageView : UIView
+ (UIImage *)seekableImage:(NSArray<PlayerSeekableModel *> *)segments
                  maxValue:(CGFloat)maxValue;
@end

NS_ASSUME_NONNULL_END

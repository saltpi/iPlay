//
//  PlayerSeekableImageView.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/4/9.
//

#import "PlayerSeekableImageView.h"

@implementation PlayerSeekableImageView

+ (UIImage *)seekableImage:(NSArray<PlayerSeekableModel *> *)segments maxValue:(CGFloat)maxValue {
    UIGraphicsBeginImageContextWithOptions(CGSizeMake(100, 1), NO, 0);
    CGContextRef context = UIGraphicsGetCurrentContext();

    for (PlayerSeekableModel *segment in segments) {
        double start = segment.start * 100 / maxValue;
        double end = segment.end * 100 / maxValue;
        CGContextSetFillColorWithColor(context, UIColor.greenColor.CGColor);
        CGContextFillRect(context, CGRectMake(start, 0, end - start, 1));
    }

    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return image;
}

@end

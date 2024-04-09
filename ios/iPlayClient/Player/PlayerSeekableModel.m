//
//  PlayerSeekableModel.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/4/9.
//

#import "PlayerSeekableModel.h"

@implementation PlayerSeekableModel

- (NSString *)description {
    return [NSString stringWithFormat:@"%.2lf-%.2lf", self.start, self.end];
}
@end

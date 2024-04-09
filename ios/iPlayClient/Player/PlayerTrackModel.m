//
//  PlayerTrackModel.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/4/8.
//

#import "PlayerTrackModel.h"

@implementation PlayerTrackModel

- (NSString *)description {
    return [NSString stringWithFormat:@"%@ %@ %@", self.ID, self.lang, self.title];
}

@end

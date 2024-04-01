//
//  PlayerSubtitleView.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/4/1.
//

#import "PlayerSubtitleView.h"

@implementation PlayerSubtitleView

+ (NSArray<NSString *> *)installedFontName {
    NSMutableArray<NSString *> *allFontNames = @[].mutableCopy;
    NSArray<NSString *> *familyNames = [UIFont familyNames];
    for (NSString *familyName in familyNames) {
        NSArray<NSString *> *fontNames = [UIFont fontNamesForFamilyName:familyName];
        [allFontNames addObjectsFromArray:fontNames];
    }
    return allFontNames;
}
@end

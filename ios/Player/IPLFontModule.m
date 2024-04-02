//
//  IPLFontModule.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/4/2.
//

#import "IPLFontModule.h"

@implementation IPLFontModule

RCT_EXPORT_MODULE(FontModule);

RCT_EXPORT_METHOD(fontListAsync:(RCTPromiseResolveBlock)resolve rejector:(RCTPromiseRejectBlock)reject) {
    resolve([IPLFontModule installedFontName]);
}

RCT_EXPORT_METHOD(fontFamilyListAsync:(RCTPromiseResolveBlock)resolve rejector:(RCTPromiseRejectBlock)reject) {
    resolve([IPLFontModule installedFontFamilyName]);
}

RCT_EXPORT_BLOCKING_SYNCHRONOUS_METHOD(fontFamilyList) {
    return [IPLFontModule installedFontFamilyName];
}

RCT_EXPORT_BLOCKING_SYNCHRONOUS_METHOD(fontList) {
    return [IPLFontModule installedFontName];
}

+ (NSArray<NSString *> *)installedFontFamilyName {
    return [UIFont familyNames];
}

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

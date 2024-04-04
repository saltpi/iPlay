//
//  IPLFontModule.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/4/2.
//

#import "IPLFontModule.h"
#import <CoreText/CoreText.h>
#import "Router.h"
#import <React/RCTComponent.h>

NSString *ON_SELECT_FONT_CHANGE = @"onSelectFontChange";
const NSString * IPLBUILTIN_FONT_NAME= @"LXGWWenKaiLite-Regular";

@interface IPLFontModule ()<UIFontPickerViewControllerDelegate>
@property (nonatomic) BOOL hasListeners;
@end

@implementation IPLFontModule

- (NSArray<NSString *> *)supportedEvents {
    return @[ON_SELECT_FONT_CHANGE];
}

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

RCT_EXPORT_METHOD(showFontPicker) {
    dispatch_async(dispatch_get_main_queue(), ^{
       [self _showFontPicker];
    });
}

- (void)startObserving {
    _hasListeners = YES;
}

- (void)stopObserving {
    _hasListeners = NO;
}

+ (NSArray<NSString *> *)installedFontFamilyName {
    NSMutableArray<NSString *> *allFontFamilyNames = @[].mutableCopy;
    NSArray<NSString *> *systemFonts = [UIFont familyNames];
    [allFontFamilyNames addObjectsFromArray:systemFonts];
    return allFontFamilyNames;
}

+ (NSArray<NSString *> *)installedFontName {
    NSMutableArray<NSString *> *allFontNames = @[].mutableCopy;
    NSArray<NSString *> *systemFonts = [UIFont familyNames];
    for (NSString *familyName in systemFonts) {
        NSArray<NSString *> *fontNames = [UIFont fontNamesForFamilyName:familyName];
        [allFontNames addObjectsFromArray:fontNames];
    }
    
    NSArray<NSString *> *userFonts = (__bridge NSArray *) CTFontManagerCopyAvailableFontFamilyNames();
    for (NSString *familyName in userFonts) {
        NSArray<NSString *> *fontNames = [UIFont fontNamesForFamilyName:familyName];
        [allFontNames addObjectsFromArray:fontNames];
    }
    return allFontNames;
}


- (void)_showFontPicker {
    UIFontPickerViewControllerConfiguration *fontConfig = [UIFontPickerViewControllerConfiguration new];
    fontConfig.includeFaces = NO;
    UIFontPickerViewController *fontPicker = [[UIFontPickerViewController alloc] initWithConfiguration:fontConfig];
    fontPicker.delegate = self;
    [self startObserving];
    [XGET(Router) presentViewController:fontPicker animated:YES completion:nil];
}

#pragma mark - UIFontPickerViewControllerDelegate

- (void)fontPickerViewControllerDidPickFont:(UIFontPickerViewController *)viewController {
    UIFontDescriptor *desc = viewController.selectedFontDescriptor;
    NSString *fontName = [desc postscriptName];
    if (self.hasListeners) {
        [self sendEventWithName:ON_SELECT_FONT_CHANGE body:fontName];
    }
}

- (void)fontPickerViewControllerDidCancel:(UIFontPickerViewController *)viewController {
    [viewController dismissViewControllerAnimated:YES completion:nil];
}


@end

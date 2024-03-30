//
//  MPVViewController.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/25.
//

#import "RootViewController.h"
#import <React/RCTRootView.h>
#import <React/RCTBundleURLProvider.h>


@interface RootViewController ()

@end

@implementation RootViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initReactApp];
}

- (void)initReactApp {
    NSURL *url = [self getBundleURL];
    RCTBridge *bridge = [[RCTBridge alloc] initWithBundleURL:url moduleProvider:nil launchOptions:@{}];
    RCTRootView *rootView = [[RCTRootView alloc] initWithBridge:bridge moduleName:@"iPlayClient" initialProperties:@{}];
    self.view = rootView;
}

- (NSURL *)getBundleURL {
#if DEBUG
  return [[RCTBundleURLProvider sharedSettings] jsBundleURLForBundleRoot:@"index"];
#else
  return [[NSBundle mainBundle] URLForResource:@"main" withExtension:@"jsbundle"];
#endif
}

@end

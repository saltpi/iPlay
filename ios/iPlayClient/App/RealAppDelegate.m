//
//  TestAppDelegate.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/25.
//

#import "RealAppDelegate.h"
#import "Router.h"

@implementation RealAppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    CGRect screenSize = UIScreen.mainScreen.bounds;
    UIWindow *window = [[UIWindow alloc] initWithFrame:screenSize];
    BOOL isPreviewMode = [NSProcessInfo.processInfo.environment[@"XCODE_RUNNING_FOR_PREVIEWS"] isEqual:@"1"];
    Class clazz = NSClassFromString(
        isPreviewMode ? @"PreviewViewController" : @"RootViewController"
    );
    UIViewController *controller = [clazz new];
    UINavigationController *navigation = [[UINavigationController alloc] initWithRootViewController:controller];
    [navigation setEdgesForExtendedLayout:UIRectEdgeAll];
    navigation.navigationBarHidden = YES;
    window.rootViewController = navigation;
    window.backgroundColor = UIColor.clearColor;
    [window makeKeyAndVisible];
    self.window = window;
    XSET(Router, navigation);
    return YES;
}

@end

#import <UIKit/UIKit.h>

#import "AppDelegate.h"
#import "RealAppDelegate.h"

int main(int argc, char *argv[]) {
    @autoreleasepool {
        return UIApplicationMain(argc, argv, nil, NSStringFromClass([RealAppDelegate class]));
    }
}

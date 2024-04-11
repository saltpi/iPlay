//
//  Router.h
//  App
//
//  Created by 赫拉 on 2023/9/21.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@protocol Router <NSObject>
- (void)presentViewController:(UIViewController *)viewControllerToPresent animated: (BOOL)flag completion:(void (^ __nullable)(void))completion NS_SWIFT_DISABLE_ASYNC API_AVAILABLE(ios(5.0));
// The completion handler, if provided, will be invoked after the dismissed controller's viewDidDisappear: callback is invoked.
- (void)dismissViewControllerAnimated: (BOOL)flag completion: (void (^ __nullable)(void))completion NS_SWIFT_DISABLE_ASYNC API_AVAILABLE(ios(5.0));

- (void)pushViewController:(UIViewController *)viewController animated:(BOOL)animated; // Uses a horizontal slide transition. Has no effect if the view controller is already in the stack.

- (nullable UIViewController *)popViewControllerAnimated:(BOOL)animated; // Returns the popped controller.
- (nullable NSArray<__kindof UIViewController *> *)popToViewController:(UIViewController *)viewController animated:(BOOL)animated; // Pops view controllers until the one specified is on top. Returns the popped controllers.
- (nullable NSArray<__kindof UIViewController *> *)popToRootViewControllerAnimated:(BOOL)animated; // Pops until there's only a single view controller left on the stack. Returns the popped controllers.

@property(nullable, nonatomic,readonly,strong) UIViewController *topViewController; // The top view controller on the stack.
@property(nullable, nonatomic,readonly,strong) UIViewController *visibleViewController; // Return modal view controller if it exists. Otherwise the top view controller.

@property(nonatomic,copy) NSArray<__kindof UIViewController *> *viewControllers; // The current view controller stack.

- (void)setViewControllers:(NSArray<UIViewController *> *)viewControllers animated:(BOOL)animated API_AVAILABLE(ios(3.0)); // If animated is YES, then simulate a push or pop depending on whether the new top view controller was previously in the stack.
@end

NS_ASSUME_NONNULL_END

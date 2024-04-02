//
//  BeanProxy.m
//  App
//
//  Created by 赫拉 on 2023/9/24.
//

#import "BeanProxy.h"

@implementation BeanProxy

- (NSMethodSignature *)methodSignatureForSelector:(SEL)sel {
    if (_beans.count) {
        __strong id instance = [_beans pointerAtIndex:0];
        return [instance methodSignatureForSelector:sel];
    }
    return nil;
}

- (void)forwardInvocation:(NSInvocation *)invocation {
    NSInteger count = _beans.count;
    for (int i = 0; i < count; i++) {
        __strong NSObject *instance = (__bridge NSObject *)[_beans pointerAtIndex:i];
        if (![instance respondsToSelector:invocation.selector]) continue;
        [invocation invokeWithTarget:instance];
    }
}

#pragma mark - Public

+ (instancetype)proxy {
    return [BeanProxy alloc];
}

- (NSInteger)count {
    return _beans.count;
}

- (void)appendBean:(id)bean {
    [self.beans addPointer:(__bridge void * _Nullable)(bean)];
}

- (void)removeBean:(id)bean {
    if (_beans.count) {
        NSInteger count = _beans.count;
        NSInteger idx = -1;
        for (int i = 0; i < count; i++) {
            id item = (__bridge id)[_beans pointerAtIndex:i];
            if ([item isEqual:bean]) {
                idx = i;
                break;
            }
        }
        if (idx != -1) {
            [_beans removePointerAtIndex:idx];
        }
        [_beans addPointer:NULL];
        [_beans compact];
    }
}

#pragma mark - Getter
- (NSPointerArray *)beans {
    if (!_beans) {
        _beans = [NSPointerArray weakObjectsPointerArray];
    }
    return _beans;
}

@end

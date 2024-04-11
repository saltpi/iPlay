//
//  BeanContainer.m
//  App
//
//  Created by 赫拉 on 2023/9/21.
//

#import "BeanContainer.h"
#import "BeanProxy.h"

@interface BeanContainer ()
@property (nonatomic, strong) NSMapTable<Protocol *, id> *holder;
@property (nonatomic, strong) NSMapTable<Protocol *, id> *proxys;
@end

@implementation BeanContainer

+ (instancetype)sharedInstance {
    static BeanContainer *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [BeanContainer new];
    });
    return instance;
}

- (void)setBean:(Protocol *)protocol value:(id)value {
    if (!protocol || !value) {
        return;
    }
    __strong id bean = [self.holder objectForKey:protocol];
    if (bean) {
        BeanProxy *proxy = [BeanProxy proxy];
        [proxy appendBean:bean];
        [proxy appendBean:value];
        [self.holder removeObjectForKey:protocol];
        [self.proxys setObject:proxy forKey:protocol];
    } else {
        BeanProxy *proxy = [self.proxys objectForKey:protocol];
        if (proxy) {
            [proxy appendBean:value];
        } else {
            [self.holder setObject:value forKey:protocol];
        }
    }
}

- (id)getBean:(Protocol *)protocol {
    if (!protocol) {
        return nil;
    }
    
    __strong id bean = [self.holder objectForKey:protocol];
    if (bean) {
        return bean;
    } else {
        BeanProxy *proxy = [self.proxys objectForKey:protocol];
        if (proxy) {
            return proxy;
        }
    }
    return nil;
}

- (void)removeBean:(Protocol *)protocol value:(id)value {
    __strong id bean = [self.holder objectForKey:protocol];
    if ([bean isEqual:value]) {
        [self.holder removeObjectForKey:protocol];
    }
    
    BeanProxy *proxy = [self.proxys objectForKey:protocol];
    if (proxy) {
        [proxy removeBean:value];
        if (proxy.count == 0) {
            [self.proxys removeObjectForKey:protocol];
        }
    }
}

- (void)removeAllBeans:(Protocol *)protocol {
    [self.holder removeObjectForKey:protocol];
    [self.proxys removeObjectForKey:protocol];
}

#pragma mark - Getter
- (NSMapTable<Protocol *,id> *)holder {
    if (!_holder) {
        _holder = [NSMapTable strongToWeakObjectsMapTable];
    }
    return _holder;
}

- (NSMapTable<Protocol *,id> *)proxys {
    if (!_proxys) {
        _proxys = [NSMapTable strongToStrongObjectsMapTable];
    }
    return _proxys;
}
@end

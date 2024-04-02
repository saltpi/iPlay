//
//  BeanProxy.h
//  App
//
//  Created by 赫拉 on 2023/9/24.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface BeanProxy : NSProxy
@property (nonatomic, strong) NSPointerArray *beans;

+ (instancetype)proxy;
- (void)appendBean:(id)bean;
- (void)removeBean:(id)bean;
- (NSInteger)count;
@end

NS_ASSUME_NONNULL_END

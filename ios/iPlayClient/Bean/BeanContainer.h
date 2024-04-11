//
//  BeanContainer.h
//  App
//
//  Created by 赫拉 on 2023/9/21.
//

#import <Foundation/Foundation.h>


#define LINK(protocol) ([BeanContainer.sharedInstance getBean:protocol])
#define BIND(protocol, bean) ([BeanContainer.sharedInstance setBean:protocol value:bean])
#define XGET(interface) ((id<interface>)[BeanContainer.sharedInstance getBean:@protocol(interface)])
#define XSET(interface, bean) ([BeanContainer.sharedInstance setBean:@protocol(interface) value:bean])
#define XNIL(interface) ([BeanContainer.sharedInstance removeAllBeans:@protocol(interface)])

NS_ASSUME_NONNULL_BEGIN

@interface BeanContainer : NSObject
+ (instancetype)sharedInstance;
- (id _Nullable)getBean:(Protocol *)protocol;
- (void)setBean:(Protocol *)protocol value:(id)value;
- (void)removeBean:(Protocol *)protocol value:(id)value;
- (void)removeAllBeans:(Protocol *)protocol;
@end

NS_ASSUME_NONNULL_END

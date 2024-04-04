//
//  IPLUIModule.h
//  iPlayClient
//
//  Created by 赫拉 on 2024/4/4.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>

NS_ASSUME_NONNULL_BEGIN

@interface IPLUIModule : NSObject<RCTBridgeModule>
+ (CGSize)windowSize;
@end

NS_ASSUME_NONNULL_END

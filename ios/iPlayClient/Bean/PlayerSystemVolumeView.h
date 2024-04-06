//
//  PlayerSystemVolumeView.h
//  iPlayClient
//
//  Created by 赫拉 on 2024/4/6.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@protocol PlayerSystemVolumeView <NSObject>
- (UISlider *)volumeSlider;
@end

NS_ASSUME_NONNULL_END

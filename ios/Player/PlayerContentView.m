//
//  MPVView.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/25.
//

#import "PlayerContentView.h"

@implementation PlayerContentView

+ (Class)layerClass {
    return [CAMetalLayer class];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.layer.contentsGravity = kCAGravityResizeAspect;
}


- (PlayerViewModel *)viewModel {
    if (!_viewModel) {
        CAMetalLayer *layer = (CAMetalLayer *)self.layer;
        layer.contentsScale = UIScreen.mainScreen.nativeScale;
        layer.framebufferOnly = YES;
        if (@available(iOS 16.0, *)) {
            layer.wantsExtendedDynamicRangeContent = YES;
        } else {
            // Fallback on earlier versions
        }
        _viewModel = [[PlayerViewModel alloc] initWithLayer:layer];
    }
    return _viewModel;
}
@end



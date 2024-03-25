//
//  MPVView.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/25.
//

#import "MPVView.h"
@import MPVKit;

@interface MPVView ()
@property (nonatomic) CAMetalLayer *videoLayer;
@end

@implementation MPVView

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self _setupUI];
    }
    return self;
}

- (void)_setupUI {
    [self.layer addSublayer:self.videoLayer];
    self.videoLayer.frame = self.frame;
}


- (void)layoutSubviews {
    [super layoutSubviews];
    self.videoLayer.frame = self.frame;
}

- (CAMetalLayer *)videoLayer {
    if (!_videoLayer) {
        _videoLayer = [CAMetalLayer new];
        _videoLayer.contentsScale = UIScreen.mainScreen.nativeScale;
        _videoLayer.framebufferOnly = true;
        if (@available(iOS 16.0, *)) {
            _videoLayer.wantsExtendedDynamicRangeContent = true;
        } else {
            
        }
    }
    return _videoLayer;
}

- (MPVViewModel *)viewModel {
    if (!_viewModel) {
        _viewModel = [[MPVViewModel alloc] initWithLayer:self.videoLayer];
    }
    return _viewModel;
}
@end



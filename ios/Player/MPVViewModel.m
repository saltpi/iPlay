//
//  MPVViewModel.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/25.
//

#import "MPVViewModel.h"
@import MPVKit;

@interface MPVViewModel ()
//@property (nonatomic) mpv_handle *mpv;
@end

@implementation MPVViewModel

- (instancetype)initWithLayer:(CAMetalLayer *)layer {
    self = [self init];
    if (self) {
        self.drawable = layer;
    }
    return self;
}

- (void)setDrawable:(id)view {
    if ([view isKindOfClass:CAMetalLayer.class]) {
        mpv_handle *mpv = mpv_create();
        mpv_request_log_messages(mpv, "debug");
        mpv_set_option(mpv, "wid", MPV_FORMAT_INT64, &view);
        mpv_set_option_string(mpv, "subs-match-os-language", "yes");
        mpv_set_option_string(mpv, "subs-fallback", "yes");
        mpv_set_option_string(mpv, "vo", "gpu-next");
        mpv_set_option_string(mpv, "gpu-api", "vulkan");
        mpv_set_option_string(mpv, "hwdec", "videotoolbox");
        mpv_initialize(mpv);
        self.mpv = mpv;
    } else {
        NSLog(@"view is not kind of CAMetalLayer");
    }
}

- (void)loadVideo:(NSString *)url {
    const char *cmd[] = {"loadfile", [url cStringUsingEncoding:NSUTF8StringEncoding], "replace", NULL};
    mpv_command(self.mpv, cmd);
}

- (void)play {
    const char *cmd[] = {"play", NULL};
    mpv_command(self.mpv, cmd);
}

- (void)stop {
    
}

- (void)pause {
    const char *cmd[] = {"pause", NULL};
    mpv_command(self.mpv, cmd);
}

- (void)dealloc {
    mpv_terminate_destroy(self.mpv);
}

@end

//
//  MPVViewModel.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/25.
//

#import "MPVViewModel.h"
@import MPVKit;

void on_progress_update(mpv_handle *mpv, double time, id<VideoPlayer> context) {
    [context.delegate onPlayEvent:PlayEventTypeOnProgress data:@{
        @"time": @(time)
    }];
}

void on_duration_update(mpv_handle *mpv, double time, id<VideoPlayer> context) {
    ((MPVViewModel *)context).duration = time;
    [context.delegate onPlayEvent:PlayEventTypeDuration data:@{
        @"duration": @(time)
    }];
}

void on_mpv_event(mpv_handle *mpv, mpv_event *event, id context) {
    if (event->event_id == MPV_EVENT_PROPERTY_CHANGE) {
        mpv_event_property *prop = event->data;
        if (strcmp(prop->name, "time-pos") == 0) {
            if (prop->format == MPV_FORMAT_DOUBLE) {
                on_progress_update(mpv, *(double *)prop->data, context);
            }
        } else if (strcmp(prop->name, "duration") == 0) {
            if (prop->format == MPV_FORMAT_DOUBLE) {
                on_duration_update(mpv, *(double *)prop->data, context);
            }
        }
    }
}


@interface MPVViewModel ()
@property (nonatomic) dispatch_queue_t queue;
@property (nonatomic, weak) id<VideoPlayerDelegate> delegate;
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
    mpv_observe_property(self.mpv, 0, "time-pos", MPV_FORMAT_DOUBLE);
    mpv_observe_property(self.mpv, 0, "duration", MPV_FORMAT_DOUBLE);
    dispatch_async(self.queue, ^{
        while (1) {
            mpv_event *event = mpv_wait_event(self.mpv, -1);
            if (event->event_id == MPV_EVENT_SHUTDOWN)
                break;
            on_mpv_event(self.mpv, event, self);
        }
    });
}

- (void)stop {
    const char *cmd[] = {"stop", NULL};
    mpv_command(self.mpv, cmd);
}

- (void)seek:(NSUInteger)timeSeconds {
//    const char* pos = [@(timeSeconds).stringValue cStringUsingEncoding:NSUTF8StringEncoding];
//    const char *cmd[] = {"seek", pos, "absolute"};
//    mpv_command(self.mpv, cmd);
}

- (void)pause {
    const char *cmd[] = {"pause", NULL};
    mpv_command(self.mpv, cmd);
    
    char *filename = NULL;
    double duration = 0;
    int64_t width = 0, height = 0;
    mpv_get_property(self.mpv, "path", MPV_FORMAT_STRING, &filename);
    mpv_get_property(self.mpv, "duration", MPV_FORMAT_DOUBLE, &duration);
    mpv_get_property(self.mpv, "width", MPV_FORMAT_INT64, &width);
    mpv_get_property(self.mpv, "height", MPV_FORMAT_INT64, &height);
    printf("Filename: %s\n", filename);
    printf("Duration: %.2f seconds\n", duration);
    printf("Resolution: %lldx%lld\n", width, height);
}


- (void)dealloc {
    mpv_terminate_destroy(self.mpv);
}

- (dispatch_queue_t)queue {
    if (!_queue) {
        _queue = dispatch_queue_create("mpv-player-queue", NULL);
    }
    return _queue;
}

@end

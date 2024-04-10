package top.ourfor.app.iPlayClient.view;

import top.ourfor.lib.mpv.MPV;

public enum PlayerPropertyType {
    None,
    TimePos,
    Duration,
    PausedForCache,
    Pause,
    TrackList,
    DemuxerCacheState,
    EofReached
}

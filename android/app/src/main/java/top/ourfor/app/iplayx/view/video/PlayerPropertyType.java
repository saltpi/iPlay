package top.ourfor.app.iplayx.view.video;

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

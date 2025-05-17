package top.ourfor.app.iplay.view.player;

public class PlayerEventType {
    public enum PlayEventType {
        PlayEventTypeOnProgress(0),
        PlayEventTypeOnPause(1),
        PlayEventTypeOnPauseForCache(2),
        PlayEventTypeDuration(3),
        PlayEventTypeEnd(4),
        PlayEventTypeDemuxerCacheState(5);

        public int value;

        PlayEventType(int value) {
            this.value = value;
        }

        public static PlayEventType fromInt(int i) {
            for (PlayEventType type : PlayEventType.values()) {
                if (type.value == i) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid value for PlayEventType: " + i);
        }
    }
}

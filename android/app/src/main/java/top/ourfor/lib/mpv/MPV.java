package top.ourfor.lib.mpv;

import android.view.Surface;

import java.util.List;


public class MPV {
    // native mpv c pointer
    private long holder;
    public SeekableRange[] cachedRanges;
    public int cachedRangeCount;

    static {
        String[] libs = {"mpv", "player"};
        for (String lib : libs) {
            System.loadLibrary(lib);
        }
    }

    public MPV() {
    }

    public native void create();

    public native void init();

    public native void destroy();

    public native void setDrawable(Surface surface);

    public native void command(String... cmd);

    public native int setOptionString(String name, String value);
    public native boolean getBoolProperty(String key);
    public native int setBoolProperty(String key, boolean flag);
    public native long getLongProperty(String key);
    public native int setLongProperty(String key, long value);
    public native double getDoubleProperty(String key);
    public native String getStringProperty(String key);
    public native int setDoubleProperty(String key, double value);

    public native int setStringProperty(String key, String value);
    public native SeekableRange[] seekableRanges(long pointer);

    public native int observeProperty(long reply_userdata, String name, int format);
    public native Event waitEvent(double timeout);


    public class Event {
        public int type;
        public String prop;
        public int format;
        // reply_userdata
        public int reply;
        public long data;
    }

    public static int MPV_FORMAT_NONE             = 0;
    public static int MPV_FORMAT_STRING           = 1;
    public static int MPV_FORMAT_OSD_STRING       = 2;
    public static int MPV_FORMAT_FLAG             = 3;
    public static int MPV_FORMAT_INT64            = 4;
    public static int MPV_FORMAT_DOUBLE           = 5;
    public static int MPV_FORMAT_NODE             = 6;
    public static int MPV_FORMAT_NODE_ARRAY       = 7;
    public static int MPV_FORMAT_NODE_MAP         = 8;
    public static int MPV_FORMAT_BYTE_ARRAY       = 9;

    public static int MPV_EVENT_NONE              = 0;
    public static int  MPV_EVENT_SHUTDOWN          = 1;
    public static int MPV_EVENT_LOG_MESSAGE       = 2;
    public static int MPV_EVENT_GET_PROPERTY_REPLY = 3;
    public static int MPV_EVENT_SET_PROPERTY_REPLY = 4;
    public static int MPV_EVENT_COMMAND_REPLY     = 5;
    public static int MPV_EVENT_START_FILE        = 6;
    public static int MPV_EVENT_END_FILE          = 7;
    public static int MPV_EVENT_FILE_LOADED       = 8;
    public static int MPV_EVENT_TICK              = 14;
    public static int MPV_EVENT_CLIENT_MESSAGE    = 16;
    public static int MPV_EVENT_VIDEO_RECONFIG    = 17;
    public static int MPV_EVENT_AUDIO_RECONFIG    = 18;
    public static int MPV_EVENT_SEEK              = 20;
    public static int MPV_EVENT_PLAYBACK_RESTART  = 21;
    public static int MPV_EVENT_PROPERTY_CHANGE   = 22;
    public static int MPV_EVENT_QUEUE_OVERFLOW    = 24;
    public static int MPV_EVENT_HOOK              = 25;
}

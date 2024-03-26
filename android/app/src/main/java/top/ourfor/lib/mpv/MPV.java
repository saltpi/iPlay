package top.ourfor.lib.mpv;

import android.content.Context;
import android.view.Surface;

public class MPV {
    private Long id;

    static {
        String[] libs = {"mpv", "player"};
        for (String lib : libs) {
            System.loadLibrary(lib);
        }
    }

    public static native void create(Context appctx);

    public static native void init();

    public static native void destroy();

    public static native void setDrawable(Surface surface);

    public static native void command(String... cmd);

    public static native int setOptionString(String name, String value);
}

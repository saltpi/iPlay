package top.ourfor.lib.mpv;

import android.view.Surface;


public class MPV {
    // native mpv c pointer
    private long holder;

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
}

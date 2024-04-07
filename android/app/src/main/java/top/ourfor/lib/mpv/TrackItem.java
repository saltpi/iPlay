package top.ourfor.lib.mpv;

import androidx.annotation.NonNull;

public class TrackItem {
    public String lang;
    public String type;
    public String title;
    public int id;

    public static String VideoTrackName = "video";
    public static String AudioTrackName = "audio";
    public static String SubtitleTrackName = "sub";

    @NonNull
    @Override
    public String toString() {
        return lang + " " + title;
    }
}

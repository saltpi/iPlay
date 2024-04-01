package top.ourfor.app.iPlayClient;

import static top.ourfor.lib.mpv.MPV.MPV_EVENT_PROPERTY_CHANGE;
import static top.ourfor.lib.mpv.MPV.MPV_EVENT_SHUTDOWN;
import static top.ourfor.lib.mpv.MPV.MPV_FORMAT_DOUBLE;
import static top.ourfor.lib.mpv.MPV.MPV_FORMAT_FLAG;
import static top.ourfor.lib.mpv.TrackItem.SubtitleTrackName;

import android.util.Log;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.List;

import top.ourfor.lib.mpv.MPV;
import top.ourfor.lib.mpv.TrackItem;

public class PlayerViewModel implements Player {
    public PlayerEventListener delegate;
    public Thread eventLoop;
    public double _duration;

    public String url = null;
    private MPV mpv;
    public PlayerViewModel(String configDir, String cacheDir) {
        mpv = new MPV();
        mpv.create();
//        mpv.setOptionString("profile", "fast");
        mpv.setOptionString("vo", "gpu");
        mpv.setOptionString("gpu-context", "android");
        mpv.setOptionString("opengl-es", "yes");
        mpv.setOptionString("hwdec", "auto");
        mpv.setOptionString("hwdec-codecs", "h264,hevc,mpeg4,mpeg2video,vp8,vp9,av1");
        mpv.setOptionString("ao", "audiotrack,opensles");
        mpv.setOptionString("config", "yes");
        mpv.setOptionString("force-window", "no");
        mpv.setOptionString("config-dir", configDir);
        mpv.setOptionString("gpu-shader-cache-dir", cacheDir);
        mpv.setOptionString("icc-cache-dir", cacheDir);
        mpv.setOptionString("track-auto-selection", "yes");
        mpv.setOptionString("slang", "zh,chi,chs,sc,zh-hans,en,eng");
        mpv.setOptionString("subs-match-os-language", "yes");
        mpv.setOptionString("subs-fallback", "yes");
        mpv.init();

        watch();
    }

    @Override
    public Double duration() {
        return _duration;
    }

    @Override
    public void setDelegate(PlayerEventListener delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setVideoOutput(String value) {
        mpv.setStringProperty("vo", value);
    }

    public void attach(SurfaceHolder holder) {
        mpv.setDrawable(holder.getSurface());
//        mpv.setOptionString("force-window", "yes");
    }

    @Override
    public void detach() {
        mpv.setStringProperty("vo", "null");
//        mpv.setOptionString("force-window", "no");
        mpv.setDrawable(null);
    }

    @Override
    public void loadVideo(String url) {
        mpv.command("loadfile", url);
    }

    @Override
    public void resize(String newSize) {
        mpv.setStringProperty("android-surface-size", newSize);
    }

    @Override
    public List<TrackItem> subtitles() {
        // video/audio/sub
        Log.d(TAG, "obtain track list");
        Long subtitleCount = mpv.getLongProperty("track-list/count");
        ArrayList<TrackItem> trackItems = new ArrayList<>();
        for (long i = 0; i < subtitleCount; i++) {
            String type = mpv.getStringProperty(String.format("track-list/%d/type", i));
            if (!type.equals(SubtitleTrackName)) continue;
            Long id = mpv.getLongProperty(String.format("track-list/%d/id", i));
            String lang = mpv.getStringProperty(String.format("track-list/%d/lang", i));
            String title = mpv.getStringProperty(String.format("track-list/%d/title", i));
            Log.d(TAG, "id: " + id + "\ntype: " + type + "\nlang: " + lang + "\ntitle: " + title);
            TrackItem trackItem = new TrackItem();
            trackItem.id = Math.toIntExact(id);
            trackItem.type = SubtitleTrackName;
            trackItem.title = title;
            trackItem.lang = lang;
            trackItems.add(trackItem);
        }
        return trackItems;
    }

    @Override
    public void useSubtitle(int id) {
        Log.d(TAG, "use subtitle " + id);
//        mpv.setOptionString("sid", String.valueOf(id));
    }

    @Override
    public void seek(long timeInSeconds) {
        mpv.command("seek", String.valueOf(timeInSeconds), "absolute+keyframes");
    }

    @Override
    public boolean isPlaying() {
        return !(mpv.getBoolProperty("pause"));
    }

    @Override
    public void resume() {
        mpv.setBoolProperty("pause", false);
    }

    @Override
    public void pause() {
        mpv.setBoolProperty("pause", true);
    }

    @Override
    public void stop() {
        mpv.command("stop");
    }

    @Override
    public void destroy() {
        mpv.command("stop");
        mpv.command("quit");
    }

    public void watch() {
        if (eventLoop ==  null) {
            mpv.observeProperty(0, "time-pos", MPV.MPV_FORMAT_DOUBLE);
            mpv.observeProperty(0, "duration", MPV.MPV_FORMAT_DOUBLE);
            mpv.observeProperty(0, "paused-for-cache", MPV.MPV_FORMAT_FLAG);
            mpv.observeProperty(0, "pause", MPV.MPV_FORMAT_FLAG);
            mpv.observeProperty(0, "track-list", MPV.MPV_FORMAT_NONE);
            eventLoop = new Thread(() -> {
                while (true) {
                    MPV.Event e = mpv.waitEvent(-1);
                    if (e == null) {
                        Log.d(TAG, "event is null, close mpv player");
                        break;
                    }
                    if (e.type == MPV_EVENT_SHUTDOWN) {
                        Log.d(TAG, "close mpv player");
                        if (mpv != null) mpv.destroy();
                        mpv = null;
                        break;
                    }

                    if (e.type == MPV_EVENT_PROPERTY_CHANGE) {
                        if (delegate == null) return;
                        Object value = null;
                        if (e.format == MPV_FORMAT_DOUBLE) {
                            value = mpv.getDoubleProperty(e.prop);
                        } else if (e.format == MPV_FORMAT_FLAG) {
                            value = mpv.getBoolProperty(e.prop);
                        }
                        delegate.onPropertyChange(e.prop, value);
                    }
                }
            });
        }
        eventLoop.start();
    }

    static String TAG = "PlayerViewModel";
}

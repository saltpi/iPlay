package top.ourfor.app.iplayx.view.video;

import static top.ourfor.app.iplayx.module.Bean.XGET;
import static top.ourfor.app.iplayx.view.video.PlayerPropertyType.DemuxerCacheState;
import static top.ourfor.app.iplayx.view.video.PlayerPropertyType.Duration;
import static top.ourfor.app.iplayx.view.video.PlayerPropertyType.EofReached;
import static top.ourfor.app.iplayx.view.video.PlayerPropertyType.Pause;
import static top.ourfor.app.iplayx.view.video.PlayerPropertyType.PausedForCache;
import static top.ourfor.app.iplayx.view.video.PlayerPropertyType.TimePos;
import static top.ourfor.app.iplayx.view.video.PlayerPropertyType.TrackList;
import static top.ourfor.lib.mpv.MPV.MPV_EVENT_PROPERTY_CHANGE;
import static top.ourfor.lib.mpv.MPV.MPV_EVENT_SHUTDOWN;
import static top.ourfor.lib.mpv.MPV.MPV_FORMAT_FLAG;
import static top.ourfor.lib.mpv.MPV.MPV_FORMAT_NODE;
import static top.ourfor.lib.mpv.TrackItem.AudioTrackName;
import static top.ourfor.lib.mpv.TrackItem.SubtitleTrackName;

import android.net.Uri;
import android.view.SurfaceHolder;
import android.webkit.MimeTypeMap;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplayx.bean.JSONAdapter;
import top.ourfor.app.iplayx.util.HTTPUtil;
import top.ourfor.app.iplayx.util.PathUtil;
import top.ourfor.app.iplayx.view.player.Player;
import top.ourfor.lib.mpv.MPV;
import top.ourfor.lib.mpv.TrackItem;

@Slf4j
public class MPVPlayerViewModel implements Player {
    public PlayerEventListener delegate;
    public String subtitleFontName;
    public String subtitleFontDirectory;
    public String cachePath;
    public Thread eventLoop;
    public double _duration;
    private Map<String, String> option = null;

    public String url = null;
    private MPV mpv;
    public MPVPlayerViewModel(String configDir, String cacheDir, String fontDir) {
        mpv = new MPV();
        mpv.create();
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
        mpv.setOptionString("keep-open", "yes");
        mpv.setOptionString("slang", "zh,chi,chs,sc,zh-hans,en,eng");
        mpv.setOptionString("subs-match-os-language", "yes");
        mpv.setOptionString("subs-fallback", "yes");
        mpv.setOptionString("user-agent", "iPlay");
        if (option != null) applyOption(option);
        subtitleFontDirectory = fontDir;
        setSubtitleFontDirectory(fontDir);
        setSubtitleFontName(subtitleFontName);
        mpv.init();

        watch();
    }

    @Override
    public void applyOption(Map<String, String> option) {
        this.option = option;
        if (mpv == null) return;
        var keys = option.keySet();
        for (var key : keys) {
            var value = option.get(key);
            mpv.setOptionString(key, value);
        }
    }

    @Override
    public void setCachePath(String cachePath) {
        this.cachePath = cachePath;
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
        if (mpv == null) return;
        mpv.setStringProperty("vo", value);
    }

    public void attach(SurfaceHolder holder) {
        mpv.setDrawable(holder.getSurface());
//        mpv.setOptionString("force-window", "yes");
    }

    @Override
    public void detach() {
        if (mpv == null) return;
        mpv.setStringProperty("vo", "null");
//        mpv.setOptionString("force-window", "no");
        mpv.setDrawable(null);
    }

    @Override
    public void loadVideo(String url) {
        if (mpv == null) return;
        if (url.startsWith("iplay://play")) {
            val uri = Uri.parse(url);
            val source = uri.getQueryParameter("source");
            if (source != null) {
                url = source;
            }
            val option = uri.getQueryParameter("option");
            if (option != null) {
                val map = XGET(JSONAdapter.class).fromJSON(option, Map.class);
                applyOption(map);
            }
        }
        mpv.command("loadfile", url);
    }

    @Override
    public void loadVideo(String url, String audioUrl) {
        if (mpv == null) return;
        if (url.startsWith("iplay://play")) {
            val uri = Uri.parse(url);
            val source = uri.getQueryParameter("source");
            if (source != null) {
                url = source;
            }
            val option = uri.getQueryParameter("option");
            if (option != null) {
                val map = XGET(JSONAdapter.class).fromJSON(option, Map.class);
                applyOption(map);
            }
        }
        mpv.command("loadfile", url, "replace", "0", "audio-file=\"" + audioUrl + "\"");
    }

    @Override
    public void resize(String newSize) {
        if (mpv == null) return;
        mpv.setStringProperty("android-surface-size", newSize);
    }

    public List<TrackItem> trackList(String trackType) {
        log.debug("obtain track list");
        Long subtitleCount = mpv.getLongProperty("track-list/count");
        ArrayList<TrackItem> trackItems = new ArrayList<>();
        for (long i = 0; i < subtitleCount; i++) {
            String type = mpv.getStringProperty(String.format("track-list/%d/type", i));
            if (!type.equals(trackType)) continue;
            Long id = mpv.getLongProperty(String.format("track-list/%d/id", i));
            String lang = mpv.getStringProperty(String.format("track-list/%d/lang", i));
            String title = mpv.getStringProperty(String.format("track-list/%d/title", i));
            log.debug("id: " + id + "\ntype: " + type + "\nlang: " + lang + "\ntitle: " + title);
            TrackItem trackItem = new TrackItem();
            trackItem.id = String.valueOf(id);
            trackItem.type = type;
            trackItem.title = title;
            trackItem.lang = lang;
            trackItems.add(trackItem);
        }
        return trackItems;
    }

    @Override
    public List<TrackItem> subtitles() {
        // video/audio/sub
        return trackList(SubtitleTrackName);
    }

    @SneakyThrows
    @Override
    public void loadSubtitle(List<PlayerSourceModel> subtitles) {
        if (mpv == null) return;
        for (var subtitle : subtitles) {
            URL url = new URL(subtitle.getUrl());
            String path = url.getPath();
            String name = subtitle.getName();
            String lang = subtitle.getValue();
            if (name == null) {
                name = "未知";
            } else {
                name = subtitle.getName();
            }
            if (lang == null) {
                lang = "未知";
            }
            String extension = MimeTypeMap.getFileExtensionFromUrl(path);
            String filePath = PathUtil.of(cachePath, UUID.randomUUID().toString() + "." + extension);
            String finalLang = lang;
            String finalName = name;
            HTTPUtil.download(subtitle.getUrl(), filePath, (subtitlePath) -> {
                if (path == null || mpv == null) return;
                mpv.command("sub-add", subtitlePath, "auto", finalName, finalLang);
            });
        }
    }

    @Override
    public List audios() {
        return trackList(AudioTrackName);
    }

    @Override
    public String currentAudioId() {
        if (mpv == null) return "no";
        return mpv.getStringProperty("aid");
    }

    @Override
    public String currentSubtitleId() {
        if (mpv == null) return "no";
        return mpv.getStringProperty("sid");
    }

    @Override
    public void useSubtitle(String id) {
        log.debug("use subtitle " + id);
        mpv.setOptionString("sid", String.valueOf(id));
    }

    @Override
    public void useAudio(String id) {
        log.debug( "use audio " + id);
        mpv.setOptionString("aid", String.valueOf(id));
    }

    @Override
    public void useVideo(String id) {
        if (mpv == null) return;
        mpv.command("loadfile", id);
    }

    @Override
    public void setLastWatchPosition(long lastWatchPosition) {
        if (mpv == null) return;
        mpv.setOptionString("start", String.valueOf(lastWatchPosition));
    }

    @Override
    public void speed(float speed) {
        if (mpv == null) return;
        mpv.setOptionString("speed", String.valueOf(speed));
    }

    @Override
    public void setSubtitleFontName(String subtitleFontName) {
        this.subtitleFontName = subtitleFontName;
        if (subtitleFontName == null) return;
        mpv.setOptionString("sub-font", subtitleFontName);
        log.debug( "use sub font " + subtitleFontName);
    }

    @Override
    public void setSubtitleFontDirectory(String directory) {
        this.subtitleFontDirectory = directory;
        if (subtitleFontDirectory == null) return;
        mpv.setOptionString("sub-fonts-dir", subtitleFontDirectory);
        log.debug("use sub font dir " + subtitleFontDirectory);
    }

    @Override
    public void seek(long timeInSeconds) {
        if (mpv == null) return;
        mpv.command("seek", String.valueOf(timeInSeconds), "absolute+keyframes");
    }

    @Override
    public boolean isPlaying() {
        if (mpv == null) return false;
        return !(mpv.getBoolProperty("pause"));
    }

    @Override
    public void resume() {
        if (mpv == null) return;
        mpv.setBoolProperty("pause", false);
    }

    @Override
    public void pause() {
        if (mpv == null) return;
        mpv.setBoolProperty("pause", true);
    }

    @Override
    public void stop() {
        if (mpv == null) return;
        mpv.command("stop");
    }

    @Override
    public void speedUp(float speed) {
        if (mpv == null) return;
        mpv.setOptionString("speed", String.valueOf(speed));
    }

    public void speedDown(float speed) {
        if (mpv == null) return;
        mpv.setOptionString("speed", String.valueOf(speed));
    }

    @Override
    public void jumpBackward(int seconds) {
        seekRelative(-seconds);
    }

    @Override
    public void jumpForward(int seconds) {
        seekRelative(seconds);
    }

    public void seekRelative(int seconds) {
        if (mpv == null) return;
        mpv.command("seek", String.valueOf(seconds), "relative+exact");
    }

    @Override
    public void destroy() {
        if (mpv == null) return;
        mpv.command("stop");
        mpv.command("quit");
    }

    public void watch() {
        if (eventLoop ==  null) {
            mpv.observeProperty(TimePos.ordinal(), "time-pos", MPV.MPV_FORMAT_DOUBLE);
            mpv.observeProperty(Duration.ordinal(), "duration", MPV.MPV_FORMAT_DOUBLE);
            mpv.observeProperty(PausedForCache.ordinal(), "paused-for-cache", MPV.MPV_FORMAT_FLAG);
            mpv.observeProperty(Pause.ordinal(), "pause", MPV.MPV_FORMAT_FLAG);
            mpv.observeProperty(TrackList.ordinal(), "track-list", MPV.MPV_FORMAT_NONE);
            mpv.observeProperty(DemuxerCacheState.ordinal(), "demuxer-cache-state", MPV_FORMAT_NODE);
            mpv.observeProperty(EofReached.ordinal(), "eof-reached", MPV_FORMAT_FLAG);
            eventLoop = new Thread(() -> {
                while (true) {
                    MPV.Event e = mpv.waitEvent(-1);
                    if (e == null) {
                        log.info("event is null");
                        break;
                    }

                    if (e.type == MPV_EVENT_SHUTDOWN) {
                        log.info("destroy mpv player");
                        if (mpv != null) {
                            mpv.destroy();
                            mpv = null;
                        }
                        break;
                    }

                    val reply = PlayerPropertyType.values()[e.reply];
                    if (e.type != MPV_EVENT_PROPERTY_CHANGE ||
                        delegate == null) {
                        continue;
                    }

                    Object value = null;
                    switch (reply) {
                        case Pause, PausedForCache, EofReached: {
                            value = mpv.getBoolProperty(e.prop);
                            break;
                        }
                        case Duration, TimePos: {
                            value = mpv.getDoubleProperty(e.prop);
                            break;
                        }
                        case DemuxerCacheState: {
                            value = mpv.seekableRanges(e.data);
                            break;
                        }
                        case TrackList: {
                            break;
                        }
                    }
                    delegate.onPropertyChange(reply, value);
                }
            });
        }
        eventLoop.start();
    }
}

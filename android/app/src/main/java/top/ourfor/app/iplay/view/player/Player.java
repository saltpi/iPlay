package top.ourfor.app.iplay.view.player;

import android.view.SurfaceHolder;

import java.util.List;
import java.util.Map;

import top.ourfor.app.iplay.view.video.PlayerEventListener;
import top.ourfor.app.iplay.view.video.PlayerSourceModel;
import top.ourfor.lib.mpv.TrackItem;

public interface Player {
    default void setDelegate(PlayerEventListener delegate) {}

    default void setVideoOutput(String value) {}
    default void attach(SurfaceHolder holder) {}
    default void detach() {}
    default Double progress() { return 0.0; }
    default Double duration() { return 0.0; }
    boolean isPlaying();
    default void loadVideo(String url) {}
    default void loadVideo(String url, String audioUrl) {}
    default void setCachePath(String path) {}
    default void play() {}
    default void resume() {}
    default void pause() {}
    default void seek(long timeInSeconds) {}
    default void jumpBackward(int seconds) {}
    default void jumpForward(int seconds) {}
    default void stop() {}
    default void resize(String newSize) {}
    default List<TrackItem> audios() { return null; }
    default List<TrackItem> subtitles() { return null; }
    default void loadSubtitle(List<PlayerSourceModel> subtitles) {}
    default String currentSubtitleId() { return "no"; }
    default String currentAudioId() { return "no"; }
    default void setSubtitleFontName(String subtitleFontName) {}
    default void setSubtitleFontDirectory(String directory) {}
    default void setSubtitleDelay(double delay) {}
    default void setSubtitlePosition(double position) {}
    default void destroy() {}

    default void useSubtitle(String id) {};
    default void useAudio(String id) {};
    default void useVideo(String id) {};

    default void applyOption(Map<String, String> option) {};

    default void speedUp(float speed) {}
    default void speedDown(float speed) {}


    default void setLastWatchPosition(long lastWatchPosition) {};

    default void speed(float speed) {};


}

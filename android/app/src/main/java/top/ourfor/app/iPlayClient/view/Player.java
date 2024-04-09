package top.ourfor.app.iPlayClient.view;

import android.view.SurfaceHolder;

import java.util.List;

public interface Player {
    default void setDelegate(PlayerEventListener delegate) {}

    default void setVideoOutput(String value) {}
    default void attach(SurfaceHolder holder) {}
    default void detach() {}
    default Double progress() { return 0.0; }
    default Double duration() { return 0.0; }
    boolean isPlaying();
    default void loadVideo(String url) {}
    default void play() {}
    default void resume() {}
    default void pause() {}
    default void seek(long timeInSeconds) {}
    default void jumpBackward(int seconds) {}
    default void jumpForward(int seconds) {}
    default void stop() {}
    default void resize(String newSize) {}
    default List audios() { return null; }
    default List subtitles() { return null; }
    default String currentSubtitleId() { return "no"; }
    default String currentAudioId() { return "no"; }
    default void setSubtitleFontName(String subtitleFontName) {}
    default void setSubtitleFontDirectory(String directory) {}
    default void destroy() {}

    void useSubtitle(int id);
    void useAudio(int id);


    enum PlayEventType {
        PlayEventTypeOnProgress(0),
        PlayEventTypeOnPause(1),
        PlayEventTypeOnPauseForCache(2),
        PlayEventTypeDuration(3),
        PlayEventTypeEnd(4),
        PlayEventTypeDemuxerCacheState(5);

        int value;

        PlayEventType(int value) {
            this.value = value;
        }
    }
}

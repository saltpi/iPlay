package top.ourfor.app.iPlayClient;

import android.view.SurfaceHolder;

public interface Player {
    default void setDelegate(PlayerEventListener delegate) {}

    default void setVideoOutput(String value) {}
    default void attach(SurfaceHolder holder) {}
    default void detach() {}
    boolean isPlaying();
    default void loadVideo(String url) {}
    default void play() {}
    default void resume() {}
    default void pause() {}
    default void seek(long timeInSeconds) {}
    default void stop() {}
    default void resize(String newSize) {}
    default void destroy() {}


    enum PlayEventType {
        PlayEventTypeOnProgress(0),
        PlayEventTypeOnPause(1),
        PlayEventTypeOnPauseForCache(2),
        PlayEventTypeDuration(3),
        PlayEventTypeEnd(4);

        int value;

        PlayEventType(int value) {
            this.value = value;
        }
    }
}

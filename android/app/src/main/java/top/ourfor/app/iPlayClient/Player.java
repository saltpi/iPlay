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
}

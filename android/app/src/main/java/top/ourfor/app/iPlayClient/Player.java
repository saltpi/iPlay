package top.ourfor.app.iPlayClient;

import android.view.SurfaceHolder;

public interface Player {

    default void attach(SurfaceHolder holder) {}
    boolean isPlaying();
    default void loadVideo(String url) {}
    default void play() {}
    default void resume() {}
    default void pause() {}
    default void seek(double timeInSeconds) {}
    default void stop() {}
    default void resize(String newSize) {}
    default void destroy() {}
}

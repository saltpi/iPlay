package top.ourfor.app.iplay.view.video;

import android.view.View;

public interface PlayerEventDelegate {
    default void onEvent(PlayerGestureType type, Object value) { }

    default View getContentView() {
        return null;
    }
}

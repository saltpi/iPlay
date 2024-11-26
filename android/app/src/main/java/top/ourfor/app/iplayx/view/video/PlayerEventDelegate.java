package top.ourfor.app.iplayx.view.video;

import android.view.View;

public interface PlayerEventDelegate {
    default void onEvent(PlayerGestureType type, Object value) { }

    default View getContentView() {
        return null;
    }
}

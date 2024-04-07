package top.ourfor.app.iPlayClient.view;

public interface PlayerEventDelegate {
    default void onEvent(PlayerGestureType type, Object value) {
    }
}

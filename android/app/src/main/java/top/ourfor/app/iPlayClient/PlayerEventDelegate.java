package top.ourfor.app.iPlayClient;

public interface PlayerEventDelegate {
    default void onEvent(PlayerGestureType type, Object value) {
    }
}

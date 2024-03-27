package top.ourfor.app.iPlayClient;

public interface PlayerEventListener {
    default void onPropertyChange(String name, Object value) {};
    default void onWindowSizeChange() {}
}

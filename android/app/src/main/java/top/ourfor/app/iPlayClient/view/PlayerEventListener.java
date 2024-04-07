package top.ourfor.app.iPlayClient.view;

public interface PlayerEventListener {
    default void onPropertyChange(String name, Object value) {};
    default void onWindowSizeChange() {}
    default void onSelectSubtitle() {}
    default void onSelectAudio() {}
}

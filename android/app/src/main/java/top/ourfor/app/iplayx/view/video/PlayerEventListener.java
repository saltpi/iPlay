package top.ourfor.app.iplayx.view.video;

public interface PlayerEventListener {
    default void onPropertyChange(PlayerPropertyType name, Object value) {};
    default void onWindowSizeChange() {}
    default void onSelectSubtitle() {}
    default void onSelectAudio() {}
    default void onSelectVideo() {}

    default void onPipEnter() {};
    default void onTapPlaylist() {}
    default void onTapComment() {}

    default void onOrientationChange() {};
}

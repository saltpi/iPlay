package top.ourfor.app.iplay.view.video;

public interface PlayerSelectDelegate<T> {
    default void onClose() {};
    default void onSelect(T data) {}
    default void onDeselect(T data) {}
}

package top.ourfor.app.iplayx.view.video;

public interface PlayerSelectDelegate<T> {
    default void onClose() {};
    default void onSelect(T data) {}
    default void onDeselect(T data) {}
}

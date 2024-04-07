package top.ourfor.app.iPlayClient.view;

public interface PlayerSelectDelegate<T> {
    default void onClose() {};
    default void onSelect(T data) {}
    default void onDeselect(T data) {}
}

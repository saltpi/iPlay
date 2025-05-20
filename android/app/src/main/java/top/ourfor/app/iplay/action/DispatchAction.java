package top.ourfor.app.iplay.action;

public interface DispatchAction {
    default void runOnUiThread(Runnable action) {}
}

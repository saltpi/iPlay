package top.ourfor.app.iplayx.action;

public interface DispatchAction {
    default void runOnUiThread(Runnable action) {}
}

package top.ourfor.app.iplayx.bean;

public interface PageLifecycle {
    default void onAttach() {};
    default void onDetach() {};
}

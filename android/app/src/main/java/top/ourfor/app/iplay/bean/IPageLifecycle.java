package top.ourfor.app.iplay.bean;

public interface IPageLifecycle {
    default void onAttach() {};
    default void onDetach() {};
}

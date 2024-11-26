package top.ourfor.app.iplayx.action;

public interface AnimationAction {
    default void cancelAnimation() {};
    default void playAnimation() {};
    default void setVisibility(int visibility) {};
    default void post(Runnable runnable) {};
}

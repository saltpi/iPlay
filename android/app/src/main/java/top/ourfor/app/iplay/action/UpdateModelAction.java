package top.ourfor.app.iplay.action;

public interface UpdateModelAction {
    <T> void updateModel(T model);
    default <T> void updateSelectionState(T model, boolean selected) {};

    default void onItemClick() {};
}

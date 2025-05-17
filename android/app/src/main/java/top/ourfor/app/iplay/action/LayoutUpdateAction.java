package top.ourfor.app.iplay.action;

import top.ourfor.app.iplay.common.type.LayoutType;

public interface LayoutUpdateAction {
    default void switchLayoutMode(LayoutType mode) { }
}

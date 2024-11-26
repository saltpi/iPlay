package top.ourfor.app.iplayx.action;

import top.ourfor.app.iplayx.common.type.LayoutType;

public interface LayoutUpdateAction {
    default void switchLayoutMode(LayoutType mode) { }
}

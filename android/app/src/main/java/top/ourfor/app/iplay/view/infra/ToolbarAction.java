package top.ourfor.app.iplay.view.infra;

import android.view.Menu;

public interface ToolbarAction {
    enum Position {
        Auto,
        Left,
        Right
    }

    default void inflateMenu(int resId, Position position) {}
    default void clear() {}
    default Menu getLeftMenu() { return null; }
    default Menu getRightMenu() { return null; }
    default void setTitle(String title) {}
}

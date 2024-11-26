package top.ourfor.app.iplayx.action;

import top.ourfor.app.iplayx.page.setting.theme.ThemeColorModel;

public interface NavigationTitleBar {
    default void setNavTitle(String title) {};
    default void setNavTitle(int id) {}

    interface ThemeManageAction {
        default void setStatusBarTextColor(boolean isDark) { }
        default void switchToDarkMode(boolean isDarkMode) { }
        default void switchToAutoModel() { }
        default boolean isDarkMode() { return false; }
        default void switchThemeColor(ThemeColorModel.ThemeColor color) { }
    }
}

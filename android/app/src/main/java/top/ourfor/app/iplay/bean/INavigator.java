package top.ourfor.app.iplay.bean;

import java.util.Map;

import top.ourfor.app.iplay.common.model.HomeTabModel;
import top.ourfor.app.iplay.page.Page;

public interface INavigator {
    default void pushPage(int id, Map<String, Object> params) {}
    default void pushPage(String name, Map<String, Object> params) {}
    default void pushPage(Page page, Map<String, Object> params) {}
    default boolean popPage() {
        return true;
    }

    default boolean canGoBack() {
        return true;
    }

    default Page getCurrentPage() {
        return null;
    };

    default int getCurrentPageId() {
        return 0;
    }

    default Map<String, HomeTabModel> getHomeTabs() {
        return null;
    }
}

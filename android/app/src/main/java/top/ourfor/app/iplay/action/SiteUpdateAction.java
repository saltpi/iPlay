package top.ourfor.app.iplay.action;

import top.ourfor.app.iplay.model.SiteModel;

public interface SiteUpdateAction {
    default void onSiteUpdate() {};
    default void onSiteModify(SiteModel model) {};
}

package top.ourfor.app.iplayx.action;

import top.ourfor.app.iplayx.model.SiteModel;

public interface SiteUpdateAction {
    default void onSiteUpdate() {};
    default void onSiteModify(SiteModel model) {};
}

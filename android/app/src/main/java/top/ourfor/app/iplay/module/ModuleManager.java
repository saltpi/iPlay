package top.ourfor.app.iplay.module;

import static top.ourfor.app.iplay.module.Bean.XGET;
import top.ourfor.app.iplay.store.IAppStore;

public class ModuleManager {

    public CacheModule defaultCacheModule() {
        return new CacheModule();
    }

    public IAppStore defaultIAppStore() {
        return XGET(IAppStore.class);
    }
}

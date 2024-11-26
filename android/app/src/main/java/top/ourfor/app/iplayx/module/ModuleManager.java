package top.ourfor.app.iplayx.module;

import static top.ourfor.app.iplayx.module.Bean.XGET;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import top.ourfor.app.iplayx.store.GlobalStore;

@Module
@InstallIn(SingletonComponent.class)
public class ModuleManager {

    @Provides
    @Singleton
    public CacheModule defaultCacheModule() {
        return new CacheModule();
    }

    @Provides
    @Singleton
    public GlobalStore defaultGlobalStore() {
        return XGET(GlobalStore.class);
    }
}

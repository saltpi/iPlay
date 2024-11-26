package top.ourfor.app.iplayx.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheModule {
    public void clean() {
        log.info("clean cache");
    }
}

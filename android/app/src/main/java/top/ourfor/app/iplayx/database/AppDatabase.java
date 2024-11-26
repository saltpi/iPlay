package top.ourfor.app.iplayx.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import top.ourfor.app.iplayx.database.entity.KVCacheEntity;
import top.ourfor.app.iplayx.database.repo.KVCacheRepo;

@Database(entities = {KVCacheEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract KVCacheRepo kvCache();
}
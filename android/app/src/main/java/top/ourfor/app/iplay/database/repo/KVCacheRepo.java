package top.ourfor.app.iplay.database.repo;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Upsert;

import java.util.List;

import top.ourfor.app.iplay.database.entity.KVCacheEntity;

@Dao
public interface KVCacheRepo {
    @Query("SELECT * FROM kv_cache")
    List<KVCacheEntity> getAll();

    @Query("SELECT * FROM kv_cache WHERE `key` IN (:keys)")
    List<KVCacheEntity> loadAllByIds(String[] keys);

    @Query("SELECT * FROM kv_cache WHERE `key` = :key")
    KVCacheEntity get(String key);

    @Insert
    void insertAll(KVCacheEntity... items);

    @Update
    void update(KVCacheEntity item);

    @Upsert
    void upsert(KVCacheEntity item);

    @Delete
    void delete(KVCacheEntity user);
}
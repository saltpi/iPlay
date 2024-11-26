package top.ourfor.app.iplayx.util;

import static top.ourfor.app.iplayx.module.Bean.XGET;

import android.app.Application;

import androidx.room.Room;

import lombok.val;
import top.ourfor.app.iplayx.bean.JSONAdapter;
import top.ourfor.app.iplayx.bean.KVStorage;
import top.ourfor.app.iplayx.database.AppDatabase;
import top.ourfor.app.iplayx.database.entity.KVCacheEntity;

public class SqliteStorage implements KVStorage {
    public static final SqliteStorage shared = new SqliteStorage();

    private final AppDatabase db;
    public SqliteStorage() {
        val app = XGET(Application.class);
        db = Room.databaseBuilder(app,
                AppDatabase.class, "db")
                .allowMainThreadQueries()
                .build();
    }

    @Override
    public void set(String key, String value) {
        val entity = new KVCacheEntity();
        entity.key = key;
        entity.value = value;
        db.kvCache().upsert(entity);
    }

    @Override
    public String get(String key) {
        val entity = db.kvCache().get(key);
        if (entity == null) {
            return null;
        }
        return entity.value;
    }

    @Override
    public <T> T getObject(String key, Class<T> clazz) {
        val value = get(key);
        if (value == null) {
            return null;
        }
        return XGET(JSONAdapter.class).fromJSON(value, clazz);
    }

    @Override
    public void setObject(String key, Object obj) {
        set(key, XGET(JSONAdapter.class).toJSON(obj));
    }
}

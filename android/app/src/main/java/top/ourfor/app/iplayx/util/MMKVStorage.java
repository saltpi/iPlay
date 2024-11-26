package top.ourfor.app.iplayx.util;

import static top.ourfor.app.iplayx.module.Bean.XGET;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.tencent.mmkv.MMKV;

import lombok.val;
import top.ourfor.app.iplayx.bean.JSONAdapter;
import top.ourfor.app.iplayx.bean.KVStorage;

public class MMKVStorage implements KVStorage {
    public static final MMKVStorage shared = new MMKVStorage();

    public MMKV client;
    SharedPreferences sharedPreferences= null;

    public MMKVStorage() {
        val app = XGET(Application.class);
        if (DeviceUtil.isArmeabiV7a) {
            sharedPreferences = app.getSharedPreferences("data",Context.MODE_PRIVATE);
        } else {
            MMKV.initialize(app);
            client = MMKV.defaultMMKV();
        }
    }

    @Override
    public void set(String key, String value) {
        if (DeviceUtil.isArmeabiV7a) {
            sharedPreferences.edit().putString(key, value).apply();
        } else {
            client.putString(key, value);
        }
    }

    @Override
    public String get(String key) {
        if (DeviceUtil.isArmeabiV7a) {
            return sharedPreferences.getString(key, null);
        }
        return client.getString(key, null);
    }

    @Override
    public <T> T getObject(String key, Class<T> clazz) {
        JSONAdapter adapter = XGET(JSONAdapter.class);
        return (T)adapter.fromJSON(get(key), clazz);
    }

    @Override
    public void setObject(String key, Object obj) {
        JSONAdapter adapter = XGET(JSONAdapter.class);
        set(key, adapter.toJSON(obj));
    }
}

package top.ourfor.app.iplay.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "kv_cache")
public class KVCacheEntity {
    @PrimaryKey
    @NonNull
    public String key;

    @ColumnInfo(name = "value")
    public String value;
}

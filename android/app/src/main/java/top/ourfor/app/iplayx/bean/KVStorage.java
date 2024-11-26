package top.ourfor.app.iplayx.bean;

public interface KVStorage {
    default void set(String key, String value) {};
    default String get(String key) { return null; }
    default <T> T getObject(String key, Class<T> clazz) { return null; }
    default void setObject(String key, Object obj) {};
    default void remove(String key) {};
    default void clear() {};
}

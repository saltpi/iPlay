package top.ourfor.app.iPlayClient.module;

import android.os.Build;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public class BeanManager {
    private static WeakHashMap<String, WeakReference> beans = new WeakHashMap<>(20);

    public static <T> T get(Class<?> clazz) {
        WeakReference<T> ref = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            ref = beans.get(clazz.getTypeName());
        }
        return ref != null ? ref.get() : null;
    }

    public static <T> void set(Class<?> clazz, T bean) {
        WeakReference<T> ref = new WeakReference(bean);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            beans.put(clazz.getTypeName(), ref);
        }
    }

    public static <T> void set(Class<?>[] clazzs, T bean) {
        WeakReference<T> ref = new WeakReference(bean);
        for (Class<?> clazz : clazzs) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                beans.put(clazz.getTypeName(), ref);
            }
        }
    }

    public static <T> void remove(Class<?>[] clazzs) {
        for (Class<?> clazz : clazzs) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                beans.remove(clazz.getTypeName());
            }
        }
    }
}

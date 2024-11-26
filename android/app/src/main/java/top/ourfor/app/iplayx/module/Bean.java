package top.ourfor.app.iplayx.module;

import android.os.Build;

import java.lang.ref.WeakReference;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.WeakHashMap;

import lombok.NonNull;
import lombok.val;

public class Bean {
    private static WeakHashMap<String, WeakReference> beans = new WeakHashMap<>(20);
    private static HashMap<String, BeanProxy> listener = new HashMap<>(20);
    private static HashMap<String, Object> proxyBeans = new HashMap<>(20);

    public static <T> T XGET(Class<T> clazz) {
        WeakReference<T> ref = null;
        String key = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            key = clazz.getTypeName();
        } else {
            key = clazz.getName();
        }
        ref = beans.get(key);
        return ref != null ? ref.get() : null;
    }

    public static <T> void XSET(Class<T> clazz, T bean) {
        WeakReference<T> ref = new WeakReference<>(bean);
        String key = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            key = clazz.getTypeName();
        } else {
            key = clazz.getName();
        }
        beans.put(key, ref);
    }

    public static <T> void XSET(Class<T>[] clazzs, T bean) {
        WeakReference<T> ref = new WeakReference(bean);
        for (Class<?> clazz : clazzs) {
            String key = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                key = clazz.getTypeName();
            } else {
                key = clazz.getName();
            }
            beans.put(key, ref);
        }
    }

    public static <T> void XWATCH(Class<T> clazz, T bean) {
        String key = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            key = clazz.getTypeName();
        } else {
            key = clazz.getName();
        }
        BeanProxy proxy = listener.getOrDefault(key, new BeanProxy());
        proxy.beans.put(bean.getClass().getName(), bean);
        listener.put(key, proxy);
        if (!beans.containsKey(key)) {
            T proxyBean = (T) Proxy.newProxyInstance(
                    clazz.getClassLoader(),
                    new Class[] { clazz },
                    proxy
            );
            proxyBeans.put("PROXY:" + key, proxyBean);
            beans.put(key, new WeakReference(proxyBean));
        }
    }

    @Deprecated
    public static <T> T get(Class<T> clazz) {
        WeakReference<T> ref = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ref = beans.get(clazz.getTypeName());
        } else {
            ref = beans.get(clazz.getName());
        }
        return ref != null ? ref.get() : null;
    }

    @Deprecated
    public static <T> void set(Class<T> clazz, T bean) {
        WeakReference<T> ref = new WeakReference(bean);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            beans.put(clazz.getTypeName(), ref);
        }
    }

    @Deprecated
    public static <T> void set(Class<T>[] clazzs, T bean) {
        WeakReference<T> ref = new WeakReference(bean);
        for (Class<?> clazz : clazzs) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                beans.put(clazz.getTypeName(), ref);
            }
        }
    }

    public static <T> void remove(Class<T> clazz) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            beans.remove(clazz.getTypeName());
        }
    }

    @Deprecated
    public static <T> void watch(Class<T> clazz, T bean) {
        String key = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            key = clazz.getTypeName();
        } else {
            key = clazz.getName();
        }
        BeanProxy proxy = listener.getOrDefault(key, new BeanProxy());
        proxy.beans.put(bean.getClass().getName(), bean);
        listener.put(key, proxy);
        if (!beans.containsKey(key)) {
            T proxyBean = (T) Proxy.newProxyInstance(
                    clazz.getClassLoader(),
                    new Class[] { clazz },
                    proxy
            );
            proxyBeans.put("PROXY:" + key, proxyBean);
            beans.put(key, new WeakReference(proxyBean));
        }
    }

}

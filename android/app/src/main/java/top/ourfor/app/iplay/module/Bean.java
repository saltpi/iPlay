package top.ourfor.app.iplay.module;

import android.os.Build;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.WeakHashMap;

import lombok.NonNull;
import lombok.val;

@SuppressWarnings({"unchecked", "rawtypes"})
public class Bean {
    private static final WeakHashMap<Class, WeakReference> beans = new WeakHashMap<>(20);
    private static final HashMap<Class, BeanProxy> listener = new HashMap<>(20);
    private static final HashMap<Class, Object> proxyBeans = new HashMap<>(20);

    @NonNull
    public static <T> T XGET(Class<T> clazz) {
        WeakReference<T> ref = null;
        val key = clazz;
        ref = beans.get(key);
        return ref != null ? ref.get() : Null.shared.proxy(clazz);
    }

    public static <T> void XSET(Class<T> clazz, @Nullable T bean) {
        WeakReference<T> ref = new WeakReference<>(bean);
        val key = clazz;
        beans.put(key, ref);
    }

    public static <T> void XSET(Class<T>[] clazzs, @Nullable T bean) {
        WeakReference<T> ref = new WeakReference<>(bean);
        for (Class<?> clazz : clazzs) {
            var key = clazz;
            beans.put(key, ref);
        }
    }

    public static <T> void XWATCH(Class<T> clazz, T bean) {
        var key = clazz;
        BeanProxy proxy = listener.getOrDefault(key, new BeanProxy());
        assert proxy != null;
        proxy.beans.put(bean.getClass().getName(), bean);
        listener.put(key, proxy);
        if (!beans.containsKey(key)) {
            T proxyBean = (T) Proxy.newProxyInstance(
                    clazz.getClassLoader(),
                    new Class[] { clazz },
                    proxy
            );
            proxyBeans.put(key, proxyBean);
            beans.put(key, new WeakReference(proxyBean));
        }
    }

    public static <T> void remove(Class<T> clazz) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            beans.remove(clazz.getTypeName());
        }
    }

}

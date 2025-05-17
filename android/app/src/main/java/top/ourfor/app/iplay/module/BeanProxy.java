package top.ourfor.app.iplay.module;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.WeakHashMap;

public class BeanProxy implements InvocationHandler {
    public WeakHashMap<String, Object> beans = new WeakHashMap<>(2);

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        for (Object bean : beans.values()) {
            if (method.getDeclaringClass().isInstance(bean)) {
                method.invoke(bean, args);
            }
        }
        return this;
    }
}

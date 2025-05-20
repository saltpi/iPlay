package top.ourfor.app.iplay.module;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode
@SuppressWarnings({"unchecked"})
public class Null implements InvocationHandler  {
    public static Null shared = new Null();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.warn("Null: call method {}, with args: {}", method, args);
        return this;
    }

    public <T> T proxy(Class<T> clazz) {
        // if class is not interface return null
        if (!clazz.isInterface()) return null;
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[] { clazz },
                this
        );
    }
}

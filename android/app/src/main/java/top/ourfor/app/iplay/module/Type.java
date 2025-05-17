package top.ourfor.app.iplay.module;

import com.fasterxml.jackson.core.type.TypeReference;

public class Type {
    public static <T> TypeReference<T> typeof(T obj) {
        return new TypeReference<T>() {};
    }
}

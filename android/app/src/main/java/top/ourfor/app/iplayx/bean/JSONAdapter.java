package top.ourfor.app.iplayx.bean;

import com.fasterxml.jackson.core.type.TypeReference;

public interface JSONAdapter {

    default String toJSON(Object obj) {
        return null;
    }

    default <T> T fromJSON(String json, Class<T> clazz) {
        return null;
    }

    default <T> T fromJSON(String json, TypeReference<T> typeReference) {
        return null;
    }
}

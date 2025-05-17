package top.ourfor.app.iplay.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Type;

import lombok.extern.slf4j.Slf4j;
import top.ourfor.app.iplay.bean.IJSONAdapter;

@Slf4j
public class JacksonJsonAdapter implements IJSONAdapter {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static JacksonJsonAdapter shared = new JacksonJsonAdapter();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        // disable create object when json is empty
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        // disable fail when json is empty
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public <T> T fromJSON(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public <T> T fromJSON(String json, Type type) {
        try {
            var newType = new TypeReference<Object>() {
                @Override
                public Type getType() {
                    return type;
                }
            };
            return (T) objectMapper.readValue(json, newType);
        } catch (Exception e) {
            log.error("Failed to parse json: {}", json);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public <T> T fromJSON(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            log.error("Failed to parse json: {}", json);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toJSON(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

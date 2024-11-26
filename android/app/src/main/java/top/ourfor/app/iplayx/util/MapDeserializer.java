package top.ourfor.app.iplayx.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapDeserializer<T> extends JsonDeserializer<Map<String, T>> {
    private final TypeReference<Map<String, T>> type;

    public MapDeserializer(TypeReference<Map<String, T>> type) {
        this.type = type;
    }

    @Override
    public Map<String, T> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return new ConcurrentHashMap<>(p.readValueAs(new TypeReference<Map<String, Object>>() {}));
    }
}

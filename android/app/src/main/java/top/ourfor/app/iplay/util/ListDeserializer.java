package top.ourfor.app.iplay.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ListDeserializer extends JsonDeserializer<List> {
    @Override
    public List deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return new CopyOnWriteArrayList(p.readValueAs(List.class));
    }
}

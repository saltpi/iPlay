package top.ourfor.app.iplay.util;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

@Builder
@With
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HTTPModel<T> {
    String url;
    Map<String, String> query;
    Map<String, String> headers;
    Map<String, String> params;
    String method;
    String body;
    Class<?> modelClass;
    boolean disableRedirect;
    TypeReference<? extends T> typeReference;
}

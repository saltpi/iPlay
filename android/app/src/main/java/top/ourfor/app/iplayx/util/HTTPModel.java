package top.ourfor.app.iplayx.util;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.With;

@Builder
@With
@Setter
@Getter
public class HTTPModel {
    String url;
    Map<String, String> query;
    Map<String, String> headers;
    Map<String, String> params;
    String method;
    String body;
    Class<?> modelClass;
    boolean disableRedirect;
    TypeReference<?> typeReference;
}

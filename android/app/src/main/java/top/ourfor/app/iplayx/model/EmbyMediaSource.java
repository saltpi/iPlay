package top.ourfor.app.iplayx.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EmbyMediaSource {
    @JsonProperty("Id")
    String id;
    @JsonProperty("Container")
    String container;
    @JsonProperty("Name")
    String name;
    @JsonProperty("Path")
    String path;
    @JsonProperty("DirectStreamUrl")
    String directStreamUrl;
    @JsonProperty("TranscodingUrl")
    String transcodingUrl;
    @JsonProperty("MediaStreams")
    List<EmbyMediaStream> mediaStreams;

    public void buildUrl(String url) {
        if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
        if (directStreamUrl != null && !directStreamUrl.startsWith("http")) {
            directStreamUrl = url + directStreamUrl;
        }
        for (EmbyMediaStream mediaStream : mediaStreams) {
            mediaStream.buildUrl(url);
        }
    }

    public void buildDirectStreamUrl(String url, Map<String, String> params) {
        if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
        if (directStreamUrl == null) {
            StringBuilder builder = new StringBuilder(url);
            builder.append("/Videos/").append(id).append("/Stream?static=true");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }
            directStreamUrl = builder.toString();
        }
    }
}

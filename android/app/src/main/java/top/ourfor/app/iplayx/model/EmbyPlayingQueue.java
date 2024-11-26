package top.ourfor.app.iplayx.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EmbyPlayingQueue {
    @JsonProperty("Id")
    String id;
    @JsonProperty("PlaylistItemId")
    String playlistItemId;
}

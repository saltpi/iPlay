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
public class EmbyUserData {
    @JsonProperty("UnplayedItemCount")
    Long unplayedItemCount;
    @JsonProperty("PlaybackPositionTicks")
    Long playbackPositionTicks;
    @JsonProperty("PlayCount")
    Long playCount;
    @JsonProperty("IsFavorite")
    Boolean isFavorite;
    @JsonProperty("Played")
    Boolean isPlayed;
}

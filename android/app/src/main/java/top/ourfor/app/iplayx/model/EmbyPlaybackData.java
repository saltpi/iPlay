package top.ourfor.app.iplayx.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;
import lombok.experimental.Accessors;
import top.ourfor.app.iplayx.common.model.SeekableRange;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class EmbyPlaybackData {
    @JsonIgnore
    public static long kIPLXSecond2TickScale = 10000000;

    @JsonProperty("AudioStreamIndex")
    Long audioStreamIndex;
    @JsonProperty("BufferedRanges")
    List<List<Long>> bufferedRanges;
    @JsonProperty("CanSeek")
    Boolean canSeek;
    @JsonProperty("EventName")
    String eventName;
    @JsonProperty("NowPlayingQueue")
    List<EmbyPlayingQueue> nowPlayingQueue;
    @JsonProperty("IsMuted")
    Boolean isMuted;
    @JsonProperty("IsPaused")
    Boolean isPaused;
    @JsonProperty("ItemId")
    String itemId;
    @JsonProperty("MaxStreamingBitrate")
    Long maxStreamingBitrate;
    @JsonProperty("MediaSourceId")
    String mediaSourceId;
    @JsonProperty("PlaySessionId")
    String playSessionId;
    @JsonProperty("PlayMethod")
    String playMethod;
    @JsonProperty("PlaybackRate")
    Double playbackRate;
    @JsonProperty("PlaybackStartTimeTicks")
    Long playbackStartTimeTicks;
    @JsonProperty("PlaylistIndex")
    Long playlistIndex;
    @JsonProperty("PlaylistLength")
    Long playlistLength;
    @JsonProperty("PositionTicks")
    Long positionTicks;
    @JsonProperty("RepeatMode")
    String repeatMode;
    @JsonProperty("SeekableRanges")
    List<SeekableRange> seekableRanges;
    @JsonProperty("SubtitleOffset")
    Double subtitleOffset;
    @JsonProperty("SubtitleStreamIndex")
    Long subtitleStreamIndex;
    @JsonProperty("VolumeLevel")
    Double volumeLevel;
}

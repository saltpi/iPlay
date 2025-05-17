package top.ourfor.app.iplay.model;

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
public class MediaUserData {
    boolean isFavorite;
    Long playbackPositionTicks;
    Long unplayedItemCount;
}

package top.ourfor.app.iplayx.store;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.With;
import top.ourfor.app.iplayx.model.EmbyAlbumModel;
import top.ourfor.app.iplayx.model.EmbyMediaModel;

@Data
@With
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class EmbyDataSource {
    CopyOnWriteArrayList<EmbyAlbumModel> albums;
    CopyOnWriteArrayList<EmbyMediaModel> resume;

    ConcurrentHashMap<String, CopyOnWriteArrayList<EmbyMediaModel>> albumMedias;
    ConcurrentHashMap<String, EmbyMediaModel> mediaMap;
    ConcurrentHashMap<String, CopyOnWriteArrayList<EmbyMediaModel>> seriesSeasons;
    ConcurrentHashMap<String, CopyOnWriteArrayList<EmbyMediaModel>> seasonEpisodes;
}

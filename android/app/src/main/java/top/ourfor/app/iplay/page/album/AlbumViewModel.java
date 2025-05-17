package top.ourfor.app.iplay.page.album;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.Getter;
import lombok.NoArgsConstructor;
import top.ourfor.app.iplay.model.MediaModel;

@Getter
@NoArgsConstructor
public class AlbumViewModel extends ViewModel {
    private final MutableLiveData<List<MediaModel>> medias = new MutableLiveData<>(new CopyOnWriteArrayList<>());
}

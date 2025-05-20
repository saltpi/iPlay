package top.ourfor.app.iplay.page.media;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import top.ourfor.app.iplay.model.MediaModel;
import top.ourfor.app.iplay.store.IAppStore;

@NoArgsConstructor
public class MediaViewModel extends ViewModel {
    @Getter
    private final MutableLiveData<MediaModel> media = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<List<MediaModel>> seasons = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<List<MediaModel>> similar = new MutableLiveData<>();


    IAppStore store;

    MediaViewModel(IAppStore store) {
        this.store = store;
    }

    public void fetchDetail(String id) {
        store.getDetail(id, media::postValue);
    }

    public void fetchSimilar(String id) {
        store.getSimilar(id, similar::postValue);
    }
}

package top.ourfor.app.iplayx.page.media;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import top.ourfor.app.iplayx.model.EmbyMediaModel;
import top.ourfor.app.iplayx.store.GlobalStore;

@NoArgsConstructor
public class MediaViewModel extends ViewModel {
    @Getter
    private final MutableLiveData<List<EmbyMediaModel>> seasons = new MutableLiveData<>();

    @Getter
    private final MutableLiveData<List<EmbyMediaModel>> similar = new MutableLiveData<>();

    GlobalStore store;

    MediaViewModel(GlobalStore store) {
        this.store = store;
    }

    public void fetchSimilar(String id) {
        store.getSimilar(id, similar::postValue);
    }
}

package top.ourfor.app.iplay.page.episode;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


import lombok.Getter;
import top.ourfor.app.iplay.model.MediaModel;

@Getter
public class EpisodeViewModel extends ViewModel {
    private final MutableLiveData<List<MediaModel>> episodes = new MutableLiveData<>(new CopyOnWriteArrayList<>());

    public EpisodeViewModel() {
    }
}

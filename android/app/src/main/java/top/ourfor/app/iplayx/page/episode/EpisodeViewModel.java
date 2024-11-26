package top.ourfor.app.iplayx.page.episode;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;

import lombok.Getter;
import top.ourfor.app.iplayx.model.EmbyMediaModel;

@Getter
public class EpisodeViewModel extends ViewModel {
    private final MutableLiveData<List<EmbyMediaModel>> episodes = new MutableLiveData<>(new CopyOnWriteArrayList<>());

    @Inject
    public EpisodeViewModel() {
    }
}

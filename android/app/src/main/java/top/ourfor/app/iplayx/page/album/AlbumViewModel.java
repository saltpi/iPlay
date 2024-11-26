package top.ourfor.app.iplayx.page.album;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.Getter;
import lombok.NoArgsConstructor;
import top.ourfor.app.iplayx.model.EmbyMediaModel;

@Getter
@NoArgsConstructor
public class AlbumViewModel extends ViewModel {
    private final MutableLiveData<List<EmbyMediaModel>> medias = new MutableLiveData<>(new CopyOnWriteArrayList<>());
}

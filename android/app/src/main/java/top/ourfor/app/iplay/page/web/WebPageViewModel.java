package top.ourfor.app.iplay.page.web;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.ourfor.app.iplay.api.file.File;
import top.ourfor.app.iplay.store.IAppStore;

@Slf4j
@Getter
@NoArgsConstructor
public class WebPageViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> path = new MutableLiveData<>("/");
    private final MutableLiveData<List<File>> cacheFiles = new MutableLiveData<>(null);

    IAppStore store;

    WebPageViewModel(IAppStore store) {
        this.store = store;
    }
}

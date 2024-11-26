package top.ourfor.app.iplayx.page.web;

import static top.ourfor.app.iplayx.module.Bean.XGET;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplayx.api.file.File;
import top.ourfor.app.iplayx.api.file.FileProvider;
import top.ourfor.app.iplayx.api.file.FileProviderFactory;
import top.ourfor.app.iplayx.store.GlobalStore;

@Slf4j
@Getter
@NoArgsConstructor
@HiltViewModel
public class WebPageViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> path = new MutableLiveData<>("/");
    private final MutableLiveData<List<File>> cacheFiles = new MutableLiveData<>(null);

    GlobalStore store;

    @Inject
    WebPageViewModel(GlobalStore store) {
        this.store = store;
    }
}

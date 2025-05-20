package top.ourfor.app.iplay.page.file;

import static top.ourfor.app.iplay.module.Bean.XGET;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplay.api.file.File;
import top.ourfor.app.iplay.api.file.FileProvider;
import top.ourfor.app.iplay.api.file.FileProviderFactory;
import top.ourfor.app.iplay.store.IAppStore;

@Slf4j
@Getter
@NoArgsConstructor
public class FileViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> path = new MutableLiveData<>("/");
    private final MutableLiveData<FileProvider> fileProvider = new MutableLiveData<>(null);
    private final MutableLiveData<List<File>> cacheFiles = new MutableLiveData<>(null);

    IAppStore store;

    FileViewModel(IAppStore store) {
        this.store = store;
        val drive = store.getDrive();
        if (drive != null) {
            val fileProvider = FileProviderFactory.create(drive);
            this.fileProvider.setValue(fileProvider);
        }
    }

    void listFiles(String path) {
        if (fileProvider.getValue() == null) return;

        isLoading.postValue(true);
        XGET(ThreadPoolExecutor.class).submit(() -> {
            fileProvider.getValue().listFiles(path, files -> {
                isLoading.postValue(false);
                cacheFiles.postValue(files);
            });
        });
    }

    void updateIfNeed() {
        if (cacheFiles.getValue() != null) {
            isLoading.postValue(false);
            cacheFiles.postValue(cacheFiles.getValue());
            return;
        }
        listFiles(path.getValue());
    }

    void link(File file, Consumer<String> completion) {
        isLoading.postValue(true);
        fileProvider.getValue().link(file, url -> {
            isLoading.postValue(false);
            completion.accept(url);
        });
    }
}

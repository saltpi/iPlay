package top.ourfor.app.iplayx.page.home;

import static top.ourfor.app.iplayx.module.Bean.XGET;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.common.type.MediaLayoutType;
import top.ourfor.app.iplayx.model.EmbyAlbumMediaModel;
import top.ourfor.app.iplayx.model.EmbyAlbumModel;
import top.ourfor.app.iplayx.store.GlobalStore;

@Slf4j
@Getter
@NoArgsConstructor
@HiltViewModel
public class HomeViewModel extends ViewModel {
    private final MutableLiveData<List<EmbyAlbumModel>> albums = new MutableLiveData<>(null);
    private final MutableLiveData<List<Object>> albumCollection = new MutableLiveData<>(new CopyOnWriteArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> hasValidSite = new MutableLiveData<>(false);
    private boolean isFirstLoad = true;

    GlobalStore store;

    @Inject
    HomeViewModel(GlobalStore store) {
        this.store = store;
    }

    public void loadFromLocalCache() {
        val datasource = store.getDataSource();
        val albums = datasource.getAlbums();
        val albumMedias = datasource.getAlbumMedias();
        val context = XGET(Activity.class);
        if (albums == null || albumMedias == null) return;
        List<Object> newItems = Collections.synchronizedList(new ArrayList<>());
        newItems.add(EmbyAlbumMediaModel.builder()
                .album(null)
                .medias(new CopyOnWriteArrayList(albums))
                .build());

        val resume = store.getDataSource().getResume();
        resume.forEach(item -> item.setLayoutType(MediaLayoutType.Backdrop));
        val resumeItem = EmbyAlbumMediaModel.builder()
                .album(null)
                .title(context.getString(R.string.continue_watch))
                .layout(MediaLayoutType.Backdrop)
                .medias(new CopyOnWriteArrayList<>(resume))
                .build();
        if (!resume.isEmpty()) {
            newItems.add(resumeItem);
        }

        albums.forEach(album -> {
            val medias = albumMedias.get(album.getId());
            if (album == null || medias == null || medias.size() == 0) return;
            val item = EmbyAlbumMediaModel.builder()
                    .album(album)
                    .medias(new CopyOnWriteArrayList<>(medias))
                    .build();
            newItems.add(item);
        });
        this.albumCollection.postValue(newItems);
    }

    private void fetchEmbyAlbumModels(List<EmbyAlbumModel> albums) {
        val datasource = store.getDataSource();
        val albumMedias = datasource.getAlbumMedias();
        albumMedias.clear();
        val latch = new CountDownLatch(albums.size() + 1);
        store.getResume(resume -> {
            latch.countDown();
        });
        albums.forEach(item -> {
            if (item == null) return;
            store.getAlbumLatestMedias(item.getId(), medias -> {
                latch.countDown();
            });
        });
        try {
            latch.await();
            loadFromLocalCache();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            isLoading.postValue(false);
        }
    }

    public void fetchAlbumsIfNeeded() {
        if (albumCollection.getValue() == null ||
            albumCollection.getValue().isEmpty() ||
            isFirstLoad) {
            isFirstLoad = false;
            fetchAlbums();
        }
    }

    public void fetchAlbums() {
        isLoading.postValue(true);
        store.getAlbums(albums -> {
            this.albums.postValue(albums);
            if (albums == null) {
                isLoading.postValue(false);
                return;
            }
            XGET(ThreadPoolExecutor.class).execute(() -> {
                fetchEmbyAlbumModels(albums);
            });
            log.debug("albums: {}", albums);
        });
    }
}

package top.ourfor.app.iplay.page.star;

import static top.ourfor.app.iplay.module.Bean.XGET;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.common.type.MediaLayoutType;
import top.ourfor.app.iplay.common.type.MediaType;
import top.ourfor.app.iplay.store.IAppStore;

@Slf4j
@Getter
@NoArgsConstructor
public class StarViewModel extends ViewModel {
    private final MutableLiveData<List<MediaStarModel>> starItems = new MutableLiveData<>(new CopyOnWriteArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    IAppStore store;

    StarViewModel(IAppStore store) {
        this.store = store;
    }


    public void fetchStartData(boolean force) {
        if (!force && !starItems.getValue().isEmpty()) {
            starItems.postValue(starItems.getValue());
            return;
        }

        isLoading.postValue(true);
        val models = new CopyOnWriteArrayList<MediaStarModel>();
        val types = List.of(MediaType.Movie, MediaType.Series, MediaType.Episode);
        CountDownLatch latch = new CountDownLatch(types.size());
        Consumer<MediaType> task = type -> store.getFavoriteMedias(type, medias -> {
            latch.countDown();
            if (medias == null || medias.isEmpty()) {
                isLoading.postValue(false);
                return;
            }
            val name = switch (type) {
                case Movie -> R.string.start_type_movie;
                case Series -> R.string.star_type_series;
                case Episode -> R.string.star_type_episode;
                default -> R.string.star_type_unknown;
            };
            medias.forEach(media -> {
                media.setLayoutType(type == MediaType.Episode ? MediaLayoutType.Backdrop : MediaLayoutType.Poster);
            });
            val model = MediaStarModel.builder()
                    .name(XGET(Activity.class).getString(name))
                    .type(type)
                    .medias(medias)
                    .build();
            boolean exist = models.stream().anyMatch(m -> m.getType() == type);
            if (!exist) models.add(model);
            else models.stream().filter(m -> m.getType() == type).findFirst().ifPresent(m -> m.setMedias(medias));
            models.sort(Comparator.comparing(MediaStarModel::getType));
            starItems.postValue(models);
        });
        val pool = XGET(ThreadPoolExecutor.class);
        types.forEach(type -> pool.submit(() -> task.accept(type)));
        try {
            latch.await();
            isLoading.postValue(false);
        } catch (InterruptedException e) {
            log.error("fetch star data error", e);
        }
    }

    public void fetchStarData() {
        fetchStartData(true);
    }
}

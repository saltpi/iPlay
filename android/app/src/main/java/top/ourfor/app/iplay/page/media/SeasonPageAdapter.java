package top.ourfor.app.iplay.page.media;

import static top.ourfor.app.iplay.module.Bean.XGET;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.bean.INavigator;
import top.ourfor.app.iplay.common.type.MediaLayoutType;
import top.ourfor.app.iplay.model.MediaModel;
import top.ourfor.app.iplay.page.home.MediaViewCell;
import top.ourfor.app.iplay.store.IAppStore;
import top.ourfor.app.iplay.view.LinearLayoutManager;
import top.ourfor.app.iplay.view.ListItemClickEvent;
import top.ourfor.app.iplay.view.ListView;


public class SeasonPageAdapter extends PagerAdapter {
    @Getter
    private List<MediaModel> seasons;
    private List<ListView<MediaModel>> views;

    @Setter
    private Context context;

    @Override
    public int getCount() {
        return views == null ? 0 : views.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        val view = views.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(views.get(position));
    }

    public void setSeasons(List<MediaModel> seasons) {
        this.seasons = seasons;
        views = new ArrayList<>();
        ListView<MediaModel> seasonList = new ListView<>(context);
        seasonList.viewModel.viewCell = MediaViewCell.class;
        seasonList.setItems(seasons);
        seasonList.listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        views.add(seasonList);
        Consumer<ListItemClickEvent<MediaModel>> onClick = event -> {
            val model = event.getModel();
            val seasonId = model.getId();
            val seriesId = model.getSeriesId();
            val args = new HashMap<String, Object>();
            val isEpisode = model.isEpisode();
            int dstId = isEpisode ? R.id.playerPage : R.id.episodePage;
            if (isEpisode) {
                args.put("id", model.getId());
            } else {
                args.put("seriesId", seriesId);
                args.put("seasonId", seasonId);
                args.put("title", model.getName());
            }
            XGET(INavigator.class).pushPage(dstId, args);
        };
        seasonList.viewModel.onClick = onClick;
        val store = XGET(IAppStore.class);
        for (var season : seasons) {
            ListView<MediaModel> listView = new ListView<>(context);
            listView.listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            listView.viewModel.viewCell = MediaViewCell.class;
            listView.viewModel.onClick = onClick;
            views.add(listView);
            store.getEpisodes(season.getSeriesId(), season.getId(), episodes -> {
                episodes.forEach(episode -> episode.setLayoutType(MediaLayoutType.EpisodeDetail));
                listView.setItems(episodes);
            });
        }
    }
}

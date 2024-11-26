package top.ourfor.app.iplayx.page.media;

import static top.ourfor.app.iplayx.module.Bean.XGET;

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
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.bean.Navigator;
import top.ourfor.app.iplayx.common.type.MediaLayoutType;
import top.ourfor.app.iplayx.model.EmbyMediaModel;
import top.ourfor.app.iplayx.page.home.MediaViewCell;
import top.ourfor.app.iplayx.store.GlobalStore;
import top.ourfor.app.iplayx.util.NavigationUtil;
import top.ourfor.app.iplayx.view.LinearLayoutManager;
import top.ourfor.app.iplayx.view.ListItemClickEvent;
import top.ourfor.app.iplayx.view.ListView;


public class SeasonPageAdapter extends PagerAdapter {
    @Getter
    private List<EmbyMediaModel> seasons;
    private List<ListView<EmbyMediaModel>> views;

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

    public void setSeasons(List<EmbyMediaModel> seasons) {
        this.seasons = seasons;
        views = new ArrayList<>();
        ListView<EmbyMediaModel> seasonList = new ListView<>(context);
        seasonList.viewModel.viewCell = MediaViewCell.class;
        seasonList.setItems(seasons);
        seasonList.listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        views.add(seasonList);
        Consumer<ListItemClickEvent<EmbyMediaModel>> onClick = event -> {
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
            XGET(Navigator.class).pushPage(dstId, args);
        };
        seasonList.viewModel.onClick = onClick;
        val store = XGET(GlobalStore.class);
        for (EmbyMediaModel season : seasons) {
            ListView<EmbyMediaModel> listView = new ListView<>(context);
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

package top.ourfor.app.iplay.page.message;

import static top.ourfor.app.iplay.module.Bean.XGET;
import static top.ourfor.app.iplay.module.Bean.XWATCH;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;

import java.util.HashMap;

import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.action.SiteUpdateAction;
import top.ourfor.app.iplay.action.ThemeUpdateAction;
import top.ourfor.app.iplay.bean.INavigator;
import top.ourfor.app.iplay.common.annotation.ViewController;
import top.ourfor.app.iplay.model.MediaModel;
import top.ourfor.app.iplay.util.AnimationUtil;
import top.ourfor.app.iplay.util.DeviceUtil;
import top.ourfor.app.iplay.util.LayoutUtil;
import top.ourfor.app.iplay.store.IAppStore;
import top.ourfor.app.iplay.view.ListView;

@ViewController(name = "message_page")
public class MessagePage extends Fragment implements SiteUpdateAction, ThemeUpdateAction {
    private static ViewGroup contentView = null;
    private ListView<MediaModel> listView = null;
    private LottieAnimationView activityIndicator = null;
    private SwipeRefreshLayout swipeRefreshLayout = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = new ConstraintLayout(container.getContext());
            setupUI(container.getContext());
            bind();
        }
        return contentView;
    }

    void setupUI(Context context) {
        listView = new ListView(context);
        listView.setId(View.generateViewId());
        listView.viewModel.viewCell = MediaInlineViewCell.class;
        LayoutParams fill = LayoutUtil.fill();
        if (DeviceUtil.isTV) {
            contentView.addView(listView, fill);
        } else {
            swipeRefreshLayout = new SwipeRefreshLayout(context);
            swipeRefreshLayout.setLayoutParams(fill);
            swipeRefreshLayout.addView(listView, fill);
            swipeRefreshLayout.setOnRefreshListener(() -> {
                onSiteUpdate();
            });
            contentView.addView(swipeRefreshLayout);
        }
        activityIndicator = AnimationUtil.createActivityIndicate(context);
        contentView.addView(activityIndicator);
    }

    void bind() {
        XWATCH(ThemeUpdateAction.class, this);
        listView.viewModel.onClick = e -> {
            val model = e.getModel();
            if (model.isEpisode() || model.isMovie()) {
                val args = new HashMap<String, Object>();
                args.put("id", model.getId());
                args.put("title", model.getName());
                args.put("type", model.getType());
                XGET(INavigator.class).pushPage(R.id.mediaPage, args);
            }
        };
        XWATCH(SiteUpdateAction.class, this);
        activityIndicator.post(() -> {
            activityIndicator.setVisibility(View.VISIBLE);
        });
        onSiteUpdate();
    }

    @Override
    public void onSiteUpdate() {
        val store = XGET(IAppStore.class);
        store.getResume(resumes -> {
            if (resumes == null) {
                stopRefresh();
                return;
            }
            listView.setItems(resumes);
            stopRefresh();
        });
    }

    void stopRefresh() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.post(() -> {
                swipeRefreshLayout.setRefreshing(false);
            });
        }
        activityIndicator.post(() -> {
            activityIndicator.setVisibility(View.GONE);
        });
    }

    @Override
    public void themeDidUpdate() {
        contentView = null;
    }
}

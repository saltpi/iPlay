package top.ourfor.app.iplayx.page.search;

import static top.ourfor.app.iplayx.module.Bean.XGET;
import static top.ourfor.app.iplayx.module.Bean.XWATCH;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuItemCompat;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.action.AnimationAction;
import top.ourfor.app.iplayx.action.SiteUpdateAction;
import top.ourfor.app.iplayx.action.ThemeUpdateAction;
import top.ourfor.app.iplayx.bean.Navigator;
import top.ourfor.app.iplayx.common.model.ColorScheme;
import top.ourfor.app.iplayx.databinding.SearchPageBinding;
import top.ourfor.app.iplayx.page.Page;
import top.ourfor.app.iplayx.util.DeviceUtil;
import top.ourfor.app.iplayx.util.NavigationUtil;
import top.ourfor.app.iplayx.model.EmbyMediaModel;
import top.ourfor.app.iplayx.common.type.MediaLayoutType;
import top.ourfor.app.iplayx.page.home.MediaViewCell;
import top.ourfor.app.iplayx.store.GlobalStore;
import top.ourfor.app.iplayx.util.WindowUtil;
import top.ourfor.app.iplayx.view.FlexLayoutManager;
import top.ourfor.app.iplayx.view.ListView;
import top.ourfor.app.iplayx.view.TagView;
import top.ourfor.app.iplayx.view.infra.Toolbar;
import top.ourfor.app.iplayx.view.infra.ToolbarAction;

public class SearchPage implements SiteUpdateAction, ThemeUpdateAction, Page {
    @Getter
    Context context;
    GlobalStore store;
    SearchViewModel viewModel;
    SearchPageBinding binding = null;
    AnimationAction activityIndicator;
    ListView<EmbyMediaModel> listView = null;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        binding = SearchPageBinding.inflate(LayoutInflater.from(context));
        store = XGET(GlobalStore.class);
        val view = binding.getRoot();
        viewModel = new SearchViewModel(store);
        viewModel.getIsLoading().observe(view, isLoading -> {
            if (activityIndicator == null) return;
            if (isLoading) {
                activityIndicator.setVisibility(View.VISIBLE);
                activityIndicator.playAnimation();
            } else {
                activityIndicator.setVisibility(View.GONE);
                activityIndicator.cancelAnimation();
            }
        });

        viewModel.getSuggestionItems().observe(view, items -> {
            if (binding == null) return;
            if (items != null) {
                val keys = ColorScheme.shared.getScheme().keySet().toArray();
                AtomicInteger idx = new AtomicInteger();
                val padding = DeviceUtil.dpToPx(5);
                val tagViews = items.stream().map(item -> {
                    val key = keys[idx.getAndIncrement() % keys.length];
                    val color = ColorScheme.shared.getScheme().get(key);
                    val textView = new TagView(getContext());
                    textView.setId(View.generateViewId());
                    textView.setText(item.getName());
                    textView.setColor(color);
                    textView.setOnClickListener(v -> {
                        val keyword = item.getName();
                        binding.searchView.setQuery(keyword, true);
                        viewModel.getKeyword().postValue(keyword);
                    });
                    val layout = new FlexboxLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                    layout.setFlexShrink(0);
                    layout.setFlexGrow(0);
                    layout.leftMargin = padding;
                    layout.topMargin = padding;
                    textView.setLayoutParams(layout);
                    idx.getAndIncrement();
                    return textView;
                }).collect(Collectors.toCollection(ArrayList::new));
                binding.suggestionView.post(() -> tagViews.forEach(tagView -> binding.suggestionView.addView(tagView)));
            }
        });

        viewModel.getSearchResult().observe(view, medias -> {
            if (medias == null) {
                return;
            }
            medias.forEach(media -> media.setLayoutType(MediaLayoutType.Backdrop));
            listView.setItems(medias);
        });

        viewModel.getIsSearchTipVisible().observe(view, isVisible -> {
            if (binding == null) return;
            binding.suggestionView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        });

        viewModel.getKeyword().observe(view, keyword -> {
            if (binding == null) return;
            search();
        });
    }

    private void toggleSuggestion() {
        val suggestionView = binding.suggestionView;
        val isHidden = suggestionView.getAlpha() == 0;

        if (isHidden) {
            viewModel.getIsSearchTipVisible().postValue(true);
            suggestionView.animate()
                    .translationY(0)
                    .alpha(1)
                    .setDuration(300)
                    .start();
        } else {
            suggestionView.animate()
                    .translationY(-suggestionView.getHeight())
                    .alpha(0)
                    .setDuration(300)
                    .withEndAction(() -> viewModel.getIsSearchTipVisible().postValue(false))
                    .start();
        }
    }

    @Override
    public void viewDidAppear() {
        val toolbar = XGET(Toolbar.class);
        toolbar.inflateMenu(R.menu.search_menu, ToolbarAction.Position.Right);
        toolbar.inflateMenu(R.menu.search_left_menu, ToolbarAction.Position.Left);
        val animationItem = toolbar.getLeftMenu().findItem(R.id.loading);
        val actionProvider = MenuItemCompat.getActionProvider(animationItem);
        activityIndicator = (AnimationAction)actionProvider;
        activityIndicator.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val color = getContext().getColor(R.color.onBackground);
            val menu = toolbar.getMenu();
            val size = menu.size();
            val tint = ColorStateList.valueOf(color);
            for (int i = 0; i < size; i++) {
                menu.getItem(i).setIconTintList(tint);
            }
        }
        toolbar.setOnMenuItemClickListener(item -> {
            val itemId = item.getItemId();
            if (itemId == R.id.toggle_suggestion) {
                toggleSuggestion();
            }
            return true;
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setupUI(getContext());
        bind(savedInstanceState);
        return binding.getRoot();
    }

    void setupUI(Context context) {
        listView = new ListView<>(getContext());
        listView.setId(View.generateViewId());
        FlexLayoutManager layoutManager = new FlexLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.SPACE_AROUND);
        listView.listView.setLayoutManager(layoutManager);
        listView.viewModel.viewCell = MediaViewCell.class;
        listView.setEmptyTipVisible(View.VISIBLE);
        binding.listContainer.addView(listView, new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
        binding.getRoot().setPadding(0, WindowUtil.defaultToolbarBottom, 0, 0);
    }

    void bind(Bundle savedInstanceState) {
        XWATCH(ThemeUpdateAction.class, this);
        XWATCH(SiteUpdateAction.class, this);
        listView.viewModel.onClick = event -> {
            val model = event.getModel();
            val args = new HashMap<String, Object>();
            args.put("id", model.getId());
            args.put("title", model.getName());
            args.put("type", model.getType());
            XGET(Navigator.class).pushPage(R.id.mediaPage, args);
        };
        binding.searchView.setIconifiedByDefault(false);
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.getKeyword().postValue(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.getKeyword().postValue(newText);
                return false;
            }
        });
        if (savedInstanceState == null) {
            viewModel.fetchSearchSuggestion();
        }
    }

    void search() {
        viewModel.search(viewModel.getKeyword().getValue());
    }

    @Override
    public void onSiteUpdate() {
        val keyword = viewModel.getKeyword().getValue();
        if (keyword == null || keyword.isEmpty()) return;
        binding.suggestionView.post(() -> binding.suggestionView.removeAllViews());
        viewModel.fetchSearchSuggestion(true);
        store.search(keyword, medias -> {
            if (medias == null) return;
            medias.forEach(media -> media.setLayoutType(MediaLayoutType.Backdrop));
            listView.setItems(medias);
        });
    }

    public void onDestroyView() {
        binding = null;
    }

    @Override
    public void create(Context context, Map<String, Object> params) {
        this.context = context;
        onCreate(null);
        onCreateView(LayoutInflater.from(context), null, null);
    }

    @Override
    public View view() {
        return binding.getRoot();
    }
}

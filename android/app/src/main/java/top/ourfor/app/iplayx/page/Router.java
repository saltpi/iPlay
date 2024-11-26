package top.ourfor.app.iplayx.page;

import static top.ourfor.app.iplayx.module.Bean.XGET;
import static top.ourfor.app.iplayx.page.PageMaker.makePage;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;

import androidx.appcompat.app.ActionBar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.action.NavigationTitleBar;
import top.ourfor.app.iplayx.bean.Navigator;
import top.ourfor.app.iplayx.bean.PageLifecycle;
import top.ourfor.app.iplayx.store.GlobalStore;
import top.ourfor.app.iplayx.util.LayoutUtil;
import top.ourfor.app.iplayx.view.infra.Toolbar;

public class Router implements Navigator {
    private static final HashMap<Integer, PageType> pageType;
    private static Map<Integer, Stack<Page>> navigators;
    private static Integer stackId = null;
    private static final Map<Page, Integer> pageId = new HashMap<>();

    Interpolator interpolator = new AccelerateDecelerateInterpolator();
    long duration = 300;

    ViewGroup container;
    BottomNavigationView bottomNavigation;
    Toolbar toolbar;

    static {
        pageType = new HashMap<>();
        pageType.put(R.id.homePage, PageType.HOME);
        pageType.put(R.id.messagePage, PageType.MESSAGE);
        pageType.put(R.id.filePage, PageType.FILE);
        pageType.put(R.id.starPage, PageType.STAR);
        pageType.put(R.id.settingPage, PageType.SETTING);
        pageType.put(R.id.searchPage, PageType.SEARCH);
        pageType.put(R.id.loginPage, PageType.LOGIN);
        pageType.put(R.id.themePage, PageType.THEME);
        pageType.put(R.id.videoPage, PageType.VIDEO_CONFIG);
        pageType.put(R.id.audioPage, PageType.AUDIO_CONFIG);
        pageType.put(R.id.playerPage, PageType.PLAYER);
        pageType.put(R.id.musicPage, PageType.MUSIC);
        pageType.put(R.id.musicPlayerPage, PageType.MUSIC_PLAYER);
        pageType.put(R.id.webPage, PageType.WEB);
    }

    Router(ViewGroup container, BottomNavigationView bottomNavigation, Toolbar toolbar) {
        this.container = container;
        this.bottomNavigation = bottomNavigation;
        this.toolbar = toolbar;
        navigators = new HashMap<>();
        bottomNavigation.setOnItemSelectedListener(item -> {
            navigate(item.getItemId());
            return true;
        });
    }

    void navigate(int id) {
        var pages = navigators.computeIfAbsent(id, k -> new Stack<>());
        if (pages.isEmpty()) {
            val page = makePage(id);
            page.create(container.getContext(), new HashMap<>());
            if (page.view() instanceof PageLifecycle lifecycle) {
                lifecycle.onAttach();
            }
            pageId.put(page, id);
            pages.push(page);
        }
        val page = pages.peek();
        container.removeAllViews();
        val view = page.view();
        page.viewWillAppear();
        container.addView(view, LayoutUtil.fill());
        onNavigateChange(id);
        page.viewDidAppear();
        stackId = id;
    }

    @Override
    public void pushPage(int id, Map<String, Object> params) {
        val pages = navigators.computeIfAbsent(stackId, k -> new Stack<>());
        if (pages.isEmpty()) return;
        val page = pages.peek();
        page.viewWillDisappear();
        val newPage = makePage(id);
        pageId.put(newPage, id);
        newPage.create(container.getContext(), params);
        pages.push(newPage);
        val view = newPage.view();
        view.setBackgroundResource(R.drawable.bg);
        newPage.viewWillAppear();
        container.addView(view, LayoutUtil.fill());
        if (view instanceof PageLifecycle lifecycle) {
            lifecycle.onAttach();
        }
        pushPageAnimation(page, newPage);
        onNavigateChange(id);
    }

    @Override
    public boolean popPage() {
        val pages = navigators.computeIfAbsent(stackId, k -> new Stack<>());
        if (pages.isEmpty()) return false;
        val page = pages.pop();
        pageId.remove(page);
        page.viewWillDisappear();
        if (page.view() instanceof PageLifecycle lifecycle) {
            lifecycle.onDetach();
        }
        val newPage = pages.peek();
        val view = newPage.view();
        newPage.viewWillAppear();
        container.addView(view, 0, LayoutUtil.fill());
        popPageAnimation(page, newPage);
        onNavigateChange(pageId.get(newPage));
        newPage.viewDidAppear();
        return true;
    }

    void onNavigateChange(int id) {
        val actionBar = XGET(ActionBar.class);
        val toolbar = XGET(Toolbar.class);
        toolbar.invalidateMenu();
        toolbar.clear();
        val page = pageType.getOrDefault(id, PageType.NONE);
        int title = switch (page) {
            case HOME -> R.string.menu_home;
            case MESSAGE -> R.string.menu_message;
            case FILE -> R.string.menu_file;
            case STAR -> R.string.menu_star;
            case SETTING -> R.string.menu_setting;
            case SEARCH -> R.string.menu_search;
            case LOGIN -> R.string.login;
            case THEME -> R.string.setting_item_theme;
            case VIDEO_CONFIG -> R.string.setting_item_video;
            case AUDIO_CONFIG -> R.string.setting_item_audio;
            case IMAGE_CONFIG -> R.string.setting_item_picture;
            case CLOUD -> R.string.setting_item_cloud;
            case ABOUT -> R.string.setting_item_about;
            case CACHE -> R.string.setting_item_cache;
            default -> R.string.menu_unknown;
        };
        if (title != R.string.menu_unknown) {
            if (page == PageType.HOME) {
                val store = XGET(GlobalStore.class);
                XGET(NavigationTitleBar.class).setNavTitle(store.getSiteName());
            } else {
                XGET(NavigationTitleBar.class).setNavTitle(title);
            }
        }
        switch (page) {
            case HOME, SEARCH, SETTING, STAR, FILE ->  {
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.show();
                bottomNavigation.setVisibility(View.VISIBLE);
            }
            default -> {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.show();
                bottomNavigation.setVisibility(View.GONE);
            }
        }

        if (page == PageType.PLAYER) {
            toolbar.setVisibility(View.GONE);
        } else {
            toolbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean canGoBack() {
        if (stackId == null) return false;
        if (!navigators.containsKey(stackId)) return false;
        return navigators.get(stackId).size() > 1;
    }

    @Override
    public Page getCurrentPage() {
        if (stackId == null) return null;
        return navigators.get(stackId).peek();
    }

    @Override
    public int getCurrentPageId() {
        if (stackId == null) return 0;
        val page = navigators.get(stackId).peek();
        return pageId.get(page);
    }

    void pushPageAnimation(Page oldPage, Page newPage) {
        newPage.view().setTranslationX(oldPage.view().getWidth());
        newPage.view().animate()
                .translationX(0)
                .setDuration(duration)
                .setInterpolator(interpolator)
                .withEndAction(() -> {
                    container.removeView(oldPage.view());
                    oldPage.viewDidDisappear();
                    newPage.viewDidAppear();
                })
                .start();
    }

    void popPageAnimation(Page oldPage, Page newPage) {
        oldPage.view().animate()
                .translationX(oldPage.view().getWidth())
                .setDuration(duration)
                .setInterpolator(interpolator)
                .withEndAction(() -> {
                    container.removeView(oldPage.view());
                    oldPage.viewDidDisappear();
                    newPage.viewDidAppear();
                })
                .start();
    }
}

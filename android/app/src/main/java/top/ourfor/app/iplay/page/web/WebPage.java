package top.ourfor.app.iplay.page.web;

import static top.ourfor.app.iplay.module.Bean.XGET;
import static top.ourfor.app.iplay.module.Bean.XWATCH;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.action.ThemeUpdateAction;
import top.ourfor.app.iplay.bean.IJSONAdapter;
import top.ourfor.app.iplay.bean.IKVStorage;
import top.ourfor.app.iplay.bean.INavigator;
import top.ourfor.app.iplay.common.annotation.ViewController;
import top.ourfor.app.iplay.config.AppSetting;
import top.ourfor.app.iplay.databinding.WebPageBinding;
import top.ourfor.app.iplay.page.Activity;
import top.ourfor.app.iplay.page.Page;
import top.ourfor.app.iplay.store.IAppStore;
import top.ourfor.app.iplay.util.AnimationUtil;
import top.ourfor.app.iplay.util.DeviceUtil;
import top.ourfor.app.iplay.util.WindowUtil;
import top.ourfor.app.iplay.view.infra.Toolbar;
import top.ourfor.app.iplay.view.infra.ToolbarAction;

@Slf4j
@ViewController(name = "web_page")
public class WebPage implements ThemeUpdateAction, Page {
    static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36 Edg/131.0.0.0";
    IAppStore store;
    @Getter
    Context context;
    WebPageViewModel viewModel;
    WebPageBinding binding;

    private BottomSheetDialog dialog = null;
    private LottieAnimationView activityIndicator = null;
    private SwipeRefreshLayout swipeRefreshLayout = null;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    public void init() {
        binding = WebPageBinding.inflate(LayoutInflater.from(context), null, false);
        store = XGET(IAppStore.class);
        val view = binding.getRoot();
        viewModel = new WebPageViewModel(store);
        viewModel.getIsLoading().observe(view, isLoading -> {
            if (activityIndicator == null) return;
            if (isLoading) {
                activityIndicator.setVisibility(View.VISIBLE);
                activityIndicator.playAnimation();
            } else {
                activityIndicator.setVisibility(View.GONE);
                activityIndicator.cancelAnimation();
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });


        binding.webview.loadUrl(AppSetting.shared.webHomePage);
        var webview = binding.webview;
        var settings = webview.getSettings();
        settings.setUserAgentString(USER_AGENT);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setDisplayZoomControls(false);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webview.addJavascriptInterface(this, "bridge");
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                injectJs();
                binding.swipeRefresh.setRefreshing(false);
                binding.backward.post(() -> binding.backward.setVisibility(webview.canGoBack() ? View.VISIBLE : View.GONE));
            }

        });

        binding.backward.setOnClickListener(v -> {
            if (webview.canGoBack()) {
                webview.goBack();
            }
        });

        binding.scrollTop.setOnClickListener(v -> {
            if (webview.canScrollVertically(-1)) {
                webview.scrollTo(0, 0);
            }
        });
    }

    @JavascriptInterface
    public void postMessage(String msg) {
        log.info("message: {}", msg);
        var model = XGET(IJSONAdapter.class).fromJSON(msg, new TypeReference<WebScriptMessage<String>>() { });
        log.info(model.getType());
        if (model.getType().equals("play")) {
            val route = XGET(INavigator.class);
            val args = new HashMap<String, Object>();
            args.put("url", model.getData());
            XGET(Activity.class).runOnUiThread(() -> {
                route.pushPage(R.id.playerPage, args);
            });
        }
    }

    void injectJs() {
        var webview = binding.webview;
        var script = XGET(IKVStorage.class).get("@script");
        if (script == null) return;
        XGET(Activity.class).runOnUiThread(() -> {
            webview.loadUrl("javascript:" + script);
        });
    }

    @Override
    public void viewDidAppear() {
        val toolbar = XGET(Toolbar.class);
        toolbar.inflateMenu(R.menu.web_menu, ToolbarAction.Position.Right);
        toolbar.inflateMenu(R.menu.web_left_menu, ToolbarAction.Position.Left);
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
            if (itemId == R.id.add_script) {
                showScriptPanel();
            } else if (itemId == R.id.refresh) {
                binding.webview.reload();
            }
            return true;
        });
    }

    public void setup() {
        setupUI(context);
        bind();
    }

    void setupUI(Context context) {
        activityIndicator = AnimationUtil.createActivityIndicate(context);
        activityIndicator.setVisibility(View.GONE);
        binding.backward.setVisibility(View.GONE);
        binding.getRoot().addView(activityIndicator);
        binding.getRoot().setPadding(0, WindowUtil.defaultToolbarBottom, 0, 0);
        if (!DeviceUtil.isTV) {
            swipeRefreshLayout = binding.swipeRefresh;
            swipeRefreshLayout.setOnRefreshListener(() -> {
                binding.webview.reload();
            });
        }

    }

    void bind() {
        XWATCH(ThemeUpdateAction.class, this);
    }

    void showScriptPanel() {
        dialog = new BottomSheetDialog(getContext(), R.style.SiteBottomSheetDialog);
        dialog.setOnDismissListener(dlg -> { });
        var view = new ScriptManageView(context);
        view.setOnSaveButtonClick(v -> dialog.dismiss());
        dialog.setContentView(view);
        var behavior = BottomSheetBehavior.from((View) view.getParent());
        val height = (int) (DeviceUtil.screenSize(getContext()).getHeight() * 0.6);
        behavior.setPeekHeight(height);
        dialog.show();
    }

    public void onDestroyView() {
        binding = null;
    }

    @Override
    public void create(Context context, Map<String, Object> params) {
        this.context = context;
        init();
        setup();
    }

    @Override
    public View view() {
        return this.binding.getRoot();
    }

    @Override
    public int id() {
        return R.id.webPage;
    }
}


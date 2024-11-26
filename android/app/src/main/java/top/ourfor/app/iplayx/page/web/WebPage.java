package top.ourfor.app.iplayx.page.web;

import static top.ourfor.app.iplayx.module.Bean.XGET;
import static top.ourfor.app.iplayx.module.Bean.XWATCH;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.action.ThemeUpdateAction;
import top.ourfor.app.iplayx.bean.JSONAdapter;
import top.ourfor.app.iplayx.bean.KVStorage;
import top.ourfor.app.iplayx.bean.Navigator;
import top.ourfor.app.iplayx.config.AppSetting;
import top.ourfor.app.iplayx.databinding.WebPageBinding;
import top.ourfor.app.iplayx.page.Activity;
import top.ourfor.app.iplayx.page.Page;
import top.ourfor.app.iplayx.page.setting.common.ScriptManageView;
import top.ourfor.app.iplayx.page.setting.common.WebScriptMessage;
import top.ourfor.app.iplayx.store.GlobalStore;
import top.ourfor.app.iplayx.util.AnimationUtil;
import top.ourfor.app.iplayx.util.DeviceUtil;
import top.ourfor.app.iplayx.util.WindowUtil;
import top.ourfor.app.iplayx.view.infra.Button;
import top.ourfor.app.iplayx.view.infra.EditText;
import top.ourfor.app.iplayx.view.infra.TextView;
import top.ourfor.app.iplayx.view.infra.Toolbar;
import top.ourfor.app.iplayx.view.infra.ToolbarAction;

@Slf4j
public class WebPage implements ThemeUpdateAction, Page {
    static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36 Edg/131.0.0.0";
    GlobalStore store;
    @Getter
    Context context;
    WebPageViewModel viewModel;
    WebPageBinding binding;

    private BottomSheetDialog dialog = null;
    private LottieAnimationView activityIndicator = null;
    private SwipeRefreshLayout swipeRefreshLayout = null;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    public void onCreate(@Nullable Bundle savedInstanceState) {
        binding = WebPageBinding.inflate(LayoutInflater.from(context), null, false);
        store = XGET(GlobalStore.class);
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
        var model = XGET(JSONAdapter.class).fromJSON(msg, new TypeReference<WebScriptMessage<String>>() { });
        log.info(model.getType());
        if (model.getType().equals("play")) {
            val route = XGET(Navigator.class);
            val args = new HashMap<String, Object>();
            args.put("url", model.getData());
            XGET(Activity.class).runOnUiThread(() -> {
                route.pushPage(R.id.playerPage, args);
            });
        }
    }

    void injectJs() {
        var webview = binding.webview;
        var script = XGET(KVStorage.class).get("@script");
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

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setupUI(getContext());
        bind();
        return binding.getRoot();
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
        val drive = store.getDrive();
        dialog = new BottomSheetDialog(getContext(), R.style.SiteBottomSheetDialog);
        dialog.setOnDismissListener(dlg -> {
        });
        var view = new ScriptManageView(context);
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
        onCreate(null);
        onCreateView(LayoutInflater.from(context), null, null);
    }

    @Override
    public View view() {
        return this.binding.getRoot();
    }
}


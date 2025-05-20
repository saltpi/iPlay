package top.ourfor.app.iplay.page.setting.site;

import static top.ourfor.app.iplay.module.Bean.XGET;
import static top.ourfor.app.iplay.module.Bean.XWATCH;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplay.action.DispatchAction;
import top.ourfor.app.iplay.action.SiteLineUpdateAction;
import top.ourfor.app.iplay.common.model.SiteEndpointModel;
import top.ourfor.app.iplay.databinding.SiteLineManageBinding;
import top.ourfor.app.iplay.model.SiteLineModel;
import top.ourfor.app.iplay.store.IAppStore;
import top.ourfor.app.iplay.view.ListView;

@Slf4j
public class SiteLineManageView extends ConstraintLayout implements SiteLineUpdateAction {
    private SiteLineModel model;
    SiteLineManageBinding binding = null;
    private ListView<SiteLineModel> siteLineListView = null;
    @Getter @Setter
    private boolean showDelay = false;
    @Getter @Setter
    private boolean showUrl = true;


    public SiteLineManageView(@NonNull Context context) {
        super(context);
        binding = SiteLineManageBinding.inflate(LayoutInflater.from(context), this, true);
        setupUI(context);
        bind();
    }

    void setupUI(Context context) {
        val layout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layout.leftToLeft = LayoutParams.PARENT_ID;
        layout.rightToRight = LayoutParams.PARENT_ID;
        siteLineListView = new ListView<>(getContext());
        binding.listContainer.addView(siteLineListView);
        setLayoutParams(layout);
    }

    public void loadData() {
        val store = XGET(IAppStore.class);
        val site = store.getSite();
        if (site == null) return;
        var items = new ArrayList<SiteLineModel>();
        var endpoints = site.getEndpoints();
        if (endpoints == null) {
            endpoints = new ArrayList<>();
            val aDefault = SiteLineModel.builder()
                    .endpoint(site.getEndpoint())
                    .remark("Default")
                    .build();
            items.add(aDefault);
        } else {
            items.addAll(endpoints);
        }
        items.forEach(item -> {
            item.setShowDelay(showDelay);
            item.setShowUrl(showUrl);
        });
        siteLineListView.viewModel.isSelected = (model) -> model.getEndpoint().getBaseUrl().equals(site.getEndpoint().getBaseUrl());
        siteLineListView.setItems(items);
    }

    void bind() {
        XWATCH(SiteLineUpdateAction.class, this);
        siteLineListView.viewModel.viewCell = SiteLineViewCell.class;
        val listViewLayout = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        listViewLayout.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        listViewLayout.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
        siteLineListView.setLayoutParams(listViewLayout);
        siteLineListView.viewModel.onClick = e -> {
            XGET(DispatchAction.class).runOnUiThread(() -> {
                val IAppStore = XGET(IAppStore.class);
                val remark = IAppStore.getSite().getEndpoint().getRemark();
                val endpoint = e.getModel().getEndpoint();
                IAppStore.getSite().setEndpoint(endpoint.withRemark(remark));
                IAppStore.save();
            });
        };

        loadData();
        siteLineListView.setOnTouchListener((v1, event) -> {
            if (!siteLineListView.listView.canScrollVertically(-1)) {
                siteLineListView.listView.requestDisallowInterceptTouchEvent(false);
            }else{
                siteLineListView.listView.requestDisallowInterceptTouchEvent(true);
            }
            return false;
        });

        binding.addLineButton.setOnClickListener(v -> {
            val server = binding.serverInput.getText().toString();
            val remark = binding.remarkInput.getText().toString();
            try {
                URL url = new URL(server);
                val endpoint = SiteEndpointModel.builder()
                        .host(url.getHost())
                        .port(url.getPort())
                        .path(url.getPath())
                        .protocol(url.getProtocol())
                        .remark(remark)
                        .build();
                val IAppStore = XGET(IAppStore.class);
                val currentSite = IAppStore.getSite();
                if (currentSite.getEndpoints() == null) {
                    currentSite.setEndpoints(new ArrayList<SiteLineModel>());
                    currentSite.getEndpoints().add(SiteLineModel.builder().endpoint(currentSite.getEndpoint()).remark("Default").build());
                }
                currentSite.getEndpoints().add(SiteLineModel.builder().endpoint(endpoint).remark(remark).build());
                IAppStore.save();
                loadData();
                binding.serverInput.setText(null);
                binding.remarkInput.setText(null);
            } catch (MalformedURLException e) {
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                throw new RuntimeException(e);
            }
        });

    }

    @Override
    public void updateSiteLines() {
        loadData();
    }
}

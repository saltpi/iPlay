package top.ourfor.app.iplayx.page.setting.site;

import static top.ourfor.app.iplayx.module.Bean.XGET;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.action.SiteLineUpdateAction;
import top.ourfor.app.iplayx.action.UpdateModelAction;
import top.ourfor.app.iplayx.common.model.ColorScheme;
import top.ourfor.app.iplayx.databinding.SiteLineCellBinding;
import top.ourfor.app.iplayx.model.SiteLineModel;
import top.ourfor.app.iplayx.store.GlobalStore;
import top.ourfor.app.iplayx.util.DeviceUtil;
import top.ourfor.app.iplayx.util.NetworkUtil;

@Slf4j
public class SiteLineViewCell extends ConstraintLayout implements UpdateModelAction {
    private SiteLineModel model;
    SiteLineCellBinding binding = null;


    public SiteLineViewCell(@NonNull Context context) {
        super(context);
        binding = SiteLineCellBinding.inflate(LayoutInflater.from(context), this, true);
        setupUI(context);
        bind();
    }

    @Override
    public <T> void updateModel(T object) {
        if (!(object instanceof SiteLineModel)) {
            return;
        }
        model = (SiteLineModel) object;

        binding.siteRemark.setText(model.getRemark());
        binding.siteUrl.setText(model.getEndpoint().getBaseUrl());
        binding.siteDelay.setTextColor(ColorScheme.shared.getScheme().get("yellow").textColor());
        binding.siteDelay.setVisibility(model.isShowDelay() ? VISIBLE : GONE);
        binding.siteUrl.setVisibility(model.isShowUrl() ? VISIBLE : GONE);

        NetworkUtil.getSiteLineInfo(model.getEndpoint().getBaseUrl(), siteInfo -> {
            if (siteInfo != null) {
                binding.siteRegion.setText(siteInfo.countryFlag);
                binding.siteDelay.setText(siteInfo.delay);
                val scheme = ColorScheme.shared;

                var delayLevelColor = -1; // yellow
                try {
                    val ms = siteInfo.delay.replace("ms", "");
                    val msDelay = Integer.parseInt(ms);
                    if (msDelay < 1000) {
                        delayLevelColor = scheme.getScheme().get("green").textColor(); // green
                    } else {
                        delayLevelColor = scheme.getScheme().get("red").textColor(); // red
                    }
                } catch (Exception e) {
                    delayLevelColor = scheme.getScheme().get("red").textColor(); // yellow
                }
                binding.siteDelay.setTextColor(delayLevelColor);
            } else {
                log.error("SiteInfo {}", "Failed to get site info");
            }
        });

    }

    @Override
    public <T> void updateSelectionState(T model, boolean selected) {
        setSelected(selected);
    }

    void setupUI(Context context) {
        val layout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layout.leftToLeft = LayoutParams.PARENT_ID;
        layout.rightToRight = LayoutParams.PARENT_ID;
        setLayoutParams(layout);
    }

    void bind() {
        binding.content.setOnClickListener(v -> callOnClick());

        binding.delete.setOnClickListener(v -> {
            val globalStore = XGET(GlobalStore.class);
            globalStore.getSite().getEndpoints().remove(model);
            globalStore.save();
            XGET(SiteLineUpdateAction.class).updateSiteLines();
        });

        if (!DeviceUtil.isTV) return;
        setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                setBackgroundResource(R.drawable.card_focus);
            } else {
                setBackgroundResource(R.drawable.card_normal);
            }
        });
    }
}

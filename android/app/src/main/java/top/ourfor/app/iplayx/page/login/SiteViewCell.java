package top.ourfor.app.iplayx.page.login;

import static top.ourfor.app.iplayx.module.Bean.XGET;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.action.DispatchAction;
import top.ourfor.app.iplayx.action.SiteUpdateAction;
import top.ourfor.app.iplayx.action.UpdateModelAction;
import top.ourfor.app.iplayx.bean.Navigator;
import top.ourfor.app.iplayx.databinding.SiteCellBinding;
import top.ourfor.app.iplayx.module.GlideApp;
import top.ourfor.app.iplayx.util.DeviceUtil;
import top.ourfor.app.iplayx.model.SiteModel;
import top.ourfor.app.iplayx.store.GlobalStore;

public class SiteViewCell extends ConstraintLayout implements UpdateModelAction {
    private SiteModel model;
    SiteCellBinding binding = null;


    public SiteViewCell(@NonNull Context context) {
        super(context);
        binding = SiteCellBinding.inflate(LayoutInflater.from(context), this, true);
        setupUI(context);
        bind();
    }

    @Override
    public <T> void updateModel(T object) {
        if (!(object instanceof SiteModel)) {
            return;
        }
        model = (SiteModel) object;
        val showSensitive = model.isShowSensitive();
        if (showSensitive) {
            binding.siteUrl.setText(showSensitive ? model.getEndpoint().getBaseUrl() : "");
            binding.siteUsername.setText(showSensitive ? model.getUserName() : "");
        } else {
            val params = (ConstraintLayout.LayoutParams) binding.siteImage.getLayoutParams();
            params.bottomToBottom = LayoutParams.PARENT_ID;
        }
        val visibility = showSensitive ? VISIBLE : GONE;
        binding.siteUrl.setVisibility(visibility);
        binding.siteUsername.setVisibility(visibility);
        var remark = model.getEndpoint().getRemark();
        if (remark == null) {
            remark = model.getUser().getServerId();
        }
        binding.siteRemark.setText(remark);
        GlideApp.with(this)
                .load(model.avatarUrl())
                .placeholder(R.drawable.avatar)
                .into(binding.siteImage);
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

        binding.delete.setOnClickListener(v -> XGET(GlobalStore.class).removeSite(model));
        boolean allowModify = XGET(Navigator.class).getCurrentPageId() == R.id.sitePage;
        binding.modify.setVisibility(allowModify ? VISIBLE : GONE);
        binding.modify.setOnClickListener(v -> {
            val dst = XGET(Navigator.class).getCurrentPageId();
            if (dst == R.id.sitePage) {
                val action = XGET(SiteUpdateAction.class);
                if (action == null) return;
                XGET(DispatchAction.class).runOnUiThread(() -> action.onSiteModify(model));
            } else {
                Toast.makeText(getContext(), getContext().getString(R.string.modify_at_site_page), Toast.LENGTH_SHORT).show();
            }
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

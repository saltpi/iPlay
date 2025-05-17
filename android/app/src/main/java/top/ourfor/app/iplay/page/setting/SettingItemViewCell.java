package top.ourfor.app.iplay.page.setting;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.action.UpdateModelAction;
import top.ourfor.app.iplay.databinding.SettingMenuCellBinding;
import top.ourfor.app.iplay.util.DeviceUtil;

public class SettingItemViewCell extends ConstraintLayout implements UpdateModelAction {
    private SettingItemModel model;
    SettingMenuCellBinding binding = null;

    public SettingItemViewCell(@NonNull Context context) {
        super(context);
        binding = SettingMenuCellBinding.inflate(LayoutInflater.from(context), this, true);
        val layout = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layout);
        setupUI(context);
        bind();
    }


    @Override
    public <T> void updateModel(T model) {
        if (!(model instanceof SettingItemModel)) {
            return;
        }
        this.model = (SettingItemModel) model;
        binding.nameLabel.setText(this.model.getTitle());
        val drawable = getContext().getResources().getDrawable(this.model.getIcon(), null);
        // tint drawable
        binding.settingIcon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.onBackground)));
        binding.settingIcon.setImageDrawable(drawable);
    }

    void setupUI(Context context) {
        LayoutParams layout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layout.topMargin = DeviceUtil.dpToPx(5);
        layout.bottomMargin = DeviceUtil.dpToPx(5);
        setLayoutParams(layout);
    }

    private void bind() {
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

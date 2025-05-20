package top.ourfor.app.iplay.page.setting.theme;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.action.UpdateModelAction;
import top.ourfor.app.iplay.common.model.HomeTabModel;
import top.ourfor.app.iplay.databinding.HomeTabEditCellBinding;
import top.ourfor.app.iplay.databinding.SiteLineCellBinding;
import top.ourfor.app.iplay.util.DeviceUtil;

@Slf4j
public class HomeTabEditViewCell extends ConstraintLayout implements UpdateModelAction {
    HomeTabModel model;
    HomeTabEditCellBinding binding = null;


    public HomeTabEditViewCell(@NonNull Context context) {
        super(context);
        binding = HomeTabEditCellBinding.inflate(LayoutInflater.from(context), this, true);
        binding.getRoot().setOnClickListener(v -> callOnClick());
        setupUI(context);
        bind();
    }

    @Override
    public <T> void updateModel(T object) {
        if (!(object instanceof HomeTabModel)) {
            return;
        }
        model = (HomeTabModel) object;
        binding.menuTitle.setText(model.getTitle());
        binding.menuIcon.setImageResource(model.getIcon());
    }

    @Override
    public <T> void updateSelectionState(T model, boolean selected) {
        if (selected) {
            binding.indicator.setImageResource(com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_checkmark_square_24_filled);
        } else {
            binding.indicator.setImageResource(com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_square_24_filled);
        }
    }

    void setupUI(Context context) {
        val layout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layout.leftToLeft = LayoutParams.PARENT_ID;
        layout.rightToRight = LayoutParams.PARENT_ID;
        setLayoutParams(layout);
    }

    void bind() {
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

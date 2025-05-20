package top.ourfor.app.iplay.page.file;

import static top.ourfor.app.iplay.module.Bean.XGET;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.action.DispatchAction;
import top.ourfor.app.iplay.action.DriveUpdateAction;
import top.ourfor.app.iplay.action.UpdateModelAction;
import top.ourfor.app.iplay.bean.INavigator;
import top.ourfor.app.iplay.databinding.DriveCellBinding;
import top.ourfor.app.iplay.model.drive.Drive;
import top.ourfor.app.iplay.store.IAppStore;
import top.ourfor.app.iplay.util.DeviceUtil;

public class DriveViewCell extends ConstraintLayout implements UpdateModelAction {
    private Drive model;
    DriveCellBinding binding = null;


    public DriveViewCell(@NonNull Context context) {
        super(context);
        binding = DriveCellBinding.inflate(LayoutInflater.from(context), this, true);
        setupUI(context);
        bind();
    }

    @Override
    public <T> void updateModel(T object) {
        if (!(object instanceof Drive)) {
            return;
        }
        model = (Drive) object;
        binding.siteRemark.setText(model.getRemark());
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
        binding.delete.setOnClickListener(v -> XGET(IAppStore.class).removeDrive(model));
        binding.modify.setOnClickListener(v -> {
            val id = XGET(INavigator.class).getCurrentPageId();
            if (id == R.id.sitePage) {
                val action = XGET(DriveUpdateAction.class);
                if (action == null) return;
                XGET(DispatchAction.class).runOnUiThread(() -> action.onDriveUpdate(model));
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

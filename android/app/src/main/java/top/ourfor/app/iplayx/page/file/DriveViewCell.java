package top.ourfor.app.iplayx.page.file;

import static top.ourfor.app.iplayx.module.Bean.XGET;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.action.DispatchAction;
import top.ourfor.app.iplayx.action.DriveUpdateAction;
import top.ourfor.app.iplayx.action.UpdateModelAction;
import top.ourfor.app.iplayx.bean.Navigator;
import top.ourfor.app.iplayx.databinding.DriveCellBinding;
import top.ourfor.app.iplayx.model.drive.Drive;
import top.ourfor.app.iplayx.store.GlobalStore;
import top.ourfor.app.iplayx.util.DeviceUtil;

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
        binding.delete.setOnClickListener(v -> XGET(GlobalStore.class).removeDrive(model));
        binding.modify.setOnClickListener(v -> {
            val id = XGET(Navigator.class).getCurrentPageId();
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

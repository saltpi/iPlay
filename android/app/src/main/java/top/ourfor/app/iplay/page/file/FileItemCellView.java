package top.ourfor.app.iplay.page.file;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.action.UpdateModelAction;
import top.ourfor.app.iplay.api.file.File;
import top.ourfor.app.iplay.api.file.FileType;
import top.ourfor.app.iplay.databinding.FileItemCellBinding;
import top.ourfor.app.iplay.util.DeviceUtil;

public class FileItemCellView extends ConstraintLayout implements UpdateModelAction {

    FileItemCellBinding binding;

    public FileItemCellView(@NonNull Context context) {
        super(context);
        val layout = new ConstraintLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(layout);
        binding = FileItemCellBinding.inflate(LayoutInflater.from(context), this, true);

        if (DeviceUtil.isTV) {
            setFocusable(true);
            setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    v.setBackground(ContextCompat.getDrawable(context, R.drawable.card_focus));
                } else {
                    v.setBackground(ContextCompat.getDrawable(context, R.drawable.card_normal));
                }
            });
        }
    }

    @Override
    public <T> void updateModel(T model) {
        if (!(model instanceof File)) {
            return;
        }
        File file = (File) model;
        binding.fileName.setText(file.getName());
        binding.rightArrow.setVisibility(file.getType().equals(FileType.DIRECTORY) ? VISIBLE : GONE);
        binding.fileIcon.setImageResource(file.getType().equals(FileType.DIRECTORY) ? R.drawable.folder : R.drawable.file);
    }
}

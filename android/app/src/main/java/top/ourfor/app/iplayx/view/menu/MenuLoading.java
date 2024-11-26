package top.ourfor.app.iplayx.view.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ActionProvider;

import lombok.val;
import top.ourfor.app.iplayx.action.AnimationAction;
import top.ourfor.app.iplayx.databinding.MenuLoadingBinding;
import top.ourfor.app.iplayx.util.DeviceUtil;

public class MenuLoading extends ActionProvider implements AnimationAction {
    Context context;
    MenuLoadingBinding binding;

    public MenuLoading(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @NonNull
    @Override
    public View onCreateActionView() {
        int size = DeviceUtil.dpToPx(40);
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = MenuLoadingBinding.inflate(inflater);
        val layoutParams = new ViewGroup.LayoutParams(size, size);
        binding.getRoot().setVisibility(View.GONE);
        binding.getRoot().setLayoutParams(layoutParams);
        return binding.getRoot();
    }

    @Override
    public void playAnimation() {
        binding.getRoot().post(() -> {
            binding.getRoot().setVisibility(View.VISIBLE);
            binding.loading.playAnimation();
        });
    }

    @Override
    public void cancelAnimation() {
        binding.getRoot().post(() -> {
            binding.getRoot().setVisibility(View.GONE);
            binding.loading.cancelAnimation();
        });
    }

    @Override
    public void setVisibility(int visibility) {
        binding.getRoot().post(() -> binding.getRoot().setVisibility(visibility));
    }

    @Override
    public void post(Runnable runnable) {
        binding.loading.post(runnable);
    }
}

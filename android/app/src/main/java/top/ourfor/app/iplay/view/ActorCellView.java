package top.ourfor.app.iplay.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import lombok.extern.slf4j.Slf4j;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.action.UpdateModelAction;
import top.ourfor.app.iplay.databinding.ActorCellBinding;
import top.ourfor.app.iplay.model.ActorModel;
import top.ourfor.app.iplay.module.GlideApp;

@Slf4j
public class ActorCellView extends ConstraintLayout implements UpdateModelAction {
    private static final RequestOptions options = new RequestOptions().transform(new RoundedCorners(20));

    ActorCellBinding binding;

    public ActorCellView(@NonNull Context context) {
        super(context);
        binding = ActorCellBinding.inflate(LayoutInflater.from(context));
        addView(binding.getRoot(), new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public <T> void updateModel(T model) {
        if (!(model instanceof ActorModel actor)) {
            return;
        }
        binding.actorName.setText(actor.getName());
        GlideApp.with(this)
                .load(actor.getImage().getPrimary())
                .placeholder(R.drawable.actor_avatar)
                .apply(options)
                .into(binding.actorAvatar);
    }
}

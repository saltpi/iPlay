package top.ourfor.app.iplayx.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.action.UpdateModelAction;
import top.ourfor.app.iplayx.model.EmbyActorModel;
import top.ourfor.app.iplayx.databinding.ActorCellBinding;
import top.ourfor.app.iplayx.module.GlideApp;

public class ActorCellView extends ConstraintLayout implements UpdateModelAction {
    private static RequestOptions options = new RequestOptions().transform(new RoundedCorners(20));

    ActorCellBinding binding = null;

    public ActorCellView(@NonNull Context context) {
        super(context);
        binding = ActorCellBinding.inflate(LayoutInflater.from(context));
        addView(binding.getRoot(), new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public <T> void updateModel(T model) {
        if (!(model instanceof EmbyActorModel)) {
            return;
        }
        EmbyActorModel actor = (EmbyActorModel) model;
        binding.actorName.setText(actor.getName());
        GlideApp.with(this)
                .load(actor.getImage().getPrimary())
                .placeholder(R.drawable.actor_avatar)
                .apply(options)
                .into(binding.actorAvatar);
    }
}

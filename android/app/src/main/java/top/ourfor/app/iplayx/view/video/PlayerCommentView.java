package top.ourfor.app.iplayx.view.video;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.MutableLiveData;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import top.ourfor.app.iplayx.api.dandan.DanDanPlayModel;
import top.ourfor.app.iplayx.util.DeviceUtil;
import top.ourfor.app.iplayx.view.infra.TextView;

public class PlayerCommentView extends FrameLayout implements LifecycleOwner {
    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    @Getter
    MutableLiveData<Float> currentTime = new MutableLiveData<>(0f);
    @Getter
    MutableLiveData<Boolean> displayComment = new MutableLiveData<>(true);

    List<TextView> availableView = new LinkedList<>();
    @Setter
    List<DanDanPlayModel.CommentAttribute> comments;

    public PlayerCommentView(@NonNull Context context) {
        super(context);
    }

    public PlayerCommentView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayerCommentView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PlayerCommentView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    void bind() {
        currentTime.observe(this, time -> {
            if (comments == null || !displayComment.getValue()) {
                return;
            }
            val ticks = currentTime.getValue();
            val nextComment = comments.stream().filter(comment -> comment.getTime() > ticks && comment.getTime() < ticks + 10).collect(Collectors.toCollection(LinkedList::new));
            val maxHeight = getHeight() / 3;
            val size = nextComment.size();
            for (int i = 0; i < size; i++) {
                DanDanPlayModel.CommentAttribute comment = nextComment.get(i);
                TextView textView = getAvailableView();
                textView.setText(comment.getContent());
                textView.setTextColor(0xFF000000 | comment.getColor());
                val params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int baseTop = 0;
                if (comment.getType() == DanDanPlayModel.CommentType.Top) {
                    baseTop = 0;
                } else if (comment.getType() == DanDanPlayModel.CommentType.Bottom) {
                    baseTop = maxHeight * 2;
                } else {
                    baseTop = maxHeight;
                }
                int offset = (int) (Math.random() * maxHeight);
                if (offset + textView.getHeight() > maxHeight) {
                    offset = maxHeight - textView.getHeight();
                }
                params.topMargin = baseTop + offset;
                params.leftMargin = DeviceUtil.dpToPx(0);
                addView(textView, params);
                textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        startAnimation(textView, (int)(comment.getTime() - ticks));
                    }
                });
            }
        });

        displayComment.observe(this, display -> {
            if (display) {
                setAlpha(1.f);
            } else {
                setAlpha(0.f);
            }
        });
    }

    TextView getAvailableView() {
        if (availableView.isEmpty()) {
            TextView textView = new TextView(getContext());
            textView.setTextSize(DeviceUtil.spToPx(18));
            availableView.add(textView);
        }
        return availableView.remove(0);
    }

    void recycleView(TextView textView) {
        textView.setText(null);
        availableView.add(textView);
    }

    private void startAnimation(final View view, int delay) {
        ViewGroup parent = (ViewGroup) view.getParent();
        int parentWidth = parent.getWidth();
        int viewWidth = view.getWidth();

        view.setTranslationX(parentWidth);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", parentWidth, -viewWidth);
        animator.setDuration(10 * 1000);
        animator.setStartDelay(delay * 1000L);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                ((ViewGroup) view.getParent()).removeView(view);
                recycleView((TextView) view);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        animator.start();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        lifecycleRegistry.setCurrentState(Lifecycle.State.STARTED);
        bind();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        lifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }
}

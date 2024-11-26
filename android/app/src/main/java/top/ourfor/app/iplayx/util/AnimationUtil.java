package top.ourfor.app.iplayx.util;

import static com.airbnb.lottie.LottieDrawable.INFINITE;

import android.content.Context;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import lombok.val;
import top.ourfor.app.iplayx.R;

public class AnimationUtil {
    public static LottieAnimationView createActivityIndicate(Context context) {
        var indicator = new LottieAnimationView(context);
        indicator.setSpeed(1);
        indicator.setRepeatMode(LottieDrawable.RESTART);
        indicator.setRepeatCount(INFINITE);
        indicator.setAnimation(R.raw.a2);
        val layout = LayoutUtil.center();
        layout.width = DeviceUtil.dpToPx(150);
        layout.height = DeviceUtil.dpToPx(150);
        indicator.setLayoutParams(layout);
        indicator.playAnimation();
        return indicator;
    }
}

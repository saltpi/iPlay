package top.ourfor.app.iplayx.view.infra.enu;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author center
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({
        BlurMode.MODE_RECTANGLE, BlurMode.MODE_CIRCLE,
        BlurMode.MODE_OVAL
})
public @interface BlurMode {
    int MODE_RECTANGLE = 0;
    int MODE_CIRCLE = 1;
    int MODE_OVAL = 2;
}

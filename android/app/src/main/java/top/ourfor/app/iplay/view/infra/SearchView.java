package top.ourfor.app.iplay.view.infra;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import top.ourfor.app.iplay.R;

public class SearchView extends android.widget.SearchView {
    public SearchView(@NonNull Context context) {
        super(context);
        styleSearchView();
    }

    public SearchView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        styleSearchView();
    }

    public SearchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        styleSearchView();
    }

    public void styleSearchView() {
        this.setIconifiedByDefault(false);
        //去除下划线
        @SuppressLint("DiscouragedApi") int plateId = this.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        LinearLayout plate = (LinearLayout)this.findViewById(plateId);
        plate.setBackgroundColor(Color.TRANSPARENT);
        //设置搜索框EditText
        @SuppressLint("DiscouragedApi") int searchPlateId = this.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        AutoCompleteTextView searchPlate = (AutoCompleteTextView)this.findViewById(searchPlateId);
        //提示文本颜色
        searchPlate.setHintTextColor(getResources().getColor(R.color.onSecondary));
        searchPlate.setTextColor(getResources().getColor(R.color.onBackground));
        searchPlate.setBackgroundColor(Color.TRANSPARENT);
        searchPlate.setGravity(Gravity.CENTER);
        //自定义搜索图标
        @SuppressLint("DiscouragedApi") int search_mag_icon_id = this.getContext().getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView search_mag_icon = (ImageView) this.findViewById(search_mag_icon_id);
        search_mag_icon.setImageDrawable(null);
        search_mag_icon.setScaleType(ImageView.ScaleType.CENTER);
        search_mag_icon.setVisibility(INVISIBLE);
        //自定义清除图标
        @SuppressLint("DiscouragedApi") int search_close_icon_id = this.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView search_close_btn = (ImageView) this.findViewById(search_close_icon_id);
        search_close_btn.setImageDrawable(null);
    }
}

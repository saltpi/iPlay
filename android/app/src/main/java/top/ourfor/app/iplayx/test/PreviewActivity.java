package top.ourfor.app.iplayx.test;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;

import lombok.SneakyThrows;
import top.ourfor.app.iplayx.view.video.PlayerSelectDelegate;
import top.ourfor.app.iplayx.view.video.PlayerSelectModel;
import top.ourfor.app.iplayx.view.video.PlayerView;

public class PreviewActivity extends AppCompatActivity implements PlayerSelectDelegate<PlayerSelectModel<Object>> {
    private static String TAG = "PlayerSelectResult";
    private ConstraintLayout rootView;
    private View playerSelectView;

    @SneakyThrows
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        String url = "https://test.mkv";
        super.onCreate(savedInstanceState);
        Context context = getApplicationContext();
        ConstraintLayout rootView = new ConstraintLayout(context);
        PlayerView playerView = new PlayerView(context, url);
        ConstraintLayout.LayoutParams playerLayout = new Constraints.LayoutParams(1200, 700);
        playerLayout.topToTop = Constraints.LayoutParams.PARENT_ID;
        playerLayout.leftToLeft = Constraints.LayoutParams.PARENT_ID;
        playerLayout.bottomToBottom = Constraints.LayoutParams.PARENT_ID;
        playerLayout.rightToRight = Constraints.LayoutParams.PARENT_ID;
        playerView.setLayoutParams(playerLayout);
        rootView.addView(playerView, playerLayout);
        setContentView(rootView);

        this.rootView = rootView;
    }

    @Override
    public void onClose() {
        rootView.removeView(playerSelectView);
    }

    @Override
    public void onSelect(PlayerSelectModel<Object> data) {
        Log.d(TAG, "" + data.getItem());
    }
}

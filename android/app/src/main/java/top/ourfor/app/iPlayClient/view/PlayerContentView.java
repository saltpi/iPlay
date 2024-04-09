package top.ourfor.app.iPlayClient.view;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class PlayerContentView extends SurfaceView implements SurfaceHolder.Callback {
    public Player viewModel;
    public String filePath = null;

    public PlayerContentView(Context context) {
        super(context);
    }

    public void initialize(String configDir, String cacheDir, String fontDir) {
        viewModel = new PlayerViewModel(
                configDir,
                cacheDir,
                fontDir
        );
        // we need to call write-watch-later manually
        getHolder().addCallback(this);
    }

    public void playFile(String filePath) {
        this.filePath = filePath;
        if (filePath != null) {
            viewModel.loadVideo(filePath);
        }
    }

    // Called when back button is pressed, or app is shutting down
    public void destroy() {
        viewModel.destroy();
        // Disable surface callbacks to avoid using unintialized mpv state
        getHolder().removeCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        log.debug("attach to surface");
        viewModel.attach(holder);
        if (filePath != null) {
            viewModel.loadVideo(filePath);
            filePath = null;
        } else {
            viewModel.setVideoOutput("gpu");
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        viewModel.resize(width+"x"+height);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        log.debug("detaching surface");
        viewModel.detach();
    }
}

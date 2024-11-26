package top.ourfor.app.iplayx.view.video;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import top.ourfor.app.iplayx.config.AppSetting;
import top.ourfor.app.iplayx.view.player.Player;

@Slf4j
@Getter
public class PlayerContentView extends SurfaceView implements SurfaceHolder.Callback {
    public Player viewModel;
    public String filePath = null;

    public PlayerContentView(Context context) {
        super(context);
    }

    public void initialize(String configDir, String cacheDir, String fontDir) {
        viewModel = AppSetting.shared.useExoPlayer ? new ExoPlayerViewModel(this) : new MPVPlayerViewModel(configDir, cacheDir, fontDir);
        setZOrderMediaOverlay(false);
        // we need to call write-watch-later manually
        getHolder().addCallback(this);
    }

    public void playFile(String filePath) {
        this.filePath = filePath;
        if (filePath != null) {
            viewModel.loadVideo(filePath);
        }
    }

    public void playFile(String filePath, String audioUrl) {
        this.filePath = filePath;
        if (filePath != null) {
            viewModel.loadVideo(filePath, audioUrl);
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

package top.ourfor.app.iplayx.view.video;

import static top.ourfor.app.iplayx.module.Bean.XGET;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.media3.common.MediaItem;
import androidx.media3.common.Timeline;
import androidx.media3.common.VideoSize;
import androidx.media3.exoplayer.ExoPlayer;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplayx.util.DeviceUtil;
import top.ourfor.app.iplayx.view.player.Player;

@Slf4j
public class ExoPlayerViewModel implements Player {

    private ExoPlayer player;
    private PlayerEventListener delegate;
    private androidx.media3.common.Player.Listener listener;
    private Handler handler;
    private Runnable updateProgressAction;
    private WeakReference<View> contentView;

    public ExoPlayerViewModel(View view) {
        val context = view.getContext();
        contentView = new WeakReference<>(view);
        player = new ExoPlayer.Builder(context).build();
        listener = new ExoPlayer.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (delegate == null) return;
                switch (state) {
                    case ExoPlayer.STATE_IDLE:
                        break;
                    case ExoPlayer.STATE_BUFFERING:
                        break;
                    case ExoPlayer.STATE_READY:
                        delegate.onPropertyChange(PlayerPropertyType.PausedForCache, false);
                        break;
                    case ExoPlayer.STATE_ENDED:
                        delegate.onPropertyChange(PlayerPropertyType.EofReached, true);
                        break;
                }

                double duration = player.getDuration() / 1000.f;
                double position = player.getCurrentPosition() / 1000.f;
                delegate.onPropertyChange(PlayerPropertyType.Duration, duration);
                delegate.onPropertyChange(PlayerPropertyType.TimePos, position);
            }

            @Override
            public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
                if (delegate == null) return;
                delegate.onPropertyChange(PlayerPropertyType.Pause, !playWhenReady);
            }

            @Override
            public void onIsLoadingChanged(boolean isLoading) {
                if (delegate == null) return;
                delegate.onPropertyChange(PlayerPropertyType.PausedForCache, isLoading);
            }

            @Override
            public void onPositionDiscontinuity(androidx.media3.common.Player.PositionInfo oldPosition, androidx.media3.common.Player.PositionInfo newPosition, int reason) {
                if (delegate == null) return;
                double currentPosition = newPosition.positionMs / 1000.0;
                delegate.onPropertyChange(PlayerPropertyType.TimePos, currentPosition);
            }

            @Override
            public void onTimelineChanged(Timeline timeline, int reason) {
                if (delegate == null) return;
                double currentPosition = player.getCurrentPosition() / 1000.f;
                double duration = player.getDuration() / 1000.f;
                delegate.onPropertyChange(PlayerPropertyType.Duration, duration);
                delegate.onPropertyChange(PlayerPropertyType.TimePos, currentPosition);
            }

            @Override
            public void onVideoSizeChanged(VideoSize videoSize) {
                log.debug("video size changed: {}", videoSize.pixelWidthHeightRatio);
                val view = contentView.get();
                if (view == null) return;
                if (view instanceof SurfaceView surfaceView) {
                    val params = surfaceView.getLayoutParams();
                    val size = DeviceUtil.screenSize(context);
                    val videoRatio = videoSize.width / (float) videoSize.height;
                    val screenRatio = size.getWidth() / (float) size.getHeight();
                    int width = 0;
                    int height = 0;
                    if (screenRatio > videoRatio) {
                        width = size.getWidth();
                        height = (int) (size.getWidth() / videoRatio);
                    } else {
                        width = (int) (size.getHeight() * videoRatio);
                        height = size.getHeight();
                    }
                    params.width = width;
                    params.height = height;
                    surfaceView.setLayoutParams(params);
                }
            }
        };
        player.addListener(listener);

        handler = new Handler(Looper.getMainLooper());
        updateProgressAction = new Runnable() {
            @Override
            public void run() {
                if (player != null && player.isPlaying()) {
                    double currentPosition = player.getCurrentPosition() / 1000.f;
                    delegate.onPropertyChange(PlayerPropertyType.TimePos, currentPosition);
                }
                handler.postDelayed(this, 1000);
            }
        };

        handler.post(updateProgressAction);
    }

    @Override
    public void setDelegate(PlayerEventListener delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setVideoOutput(String value) {
        Player.super.setVideoOutput(value);
    }

    @Override
    public void attach(SurfaceHolder holder) {
        player.setVideoSurfaceHolder(holder);
    }

    @Override
    public void detach() {
        player.setVideoSurfaceHolder(null);
    }

    @Override
    public Double progress() {
        return (double) player.getCurrentPosition() / 1000.f;
    }

    @Override
    public Double duration() {
        return (double) player.getDuration() / 1000.f;
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public void loadVideo(String url) {
        MediaItem mediaItem = MediaItem.fromUri(url);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    @Override
    public void setCachePath(String path) {
        Player.super.setCachePath(path);
    }

    @Override
    public void play() {
        player.play();
    }

    @Override
    public void resume() {
        player.play();
    }

    @Override
    public void pause() {
        player.pause();
    }

    @Override
    public void seek(long timeInSeconds) {
        player.seekTo(timeInSeconds * 1000);
    }

    @Override
    public void jumpBackward(int seconds) {
        player.seekTo(player.getCurrentPosition() - seconds * 1000);
    }

    @Override
    public void jumpForward(int seconds) {
        player.seekTo(player.getCurrentPosition() + seconds * 1000);
    }

    @Override
    public void stop() {
        player.stop();
    }

    @Override
    public void resize(String newSize) {
        Player.super.resize(newSize);
    }

    @Override
    public List audios() {
        return List.of();
    }

    @Override
    public List subtitles() {
        return List.of();
    }

    @Override
    public void loadSubtitle(List<PlayerSourceModel> subtitles) {
        Player.super.loadSubtitle(subtitles);
    }

    @Override
    public String currentSubtitleId() {
        return Player.super.currentSubtitleId();
    }

    @Override
    public String currentAudioId() {
        return Player.super.currentAudioId();
    }

    @Override
    public void setSubtitleFontName(String subtitleFontName) {
        Player.super.setSubtitleFontName(subtitleFontName);
    }

    @Override
    public void setSubtitleFontDirectory(String directory) {
        Player.super.setSubtitleFontDirectory(directory);
    }

    @Override
    public void destroy() {
        handler.removeCallbacks(updateProgressAction);
        player.setVideoSurfaceHolder(null);
        player.removeListener(listener);
        player.release();
    }

    @Override
    public void useSubtitle(String id) {

    }

    @Override
    public void useAudio(String id) {

    }

    @Override
    public void applyOption(Map<String, String> option) {
        Player.super.applyOption(option);
    }

    @Override
    public void speedUp(float speed) {
        player.setPlaybackSpeed(speed);
    }

    @Override
    public void speedDown(float speed) {
        player.setPlaybackSpeed(speed);
    }

    @Override
    public void useVideo(String id) {
        Player.super.useVideo(id);
    }

    @Override
    public void setLastWatchPosition(long lastWatchPosition) {
        player.seekTo(lastWatchPosition);
    }
}

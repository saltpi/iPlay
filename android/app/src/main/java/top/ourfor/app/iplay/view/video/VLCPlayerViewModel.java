package top.ourfor.app.iplay.view.video;

import static top.ourfor.lib.mpv.TrackItem.AudioTrackName;
import static top.ourfor.lib.mpv.TrackItem.SubtitleTrackName;

import android.net.Uri;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.View;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.interfaces.IMedia;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplay.view.player.Player;
import top.ourfor.lib.mpv.TrackItem;

@Slf4j
public class VLCPlayerViewModel implements Player {
    LibVLC libVLC;
    MediaPlayer mediaPlayer;
    PlayerEventListener delegate;

    public VLCPlayerViewModel(View view) {
        var options = List.of("--avcodec-hw=any");
        var context = view.getContext();
        libVLC = new LibVLC(context, options);
        mediaPlayer = new MediaPlayer(libVLC);
    }

    @Override
    public void setDelegate(PlayerEventListener delegate) {
        this.delegate = delegate;
        mediaPlayer.setEventListener(event -> {
            switch (event.type) {
                case MediaPlayer.Event.EndReached:
                    delegate.onPropertyChange(PlayerPropertyType.EofReached, true);
                    break;
                case MediaPlayer.Event.Playing:
                    delegate.onPropertyChange(PlayerPropertyType.PausedForCache, false);
                    break;
                case MediaPlayer.Event.Paused:
                    delegate.onPropertyChange(PlayerPropertyType.Pause, true);
                    break;
                case MediaPlayer.Event.TimeChanged:
                    if (isPlaying()) {
                        delegate.onPropertyChange(PlayerPropertyType.TimePos, mediaPlayer.getTime() / 1000.0);
                        delegate.onPropertyChange(PlayerPropertyType.PausedForCache, false);
                    }
                    break;
                case MediaPlayer.Event.Buffering:
                    delegate.onPropertyChange(PlayerPropertyType.PausedForCache, true);
                    break;
                case MediaPlayer.Event.LengthChanged:
                    if (isPlaying()) delegate.onPropertyChange(PlayerPropertyType.Duration, mediaPlayer.getLength() / 1000.0);
                    break;
            }
        });
    }

    @Override
    public void attach(SurfaceHolder holder) {
        mediaPlayer.getVLCVout().setVideoSurface(holder.getSurface(), holder);
        val size = new Size(holder.getSurfaceFrame().width(), holder.getSurfaceFrame().height());
        mediaPlayer.getVLCVout().setWindowSize(size.getWidth(), size.getHeight());
        mediaPlayer.getVLCVout().attachViews();
    }

    @Override
    public void resize(String newSize) {
        // size: width x height
        var size = newSize.split("x");
        mediaPlayer.getVLCVout().setWindowSize(Integer.parseInt(size[0]), Integer.parseInt(size[1]));
    }

    @Override
    public void detach() {
        mediaPlayer.getVLCVout().detachViews();
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public void resume() {
        mediaPlayer.play();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public Double progress() {
        return (double) mediaPlayer.getTime() / 1000.0;
    }

    @Override
    public Double duration() {
        return (double) mediaPlayer.getLength() / 1000.0;
    }

    @Override
    public void loadVideo(String url) {
        log.info("load video: {}", url);
        var media = new Media(libVLC, Uri.parse(url));
        mediaPlayer.setMedia(media);
        media.release();
        mediaPlayer.play();
    }

    @Override
    public void loadVideo(String url, String audioUrl) {
        log.info("load video: {}, audio: {}", url, audioUrl);
        var media = new Media(libVLC, Uri.parse(url));
        mediaPlayer.setMedia(media);
        media.release();
        mediaPlayer.play();
    }

    @Override
    public void jumpForward(int seconds) {
        mediaPlayer.setTime(mediaPlayer.getTime() + seconds * 1000L);
    }

    @Override
    public void jumpBackward(int seconds) {
        mediaPlayer.setTime(mediaPlayer.getTime() - seconds * 1000L);
    }

    @Override
    public void speed(float speed) {
        mediaPlayer.setRate(speed);
    }

    @Override
    public void speedDown(float speed) {
        mediaPlayer.setRate(mediaPlayer.getRate() - speed);
    }

    @Override
    public void speedUp(float speed) {
        mediaPlayer.setRate(mediaPlayer.getRate() + speed);
    }

    @Override
    public String currentSubtitleId() {
        val selectedTrack = mediaPlayer.getSelectedTrack(IMedia.Track.Type.Text);
        return selectedTrack != null ? selectedTrack.id : "";
    }

    @Override
    public String currentAudioId() {
        val selectedTrack = mediaPlayer.getSelectedTrack(IMedia.Track.Type.Audio);
        return selectedTrack != null ? selectedTrack.id : "";
    }

    @Override
    public void useSubtitle(String id) {
        mediaPlayer.selectTracks(IMedia.Track.Type.Text, id);
    }

    @Override
    public void useAudio(String id) {
        mediaPlayer.selectTracks(IMedia.Track.Type.Audio, id);
    }

    @Override
    public void useVideo(String id) {
        mediaPlayer.selectTracks(IMedia.Track.Type.Video, id);
    }

    @Override
    public void seek(long timeInSeconds) {
        mediaPlayer.setTime(timeInSeconds * 1000);
    }

    @Override
    public List<TrackItem> subtitles() {
        val tracks = mediaPlayer.getTracks(IMedia.Track.Type.Text);
        if (tracks == null) return List.of();
        return Arrays.stream(tracks).map(track -> {
            val builder = TrackItem.builder();
            builder.id(String.valueOf(track.id));
            builder.title(track.name);
            builder.type(SubtitleTrackName);
            builder.lang(track.language);
            return builder.build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<TrackItem> audios() {
        val tracks = mediaPlayer.getTracks(IMedia.Track.Type.Audio);
        if (tracks == null) return List.of();
        return Arrays.stream(tracks).map(track -> {
            val builder = TrackItem.builder();
            builder.id(String.valueOf(track.id));
            builder.title(track.name);
            builder.type(AudioTrackName);
            builder.lang(track.language);
            return builder.build();
        }).collect(Collectors.toList());
    }


    @Override
    public void destroy() {
        try {
            if (mediaPlayer != null && !mediaPlayer.isReleased()) {
                mediaPlayer.stop();
                mediaPlayer.getVLCVout().detachViews();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            if (libVLC != null && !libVLC.isReleased()) {
                libVLC.release();
                libVLC = null;
            }
        } catch (Exception e) {
            log.error("destroy error", e);
        }
    }
}

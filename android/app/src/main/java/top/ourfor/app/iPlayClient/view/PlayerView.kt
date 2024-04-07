package top.ourfor.app.iPlayClient.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.AssetManager
import android.media.AudioManager
import android.os.Build
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.facebook.react.uimanager.ThemedReactContext
import top.ourfor.app.iPlayClient.R
import top.ourfor.app.iPlayClient.module.FontModule
import top.ourfor.app.iPlayClient.view.Player.PlayEventType
import top.ourfor.lib.mpv.TrackItem
import top.ourfor.lib.mpv.TrackItem.AudioTrackName
import top.ourfor.lib.mpv.TrackItem.SubtitleTrackName
import java.io.File
import java.io.FileOutputStream
import java.util.stream.Collectors
import kotlin.math.max
import kotlin.math.min
import android.R as GlobalR


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("ResourceType")
class PlayerView(
    context: Context,
    url: String?
) : ConstraintLayout(context),
    PlayerEventListener,
    PlayerEventDelegate, PlayerSelectDelegate<PlayerSelectModel<TrackItem>> {
    var subtitleFontName: String? = null
        set(value) {
            contentView.viewModel.setSubtitleFontName(value)
        }

    private var controlView: PlayerControlView?
    private var contentView: PlayerContentView
    private var eventView: PlayerEventView
    private var fullscreenView: PlayerFullscreenView? = null
    private var isFullscreen = false
    private var duration: Double = 0.0
    private var position: Double = 0.0
    private var brightnessValue = 0
    private var volumeValue = 0;
    var themedReactContext: ThemedReactContext? = null
    var onPlayStateChange: (data: HashMap<String, Any>) -> Unit  = {}
    var url: String? = null
        set(value) {
            field = value
            if (value != null) {
                contentView.playFile(value)
            }
        }
    var title: String? = null
        @RequiresApi(Build.VERSION_CODES.O)
        set(value) {
            field = value
            if (controlView == null) return
            controlView!!.videoTitle = title
        }
    init {
        contentView = PlayerContentView(context)
        val player = contentView
        val contentLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        contentLayoutParams.topToTop = LayoutParams.PARENT_ID;
        contentLayoutParams.bottomToBottom = LayoutParams.PARENT_ID;
        contentLayoutParams.leftToLeft = LayoutParams.PARENT_ID;
        contentLayoutParams.rightToRight = LayoutParams.PARENT_ID;
        addView(contentView, contentLayoutParams)

        copySubtitleFont(context.filesDir.path)

        player?.initialize(context.filesDir.path, context.cacheDir.path,
            FontModule.getFontPath(context)
        )
        val viewModel = player?.viewModel
        if (subtitleFontName != null) {
            viewModel?.setSubtitleFontName(subtitleFontName)
        }
        viewModel?.setDelegate(this)
        if (url != null) player?.playFile(url)

        val controlView = PlayerControlView(context)
        val controlLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        controlLayoutParams.topToTop = LayoutParams.PARENT_ID;
        controlLayoutParams.bottomToBottom = LayoutParams.PARENT_ID;
        controlLayoutParams.leftToLeft = LayoutParams.PARENT_ID;
        controlLayoutParams.rightToRight = LayoutParams.PARENT_ID;
        controlView.player = viewModel
        controlView.delegate = this
        addView(controlView, controlLayoutParams)
        this.controlView = controlView;

        eventView = PlayerEventView(context);
        eventView.ignoreAreas = listOf(
            controlView.playButton,
            controlView.fullscreenButton,
            controlView.progressBar,
            controlView.subtitleButton,
            controlView.audioButton
        )
        eventView.delegate = this
        eventView.trackSelectDelegate = this
        val eventLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        eventLayoutParams.topToTop = LayoutParams.PARENT_ID;
        eventLayoutParams.bottomToBottom = LayoutParams.PARENT_ID;
        eventLayoutParams.leftToLeft = LayoutParams.PARENT_ID;
        eventLayoutParams.rightToRight = LayoutParams.PARENT_ID;
        addView(eventView, eventLayoutParams)

        fullscreenView = PlayerFullscreenView(
            context,
            contentView,
            controlView,
            eventView
        )
        fullscreenView?.getWindow()?.setWindowAnimations(GlobalR.style.Animation_Dialog)

        keepScreenOn = true
    }

    override fun onPropertyChange(name: String?, value: Any?) {
        this.controlView?.post {
            this.controlView?.onPropertyChange(name, value)
        }
        if (value == null) {
            if (name.equals("track-list")) {
                Log.d(TAG, "load track list")
            }
            return
        }
        
        if (name.equals("time-pos") ||
            name.equals("pause") ||
            name.equals("paused-for-cache")) {
            var state = PlayEventType.PlayEventTypeOnProgress
            val data = HashMap<String, Any>()
            if (name.equals("time-pos")) {
                state = PlayEventType.PlayEventTypeOnProgress
                position = value as Double
                data.put("duration", duration);
                data.put("position", position);
            } else if (name.equals("pause")) {
                state = PlayEventType.PlayEventTypeOnPause
                data.put("duration", duration);
                data.put("position", position);
            } else if (name.equals("paused-for-cache")) {
                state = PlayEventType.PlayEventTypeOnPauseForCache
            }

            data.put("type", state.value)
            onPlayStateChange(data)
        } else if (name.equals("duration")) {
            duration = value as Double
        }
    }

    override fun onSelectSubtitle() {
        Log.d(TAG, "open subtitle select view")
        var player = contentView.viewModel
        var currentSubtitleId = player.currentSubtitleId()
        var subtitles = player.subtitles() as List<TrackItem>
        controlView?.toggleVisible()
        eventView.showSelectView(subtitles, currentSubtitleId)
    }

    override fun onSelectAudio() {
        Log.d(TAG, "open audio select view")
        var player = contentView.viewModel
        var currentAudioId = player.currentAudioId()
        var audios = player.audios() as List<TrackItem>
        controlView?.toggleVisible()
        eventView.showSelectView(audios, currentAudioId)
    }

    override fun onWindowSizeChange() {
        val activity = themedReactContext?.currentActivity ?: return
        val window = activity.window

        if (isFullscreen) {
            fullscreenView?.dismiss()
            requestLayout()
            val controller = WindowInsetsControllerCompat(window, window.decorView)
            WindowCompat.setDecorFitsSystemWindows(window, true)
            controller.show(WindowInsetsCompat.Type.systemBars())
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            fullscreenView?.show()
            val controller = WindowInsetsControllerCompat(window, window.decorView)
            WindowCompat.setDecorFitsSystemWindows(window, false)
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            fullscreenView?.setOnDismissListener {
                val controller = WindowInsetsControllerCompat(window, window.decorView)
                WindowCompat.setDecorFitsSystemWindows(window, true)
                controller.show(WindowInsetsCompat.Type.systemBars())
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                controlView?.updateFullscreenStyle(false)
            }
        }

        isFullscreen = !isFullscreen
        controlView?.updateFullscreenStyle(isFullscreen)

        if (isFullscreen) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun onSelect(data: PlayerSelectModel<TrackItem>?) {
        var item: TrackItem? = data?.item ?: return
        if (item == null) return
        if (item.type.equals(SubtitleTrackName)) {
            Log.d(TAG, "use subtitle id ${item.id}")
            contentView.viewModel.useSubtitle(item.id)
        } else if (item.type.equals(AudioTrackName)) {
            Log.d(TAG, "use audio id ${item.id}")
            contentView.viewModel.useAudio(item.id)
        }
    }

    override fun onDeselect(data: PlayerSelectModel<TrackItem>?) {
    }

    fun copySubtitleFont(configDir: String) {
        var ins = context.assets.open("subfont.ttf", AssetManager.ACCESS_STREAMING)
        val outFile = File("$configDir/subfont.ttf")
        val out = FileOutputStream(outFile)
        if (outFile.length() == ins.available().toLong()) {
            return
        }
        ins.copyTo(out)
        ins.close()
        out.close()
    }

    override fun onDetachedFromWindow() {
        keepScreenOn = false
        Log.d(TAG, "destroy player")
        contentView.viewModel.destroy()
        super.onDetachedFromWindow()
    }

    override fun onEvent(type: PlayerGestureType?, value: Any) {
        when(type) {
            PlayerGestureType.None -> {
                brightnessValue = getBrightnessValue();
                volumeValue = getVolumeValue()
                val targetType = value as PlayerGestureType
                if (targetType == PlayerGestureType.Brightness) {
                    eventView?.numberValueView?.setMaxValue(getBrightnessMaxValue())
                    eventView?.numberValueView?.updateIcon(R.drawable.lightbulb_min)
                    eventView?.numberValueView?.show()
                } else if (targetType == PlayerGestureType.Volume) {
                    eventView?.numberValueView?.setMaxValue(getVolumeMaxValue())
                    eventView?.numberValueView?.updateIcon(R.drawable.waveform)
                    eventView?.numberValueView?.show()
                }
            }
            PlayerGestureType.HideControl -> {
                Log.d(TAG, "hide control view")
                eventView?.numberValueView?.hide();
                controlView?.toggleVisible()
            }
            PlayerGestureType.Seek -> {
                var delta = 10;
                if ((value as Float) > 0) {
                    contentView.viewModel.jumpForward(delta);
                } else {
                    contentView.viewModel.jumpBackward(delta)
                }
            }
            PlayerGestureType.Volume -> {
                var delta = (value as Float).toInt()
                setVolumeValue(volumeValue + delta);
                eventView?.numberValueView?.setProgress(volumeValue + delta);
            }
            PlayerGestureType.Brightness -> {
                var delta = (value as Float).toInt()
                setBrightnessValue(brightnessValue + delta);
                eventView?.numberValueView?.setProgress(brightnessValue + delta);
            }
            null -> {}
        }
    }

    fun getBrightnessValue(): Int {
        val defVal = 50;
        val value = getWindow(this)?.attributes?.screenBrightness
        if (value != null) return (value * 100).toInt()
        return defVal
    }

    fun setBrightnessValue(value: Int) {
        val newValue = min(max(0, value), 100)
        var window = getWindow(this)
        var attributes = window?.attributes
        if (attributes != null) {
            attributes.screenBrightness = (newValue / 100.0).toFloat();
            window?.setAttributes(attributes)
        }
    }

    fun getBrightnessMaxValue(): Int {
        return 100;
    }

    fun getVolumeMaxValue(): Int {
        var audioService = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        var maxValue = audioService.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        return maxValue;
    }
    fun getVolumeValue(): Int {
        var audioService = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val value = audioService.getStreamVolume(AudioManager.STREAM_MUSIC)
        return value;
    }

    fun setVolumeValue(value: Int) {
        var audioService = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        var maxValue = audioService.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val newValue = min(max(0, value), maxValue)
        audioService.setStreamVolume(AudioManager.STREAM_MUSIC, newValue, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
    }

    fun getWindow(view: View): Window? {
        val activity = themedReactContext?.currentActivity ?: return null
        return activity.window
    }

    companion object {
        val TAG = "PlayerView"
    }
}
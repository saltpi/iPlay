package top.ourfor.app.iPlayClient

import android.R as GlobalR
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.AssetManager
import android.media.AudioManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.facebook.react.uimanager.ThemedReactContext
import top.ourfor.app.iPlayClient.Player.PlayEventType
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max
import kotlin.math.min


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("ResourceType")
class PlayerView(
    context: Context,
    url: String?
) : ConstraintLayout(context), PlayerEventListener, PlayerEventDelegate {
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
    private var progressValue = 0;
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

        player?.initialize(context.filesDir.path, context.cacheDir.path, FontModule.getFontPath(context))
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
            controlView.progressBar
        )
        eventView.delegate = this
        val eventLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        eventLayoutParams.topToTop = LayoutParams.PARENT_ID;
        eventLayoutParams.bottomToBottom = LayoutParams.PARENT_ID;
        eventLayoutParams.leftToLeft = LayoutParams.PARENT_ID;
        eventLayoutParams.rightToRight = LayoutParams.PARENT_ID;
        addView(eventView, eventLayoutParams)

        fullscreenView = PlayerFullscreenView(context, contentView, controlView, eventView)
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
//                contentView.viewModel.subtitles()
//                contentView.viewModel.useSubtitle(1)
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
                    controlView?.numberValueView?.updateIcon(R.drawable.lightbulb_min)
                } else if (targetType == PlayerGestureType.Volume) {
                    controlView?.numberValueView?.updateIcon(R.drawable.waveform)
                }
            }
            PlayerGestureType.HideControl -> {
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
                controlView?.numberValueView?.setProgress(volumeValue + delta);
            }
            PlayerGestureType.Brightness -> {
                var delta = (value as Float).toInt()
                setBrightnessValue(brightnessValue + delta);
                controlView?.numberValueView?.setProgress(brightnessValue + delta);
            }
            null -> {}
        }
    }

    fun getBrightnessValue(): Int {
        val defVal = 50;
        var value = Settings.System.getInt(context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS, defVal);
        return (value * 100.0 / 255).toInt();
    }

    fun setBrightnessValue(value: Int) {
        val newValue = min(max(0, (value * 255 / 100.0).toInt()), 255)
        Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, newValue);
    }

    fun getVolumeValue(): Int {
        var audioService = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        var maxValue = audioService.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val value = audioService.getStreamVolume(AudioManager.STREAM_MUSIC)
        return (value * 100.0 / maxValue).toInt();
    }

    fun setVolumeValue(value: Int) {
        var audioService = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        var maxValue = audioService.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val newValue = min(max(0, (value * maxValue / 100.0).toInt()), maxValue)
        audioService.setStreamVolume(AudioManager.STREAM_MUSIC, value, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
    }

    companion object {
        val TAG = "PlayerView"
    }
}
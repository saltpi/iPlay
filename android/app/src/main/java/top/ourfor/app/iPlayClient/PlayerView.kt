package top.ourfor.app.iPlayClient

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.Log
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ReactContext
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.events.RCTEventEmitter
import top.ourfor.app.iPlayClient.Player.PlayEventType


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("ResourceType")
class PlayerView(
    context: Context,
    url: String?
) : ConstraintLayout(context), PlayerEventListener {
    private var controlView: PlayerControlView?
    private var contentView: PlayerContentView
    private var fullscreenView: PlayerFullscreenView? = null
    private var isFullscreen = false
    var themedReactContext: ThemedReactContext? = null
    var onPlayStateChange: (state: Int) -> Unit  = {}
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

        player?.initialize(context.filesDir.path, context.cacheDir.path)
        val viewModel = player?.viewModel
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

        fullscreenView = PlayerFullscreenView(context, contentView, controlView)
        fullscreenView?.getWindow()?.setWindowAnimations(R.style.Animation_Dialog)
    }

    override fun onPropertyChange(name: String?, value: Any?) {
        this.controlView?.onPropertyChange(name, value)
        if (name.equals("time-pos") ||
            name.equals("pause") ||
            name.equals("paused-for-cache")) {
            var state = PlayEventType.PlayEventTypeOnProgress
            if (name.equals("time-pos")) state = PlayEventType.PlayEventTypeOnProgress;
            else if (name.equals("pause")) state = PlayEventType.PlayEventTypeOnPause;
            else if (name.equals("paused-for-cache")) state = PlayEventType.PlayEventTypeOnPauseForCache
            onPlayStateChange(state.value)
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
        }
        isFullscreen = !isFullscreen
        controlView?.updateFullscreenStyle(isFullscreen)

        if (isFullscreen) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    fun onDestroy() {
        Log.d(TAG, "destroy player")
        contentView.viewModel.stop()
        contentView.destroy()
    }

    companion object {
        val TAG = "PlayerView"
    }
}
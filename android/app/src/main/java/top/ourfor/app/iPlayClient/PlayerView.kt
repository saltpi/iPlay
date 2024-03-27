package top.ourfor.app.iPlayClient

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import top.ourfor.app.iPlayClient.databinding.PlayerBinding


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("ResourceType")
class PlayerView(
    context: Context,
    url: String?
) : ConstraintLayout(context), PlayerEventListener {
    private var controlView: PlayerControlView?
    private var contentView: ViewGroup?
    private var fullscreenView: PlayerFullscreenView? = null
    private var isFullscreen = false
    init {
        setBackgroundColor(Color.BLUE)

        val binding = PlayerBinding.inflate(LayoutInflater.from(context))
        val player = binding.player
        val contentLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        contentLayoutParams.topToTop = LayoutParams.PARENT_ID;
        contentLayoutParams.bottomToBottom = LayoutParams.PARENT_ID;
        contentLayoutParams.leftToLeft = LayoutParams.PARENT_ID;
        contentLayoutParams.rightToRight = LayoutParams.PARENT_ID;
        contentView = binding.root
        addView(contentView, contentLayoutParams)

        player.initialize(context.filesDir.path, context.cacheDir.path)
        val viewModel = player.viewModel
        viewModel.setDelegate(this)
        if (url != null) player.playFile(url)

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
    }

    override fun onWindowSizeChange() {
        if (isFullscreen) {
            fullscreenView?.dismiss()
        } else {
            fullscreenView?.show()
        }
        isFullscreen = !isFullscreen
    }
}
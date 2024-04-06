package top.ourfor.app.iPlayClient

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger


@RequiresApi(Build.VERSION_CODES.O)
class PlayerControlView(context: Context) : ConstraintLayout(context), PlayerEventListener {
    public var delegate: PlayerEventListener? = null;
    private var shouldUpdateProgress = true;
    var player: Player? = null
    var videoTitle: String? = null
        set(value) {
            field = value
            titleLabel.text = value
        }

    fun updateFullscreenStyle(isFullscreen: Boolean) {
        if (isFullscreen) {
            updateIcon(fullscreenButton, androidx.media3.ui.R.drawable.exo_icon_fullscreen_exit)
        } else {
            updateIcon(fullscreenButton, androidx.media3.ui.R.drawable.exo_icon_fullscreen_enter)
        }
    }

    private var resId: AtomicInteger = AtomicInteger(8000)
    private val dateFormatter: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("HH:mm:ss")
    }

    var playButton: View = run {
        val layout = ConstraintLayout(context)
        val icon = ImageView(context)
        icon.tag = ICON_TAG
        icon.setImageResource(androidx.media3.ui.R.drawable.exo_icon_pause)
        val iconLayout = centerLayout()
        iconLayout.width = ICON_SIZE
        iconLayout.height = ICON_SIZE
        layout.addView(icon, iconLayout)
        val gradientDrawable = GradientDrawable()
        gradientDrawable.setColor(Color.argb(50, 0, 0, 0))
        gradientDrawable.cornerRadius = ICON_SIZE + 0f
        layout.background = gradientDrawable
        layout.rootView.id = resId.getAndIncrement()
        layout.rootView
    }

    private var playButtonLayout = run {
        val params = LayoutParams(ICON_SIZE * 2, ICON_SIZE * 2)
        params.topToTop = LayoutParams.PARENT_ID
        params.bottomToBottom = LayoutParams.PARENT_ID
        params.rightToRight = LayoutParams.PARENT_ID
        params.leftToLeft = LayoutParams.PARENT_ID
        params
    }

    var fullscreenButton = run {
        val layout = ConstraintLayout(context)
        val icon = ImageView(context)
        icon.tag = ICON_TAG
        icon.setImageResource(androidx.media3.ui.R.drawable.exo_icon_fullscreen_enter)
        val iconLayout = LayoutParams(centerLayout())
        iconLayout.width = ICON_SMALL_SIZE
        iconLayout.height = ICON_SMALL_SIZE
        layout.addView(icon, iconLayout)
        val gradientDrawable = GradientDrawable()
        gradientDrawable.setColor(Color.argb(50, 0, 0, 0))
        gradientDrawable.cornerRadius = ICON_SMALL_SIZE + 0f
        layout.background = gradientDrawable
        layout.rootView.id = resId.getAndIncrement()
        layout.rootView
    }

    private var fullscreenLayout = run {
        val params = LayoutParams(ICON_SMALL_SIZE * 2, ICON_SMALL_SIZE * 2)
        params.topToTop = LayoutParams.PARENT_ID
        params.rightToRight = LayoutParams.PARENT_ID
        params.topMargin = 48
        params.rightMargin = 48
        params
    }

    var progressBar = run {
        val slide = SeekBar(context)
        val color = Color.WHITE
        val colorStateList = ColorStateList.valueOf(color)
        val thumbRadius = 25
        val thumb = GradientDrawable()
        thumb.shape = GradientDrawable.OVAL
        thumb.setSize(thumbRadius * 2, thumbRadius * 2)
        thumb.setColor(Color.RED)
        slide.thumb = thumb
        slide.thumbTintList = colorStateList
        slide.progressTintList = colorStateList
        slide.id = resId.getAndIncrement()
        slide.setPadding(thumbRadius, thumbRadius, thumbRadius, thumbRadius)
        slide
    }

    private var progressBarLayout = run {
        val params = LayoutParams(0, 60)
        params.matchConstraintPercentWidth = 0.9f
        params.bottomToBottom = LayoutParams.PARENT_ID
        params.bottomMargin = 100
        params.leftToLeft = LayoutParams.PARENT_ID
        params.rightToRight = LayoutParams.PARENT_ID
        params
    }

    private var durationLabel = run {
        val label = TextView(context)
        label.textSize = 12.0F
        label.setTextColor(Color.WHITE)
        label.text = formatTime(0, 0)
        label.id = resId.getAndIncrement()
        label
    }

    private var durationLayout = run {
        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.rightToRight = progressBar.id
        params.bottomToTop = progressBar.id
        params.bottomMargin = 10
        params
    }

    var titleLabel = run {
        val label = TextView(context)
        label.textSize = 14.0F
        label.setTextColor(Color.WHITE)
        label.id = resId.getAndIncrement()
        label
    }

    private var titleLayout = run {
        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.leftToLeft = progressBar.id
        params.bottomToTop = progressBar.id
        params.bottomMargin = 10
        params
    }

    var numberValueView = run {
        val view = PlayerNumberValueView(context);
        view.id = resId.getAndIncrement()
        view
    }

    private var numberValueLayout = run {
        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.leftToLeft = LayoutParams.PARENT_ID;
        params.topToTop = LayoutParams.PARENT_ID;
        params.rightToRight = LayoutParams.PARENT_ID;
        params.topMargin = 100
        params
    }


    init {
        setupUI()
        bind()
    }

    private fun setupUI() {
        addView(playButton, playButtonLayout)
        addView(fullscreenButton, fullscreenLayout)
        addView(progressBar, progressBarLayout)
        addView(durationLabel, durationLayout)
        addView(titleLabel, titleLayout)
        addView(numberValueView, numberValueLayout)
    }

    private fun bind() {
        playButton.setOnClickListener {
            Log.d(TAG, "play")
            val isPlaying = player?.isPlaying == true
            var resId = if (isPlaying) androidx.media3.ui.R.drawable.exo_icon_play else androidx.media3.ui.R.drawable.exo_icon_pause
            post {
                updateIcon(playButton, resId)
            }
            if (isPlaying) {
                player?.pause()
            } else {
                player?.resume()
            }
        }
        progressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                shouldUpdateProgress = false;
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                player?.seek(progressBar.progress.toLong())
                shouldUpdateProgress = true;
            }
        })
        fullscreenButton.setOnClickListener {
            delegate?.onWindowSizeChange()
        }
    }

    fun toggleVisible() {
        animate()
            .alpha(1.0f - alpha)
            .setDuration(800)
            .start()
    }

    private fun updateIcon(view: View, resId: Int) {
        val imageView = view.findViewWithTag<ImageView?>(ICON_TAG)
        if (imageView !is ImageView) return
        imageView?.setImageResource(resId)
    }

    override fun onPropertyChange(name: String?, value: Any?) {
        if (value == null) return
        if (name.equals("duration")) {
            val duration = value as Double
            progressBar.max = duration.toInt()
            durationLabel.text = duration.toString()
            durationLabel.text = formatTime(progressBar.progress, progressBar.max)
            requestLayout()
        } else if (name.equals("time-pos")) {
            if (!shouldUpdateProgress) {
                return
            }

            val time = value as Double
            progressBar.progress = time.toInt()
            durationLabel.text = formatTime(progressBar.progress, progressBar.max)
        }
    }

    private fun formatTime(current: Int, total: Int): String {
        var duration: Duration = Duration.ofSeconds(current.toLong())
        var time = LocalDateTime.MIN.plus(duration)
        val part1 = time.format(dateFormatter)
        duration = Duration.ofSeconds(total.toLong())
        time = LocalDateTime.MIN.plus(duration)
        val part2 = time.format(dateFormatter)
        return "$part1 / $part2"
    }

    companion object {
        val TAG = "PlayerControlView"

        fun centerLayout(): LayoutParams {
            val centerParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            centerParams.topToTop = LayoutParams.PARENT_ID;
            centerParams.leftToLeft = LayoutParams.PARENT_ID
            centerParams.rightToRight = LayoutParams.PARENT_ID
            centerParams.bottomToBottom = LayoutParams.PARENT_ID
            return centerParams
        }

        val ICON_SMALL_SIZE = 24 * 3
        val ICON_SIZE = 32 * 3
        val ICON_TAG = 2
    }
}
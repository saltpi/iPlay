package top.ourfor.app.iPlayClient

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.Timer
import java.util.concurrent.TimeUnit

class PlayerControlView(context: Context) : ConstraintLayout(context), PlayerEventListener {
    private var shouldUpdateProgress = true;
    var player: Player? = null

    private var playButton: View = run {
        val layout = ConstraintLayout(context)
        val icon = ImageView(context)
        icon.tag = ICON_TAG
        icon.setImageResource(androidx.media3.ui.R.drawable.exo_icon_pause)
        val iconLayout = LayoutParams(CENTER_LAYOUT)
        iconLayout.width = ICON_SIZE
        iconLayout.height = ICON_SIZE
        layout.addView(icon, iconLayout)
        val gradientDrawable = GradientDrawable()
        gradientDrawable.setColor(Color.argb(50, 0, 0, 0))
        gradientDrawable.cornerRadius = ICON_SIZE + 0f
        layout.background = gradientDrawable
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

    private var fullscreenButton = run {
        val layout = ConstraintLayout(context)
        val icon = ImageView(context)
        icon.setImageResource(androidx.media3.ui.R.drawable.exo_icon_fullscreen_enter)
        val iconLayout = LayoutParams(CENTER_LAYOUT)
        iconLayout.width = ICON_SIZE
        iconLayout.height = ICON_SIZE
        layout.addView(icon, iconLayout)
        val gradientDrawable = GradientDrawable()
        gradientDrawable.setColor(Color.argb(50, 0, 0, 0))
        gradientDrawable.cornerRadius = ICON_SIZE + 0f
        layout.background = gradientDrawable
        layout.rootView
    }

    private var fullscreenLayout = run {
        val params = LayoutParams(ICON_SIZE * 2, ICON_SIZE * 2)
        params.topToTop = LayoutParams.PARENT_ID
        params.rightToRight = LayoutParams.PARENT_ID
        params
    }

    private var progressBar = run {
        val slide = SeekBar(context)
        slide.max = 9999
        slide
    }

    private var progressBarLayout = run {
        val params = LayoutParams(LayoutParams.MATCH_PARENT, 20)
        params.bottomToBottom = LayoutParams.PARENT_ID
        params.bottomMargin = 20
        params.leftToLeft = LayoutParams.PARENT_ID
        params.rightToRight = LayoutParams.PARENT_ID
        params
    }

    private var durationLabel = run {
        val label = TextView(context)
        label.textSize = 14.0F
        label
    }

    private var durationLayout = run {
        val params = LayoutParams(ICON_SIZE * 2, ICON_SIZE * 2)
        params.rightToRight = progressBar.id
        params.bottomToTop = progressBar.id
        params.bottomMargin = 10
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
    }

    private fun bind() {
        playButton.setOnClickListener {
            Log.d(TAG, "play")
            val isPlaying = player?.isPlaying == true
            var resId = if (isPlaying) androidx.media3.ui.R.drawable.exo_icon_play else androidx.media3.ui.R.drawable.exo_icon_pause
            updateIcon(playButton, resId)
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
                player?.seek(progressBar.progress.toDouble())
                TimeUnit.MILLISECONDS.sleep(500)
                shouldUpdateProgress = true;
            }
        })

    }

    private fun updateIcon(view: View, resId: Int) {
        val imageView = view.findViewWithTag<ImageView?>(ICON_TAG)
        if (imageView !is ImageView) return
        imageView?.setImageResource(resId)
    }

    override fun onPropertyChange(name: String?, value: Any?) {
        if (value == null) return
        Log.d(TAG, name + "" + value)
        if (name.equals("duration")) {
            val duration = value as Double
            progressBar.max = duration.toInt()
            durationLabel.text = duration.toString()
        } else if (name.equals("time-pos")) {
            synchronized(shouldUpdateProgress) {
                if (!shouldUpdateProgress) {
                    return
                }

                val time = value as Double
                progressBar.progress = time.toInt()
            }
        }
    }

    companion object {
        val TAG = "PlayerControlView"

        val CENTER_LAYOUT = run {
            val centerParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            centerParams.topToTop = LayoutParams.PARENT_ID;
            centerParams.leftToLeft = LayoutParams.PARENT_ID
            centerParams.rightToRight = LayoutParams.PARENT_ID
            centerParams.bottomToBottom = LayoutParams.PARENT_ID
            centerParams
        }

        val ICON_SIZE = 24 * 3
        val ICON_TAG = 2
    }
}
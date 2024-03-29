package top.ourfor.app.iPlayClient

import android.content.Context
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView

class PlayerContentView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    public lateinit var viewModel: Player
    fun initialize(configDir: String, cacheDir: String) {
        viewModel = PlayerViewModel(configDir, cacheDir)
        // we need to call write-watch-later manually
        holder.addCallback(this)
    }

    private var filePath: String? = null

    fun playFile(filePath: String) {
        this.filePath = filePath
        if (filePath != null) {
            this.viewModel.loadVideo(filePath)
        }
    }

    // Called when back button is pressed, or app is shutting down
    fun destroy() {
        viewModel.destroy()
        // Disable surface callbacks to avoid using unintialized mpv state
        holder.removeCallback(this)
    }

    var paused: Boolean? = true

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        viewModel.resize("${width}x$height")
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.w(TAG, "attaching surface")
        viewModel.attach(holder)
        if (filePath != null) {
            viewModel.loadVideo(filePath)
            filePath = null
        } else {
            viewModel.setVideoOutput("gpu")
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.w(TAG, "detaching surface")
        viewModel.detach()
    }

    companion object {
        private const val TAG = "mpv"
    }
}

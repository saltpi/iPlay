package top.ourfor.app.iPlayClient.mpv

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import top.ourfor.app.iPlayClient.Player
import top.ourfor.app.iPlayClient.PlayerViewModel

class MPVView(context: Context, attrs: AttributeSet) : SurfaceView(context, attrs), SurfaceHolder.Callback {
    public lateinit var viewModel: Player
    fun initialize(configDir: String, cacheDir: String) {
        viewModel = PlayerViewModel()
        // we need to call write-watch-later manually
        holder.addCallback(this)
    }

    private var filePath: String? = null

    fun playFile(filePath: String) {
        this.filePath = filePath
    }

    // Called when back button is pressed, or app is shutting down
    fun destroy() {
        // Disable surface callbacks to avoid using unintialized mpv state
        holder.removeCallback(this)
    }

    var paused: Boolean? = true

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.w(TAG, "attaching surface")
        viewModel.attach(holder)
        if (filePath != null) {
            viewModel.loadVideo(filePath)
            filePath = null
        } else {

        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.w(TAG, "detaching surface")
        viewModel.destroy();
    }

    companion object {
        private const val TAG = "mpv"
    }
}

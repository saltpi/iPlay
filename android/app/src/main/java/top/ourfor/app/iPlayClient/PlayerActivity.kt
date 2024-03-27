package top.ourfor.app.iPlayClient

import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class PlayerActivity : AppCompatActivity() {

    private var activityIsForeground = true
    private var autoRotationMode = "landscape"

    override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        updateOrientation(true)
        val view = BeanManager.get<PlayerView>(PlayerView::class.java)

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    override fun onDestroy() {
        activityIsForeground = false
        super.onDestroy()
    }

    private fun updateOrientation(initial: Boolean = false) {
        // screen orientation is fixed (Android TV)
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_SCREEN_PORTRAIT))
            return

        if (autoRotationMode != "auto") {
            if (!initial)
                return // don't reset at runtime
            requestedOrientation = when (autoRotationMode) {
                "landscape" -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                "portrait" -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                else -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }
}
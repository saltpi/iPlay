package top.ourfor.app.iPlayClient

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class IntentModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    override fun getName(): String {
        return "IntentModule"
    }

    @ReactMethod
    fun openUrl(url: String) {
        Log.d("IntentModule", "open: $url")
        val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val context = BeanManager.get<Context>(Context::class.java)
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.d("IntentModule", e.toString())
        }
    }

    @ReactMethod
    fun playFile(filepath: String) {
        val intent = Intent()
        intent.putExtra("filepath", filepath)
        val context = BeanManager.get<Context>(Context::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setClassName(context, "top.ourfor.app.iPlayClient.mpv.MPVActivity")
        context.startActivity(intent)
    }
}
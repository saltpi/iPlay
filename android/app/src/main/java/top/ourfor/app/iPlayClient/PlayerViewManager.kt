package top.ourfor.app.iPlayClient

import android.os.Build
import android.view.Choreographer
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp

class PlayerViewManager(
    private val reactContext: ReactApplicationContext
) : ViewGroupManager<FrameLayout>() {
    private var propWidth: Int? = null
    private var propHeight: Int? = null
    private var videoSrc: String? = null
    private var videoTitle: String? = null

    override fun getName() = REACT_CLASS

    /**
     * Return a FrameLayout which will later hold the Fragment
     */
    override fun createViewInstance(reactContext: ThemedReactContext) =
        FrameLayout(reactContext)

    /**
     * Map the "create" command to an integer
     */
    override fun getCommandsMap() = mapOf("create" to COMMAND_CREATE)

    /**
     * Handle "create" command (called from JS) and call createFragment method
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun receiveCommand(
        root: FrameLayout,
        commandId: String,
        args: ReadableArray?
    ) {
        super.receiveCommand(root, commandId, args)
        val reactNativeViewId = requireNotNull(args).getInt(0)
        propWidth = root.width
        propHeight = root.height
        when (commandId.toInt()) {
            COMMAND_CREATE -> createFragment(root, reactNativeViewId)
        }
    }

    @ReactProp(name = "url")
    fun setSrc(view: FrameLayout, url: String?) {
        videoSrc = url
    }

    @ReactProp(name = "title")
    fun setTitle(view: FrameLayout, title: String?) {
        videoTitle = title
    }

    override fun getExportedCustomBubblingEventTypeConstants(): Map<String, Any> {
        return mapOf(
            "onPlayStateChange" to mapOf(
                "phasedRegistrationNames" to mapOf(
                    "bubbled" to "onPlayStateChange"
                )
            )
        )
    }

    /**
     * Replace your React Native view with a custom fragment
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun createFragment(root: FrameLayout, reactNativeViewId: Int) {
        val parentView = root.findViewById<ViewGroup>(reactNativeViewId)
        setupLayout(parentView)

        val fragment = PlayerFragment(videoSrc)
        fragment.title = videoTitle
        fragment.themedReactContext = root.context as ThemedReactContext?
        fragment.reactContext = reactContext
        val activity = reactContext.currentActivity as FragmentActivity
        activity.supportFragmentManager
            .beginTransaction()
            .replace(reactNativeViewId, fragment, reactNativeViewId.toString())
            .commit()
    }

    fun setupLayout(view: View) {
        Choreographer.getInstance().postFrameCallback(object: Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                manuallyLayoutChildren(view)
                view.viewTreeObserver.dispatchOnGlobalLayout()
                Choreographer.getInstance().postFrameCallback(this)
            }
        })
    }

    /**
     * Layout all children properly
     */
    private fun manuallyLayoutChildren(view: View) {
        // propWidth and propHeight coming from react-native props
        val width = requireNotNull(propWidth).toInt()
        val height = requireNotNull(propHeight).toInt()

        view.measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY))

        view.layout(0, 0, width, height)
    }

    companion object {
        private const val REACT_CLASS = "PlayerViewManager"
        private const val COMMAND_CREATE = 1
    }
}
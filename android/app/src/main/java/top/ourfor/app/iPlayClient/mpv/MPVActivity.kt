package top.ourfor.app.iPlayClient.mpv

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.media.AudioManager
import android.net.Uri
import android.os.*
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import top.ourfor.app.iPlayClient.R
import top.ourfor.app.iPlayClient.databinding.PlayerBinding
import top.ourfor.lib.mpv.MPVLib

typealias ActivityResultCallback = (Int, Intent?) -> Unit
typealias StateRestoreCallback = () -> Unit

class MPVActivity : AppCompatActivity(), MPVLib.EventObserver {
    // for calls to eventUi() and eventPropertyUi()
    private val eventUiHandler = Handler(Looper.getMainLooper())

    // for use with stopServiceRunnable
    private val stopServiceHandler = Handler(Looper.getMainLooper())

    /**
     * DO NOT USE THIS
     */
    private var activityIsStopped = false

    private var activityIsForeground = true
    private var didResumeBackgroundPlayback = false

    private var audioManager: AudioManager? = null
    private var audioFocusRestore: () -> Unit = {}

    private val psc = Utils.PlaybackStateCache()
    private var mediaSession: MediaSessionCompat? = null

    private lateinit var binding: PlayerBinding
    private lateinit var toast: Toast

    // convenience alias
    private val player get() = binding.player

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { type ->
        Log.v(TAG, "Audio focus changed: $type")
        if (ignoreAudioFocus)
            return@OnAudioFocusChangeListener
        when (type) {
            AudioManager.AUDIOFOCUS_LOSS,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                // loss can occur in addition to ducking, so remember the old callback
                val oldRestore = audioFocusRestore
                val wasPlayerPaused = player.paused ?: false
                player.paused = true
                audioFocusRestore = {
                    oldRestore()
                    if (!wasPlayerPaused) player.paused = false
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                MPVLib.command(arrayOf("multiply", "volume", AUDIO_FOCUS_DUCKING.toString()))
                audioFocusRestore = {
                    val inv = 1f / AUDIO_FOCUS_DUCKING
                    MPVLib.command(arrayOf("multiply", "volume", inv.toString()))
                }
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                audioFocusRestore()
                audioFocusRestore = {}
            }
        }
    }

    private val stopServiceRunnable = Runnable {
    }

    /* Settings */
    private var statsFPS = false
    private var statsLuaMode = 0 // ==0 disabled, >0 page number

    private var backgroundPlayMode = ""
    private var noUIPauseMode = ""

    private var shouldSavePosition = false

    private var autoRotationMode = ""

    private var controlsAtBottom = true
    private var showMediaTitle = false

    private var ignoreAudioFocus = false

    private var smoothSeekGesture = false
    /* * */

    private fun initListeners() {
    }

    @SuppressLint("ShowToast")
    private fun initMessageToast() {
        toast = Toast.makeText(this, "This totally shouldn't be seen", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 0)
    }

    private var playbackHasStarted = false
    private var onloadCommands = mutableListOf<Array<String>>()

    // Activity lifetime

    override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Do these here and not in MainActivity because mpv can be launched from a file browser
        Utils.copyAssets(this)

        binding = PlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize listeners for the player view
        initListeners()

        // Initialize toast used for short messages
        initMessageToast()

        // set up initial UI state
        syncSettings()
        onConfigurationChanged(resources.configuration)
        run {
            // edge-to-edge & immersive mode
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
            insetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        updateOrientation(true)

        // Parse the intent
        val filepath = parsePathFromIntent(intent)
        if (intent.action == Intent.ACTION_VIEW) {
            parseIntentExtras(intent.extras)
        }

        player.initialize(applicationContext.filesDir.path, applicationContext.cacheDir.path)
        player.addObserver(this)
        player.playFile(filepath!!)

        mediaSession = initMediaSession()
        updateMediaSession()

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        volumeControlStream = AudioManager.STREAM_MUSIC

        @Suppress("DEPRECATION")
        val res = audioManager!!.requestAudioFocus(
            audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN
        )
        if (res != AudioManager.AUDIOFOCUS_REQUEST_GRANTED && !ignoreAudioFocus) {
            Log.w(TAG, "Audio focus not granted")
            onloadCommands.add(arrayOf("set", "pause", "yes"))
        }
    }

    override fun onDestroy() {
        Log.v(TAG, "Exiting.")

        // Suppress any further callbacks
        activityIsForeground = false

        mediaSession?.let {
            it.isActive = false
            it.release()
        }
        mediaSession = null

        @Suppress("DEPRECATION")
        audioManager?.abandonAudioFocus(audioFocusChangeListener)

        // take the background service with us
        stopServiceRunnable.run()

        player.removeObserver(this)
        player.destroy()
        super.onDestroy()
    }

    override fun onPause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (isInMultiWindowMode || isInPictureInPictureMode) {
                Log.v(TAG, "Going into multi-window mode")
                super.onPause()
                return
            }
        }

        onPauseImpl()
    }

    private fun onPauseImpl() {
    }

    private fun syncSettings() {
        // FIXME: settings should be in their own class completely
        val prefs = getDefaultSharedPreferences(applicationContext)
        val getString: (String, Int) -> String = { key, defaultRes ->
            prefs.getString(key, resources.getString(defaultRes))!!
        }

        val statsMode = prefs.getString("stats_mode", "") ?: ""
        this.statsFPS = statsMode == "native_fps"
        this.statsLuaMode = if (statsMode.startsWith("lua"))
            statsMode.removePrefix("lua").toInt()
        else
            0
        this.backgroundPlayMode = getString("background_play", R.string.pref_background_play_default)
        this.noUIPauseMode = getString("no_ui_pause", R.string.pref_no_ui_pause_default)
        this.shouldSavePosition = prefs.getBoolean("save_position", false)
        this.autoRotationMode = getString("auto_rotation", R.string.pref_auto_rotation_default)
        this.controlsAtBottom = prefs.getBoolean("bottom_controls", true)
        this.showMediaTitle = prefs.getBoolean("display_media_title", false)
        this.ignoreAudioFocus = prefs.getBoolean("ignore_audio_focus", false)
        this.smoothSeekGesture = prefs.getBoolean("seek_gesture_smooth", false)
    }

    override fun onStart() {
        super.onStart()
        activityIsStopped = false
    }

    override fun onStop() {
        super.onStop()
        activityIsStopped = true
    }

    override fun onResume() {
        // If we weren't actually in the background (e.g. multi window mode), don't reinitialize stuff
        if (activityIsForeground) {
            super.onResume()
            return
        }
        // Init controls to be hidden and view fullscreen
        syncSettings()

        activityIsForeground = true
        // stop background service with a delay
        stopServiceHandler.removeCallbacks(stopServiceRunnable)
        stopServiceHandler.postDelayed(stopServiceRunnable, 1000L)
        super.onResume()
    }

    // UI

    private var btnSelected = -1 // dpad navigation


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val isLandscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE

        // TODO: figure out if this should be replaced by WindowManager.getCurrentWindowMetrics()
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(dm)
    }


    private fun parsePathFromIntent(intent: Intent): String? {
        val filepath: String?
        filepath = when (intent.action) {
            Intent.ACTION_VIEW -> intent.data?.let { resolveUri(it) }
            Intent.ACTION_SEND -> intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                val uri = Uri.parse(it.trim())
                if (uri.isHierarchical && !uri.isRelative) resolveUri(uri) else null
            }
            else -> intent.getStringExtra("filepath")
        }
        return filepath
    }

    private fun resolveUri(data: Uri): String? {
        val filepath = when (data.scheme) {
            "file" -> data.path
            "content" -> openContentFd(data)
            "http", "https", "rtmp", "rtmps", "rtp", "rtsp", "mms", "mmst", "mmsh", "tcp", "udp", "lavf"
            -> data.toString()
            else -> null
        }

        if (filepath == null)
            Log.e(TAG, "unknown scheme: ${data.scheme}")
        return filepath
    }

    private fun openContentFd(uri: Uri): String? {
        val resolver = applicationContext.contentResolver
        Log.v(TAG, "Resolving content URI: $uri")
        val fd = try {
            val desc = resolver.openFileDescriptor(uri, "r")
            desc!!.detachFd()
        } catch(e: Exception) {
            Log.e(TAG, "Failed to open content fd: $e")
            return null
        }
        // See if we skip the indirection and read the real file directly
        val path = Utils.findRealPath(fd)
        if (path != null) {
            Log.v(TAG, "Found real file path: $path")
            ParcelFileDescriptor.adoptFd(fd).close() // we don't need that anymore
            return path
        }
        // Else, pass the fd to mpv
        return "fd://${fd}"
    }

    private fun parseIntentExtras(extras: Bundle?) {
        onloadCommands.clear()
        if (extras == null)
            return

        // Refer to http://mpv-android.github.io/mpv-android/intent.html
        if (extras.getByte("decode_mode") == 2.toByte())
            onloadCommands.add(arrayOf("set", "file-local-options/hwdec", "no"))
        if (extras.containsKey("subs")) {
            val subList = extras.getParcelableArray("subs")?.mapNotNull { it as? Uri } ?: emptyList()
            val subsToEnable = extras.getParcelableArray("subs.enable")?.mapNotNull { it as? Uri } ?: emptyList()

            for (suburi in subList) {
                val subfile = resolveUri(suburi) ?: continue
                val flag = if (subsToEnable.filter { it.compareTo(suburi) == 0 }.any()) "select" else "auto"

                Log.v(TAG, "Adding subtitles from intent extras: $subfile")
                onloadCommands.add(arrayOf("sub-add", subfile, flag))
            }
        }
        if (extras.getInt("position", 0) > 0) {
            val pos = extras.getInt("position", 0) / 1000f
            onloadCommands.add(arrayOf("set", "start", pos.toString()))
        }
    }

    // UI (Part 2)

    data class TrackData(val track_id: Int, val track_type: String)
    private fun trackSwitchNotification(f: () -> TrackData) {
        val (track_id, track_type) = f()
        val trackPrefix = when (track_type) {
            "audio" -> getString(R.string.track_audio)
            "sub"   -> getString(R.string.track_subs)
            "video" -> "Video"
            else    -> "???"
        }

        val msg = if (track_id == -1) {
            "$trackPrefix ${getString(R.string.track_off)}"
        } else {
            val trackName = player.tracks[track_type]?.firstOrNull{ it.mpvId == track_id }?.name ?: "???"
            "$trackPrefix $trackName"
        }
    }



    private var activityResultCallbacks: MutableMap<Int, ActivityResultCallback> = mutableMapOf()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityResultCallbacks.remove(requestCode)?.invoke(resultCode, data)
    }

    private fun updatePlaybackStatus(paused: Boolean) {
        updatePiPParams()
        if (paused)
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        else
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun updateDecoderButton() {
    }


    private fun updatePlaylistButtons() {

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
        if (initial || player.vid == -1)
            return

        val ratio = player.videoAspect?.toFloat() ?: 0f
        Log.v(TAG, "auto rotation: aspect ratio = $ratio")

        if (ratio == 0f || ratio in (1f / ASPECT_RATIO_MIN) .. ASPECT_RATIO_MIN) {
            // video is square, let Android do what it wants
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            return
        }
        requestedOrientation = if (ratio > 1f)
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        else
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
    }

    private fun updatePiPParams(force: Boolean = false) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return
        if (!isInPictureInPictureMode && !force)
            return
    }

    // Media Session handling

    private fun initMediaSession(): MediaSessionCompat {
        /*
            https://developer.android.com/guide/topics/media-apps/working-with-a-media-session
            https://developer.android.com/guide/topics/media-apps/audio-app/mediasession-callbacks
            https://developer.android.com/reference/android/support/v4/media/session/MediaSessionCompat
         */
        val session = MediaSessionCompat(this, TAG)
        session.setFlags(0)
        session.setCallback(object : MediaSessionCompat.Callback() {
            override fun onPause() {
                player.paused = true
            }
            override fun onPlay() {
                player.paused = false
            }
            override fun onSeekTo(pos: Long) {
                player.timePos = (pos / 1000).toInt()
            }
            override fun onSkipToNext() {
                MPVLib.command(arrayOf("playlist-next"))
            }
            override fun onSkipToPrevious() {
                MPVLib.command(arrayOf("playlist-prev"))
            }
            override fun onSetRepeatMode(repeatMode: Int) {
                MPVLib.setPropertyString("loop-playlist",
                    if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ALL) "inf" else "no")
                MPVLib.setPropertyString("loop-file",
                    if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ONE) "inf" else "no")
            }
            override fun onSetShuffleMode(shuffleMode: Int) {
                player.changeShuffle(false, shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL)
            }
        })
        return session
    }

    private fun updateMediaSession() {
        synchronized (psc) {
            mediaSession?.let { psc.write(it) }
        }
    }

    // mpv events

    private fun eventPropertyUi(property: String) {
        if (!activityIsForeground) return
        when (property) {
            "track-list" -> player.loadTracks()
            "video-params/aspect" -> {
                updateOrientation()
                updatePiPParams()
            }
            "hwdec-current" -> updateDecoderButton()
        }
    }

    private fun eventPropertyUi(property: String, value: Boolean) {
        if (!activityIsForeground) return
        when (property) {
            "pause" -> updatePlaybackStatus(value)
        }
    }

    private fun eventPropertyUi(property: String, value: Long) {
    }

    private fun eventPropertyUi(property: String, value: String, triggerMetaUpdate: Boolean) {
        if (!activityIsForeground) return
    }

    private fun eventUi(eventId: Int) {
        if (!activityIsForeground) return
        when (eventId) {
            MPVLib.mpvEventId.MPV_EVENT_PLAYBACK_RESTART -> updatePlaybackStatus(player.paused!!)
        }
    }

    override fun eventProperty(property: String) {
        if (property == "loop-file" || property == "loop-playlist") {
            mediaSession?.setRepeatMode(when (player.getRepeat()) {
                2 -> PlaybackStateCompat.REPEAT_MODE_ONE
                1 -> PlaybackStateCompat.REPEAT_MODE_ALL
                else -> PlaybackStateCompat.REPEAT_MODE_NONE
            })
        }

        if (!activityIsForeground) return
        eventUiHandler.post { eventPropertyUi(property) }
    }

    override fun eventProperty(property: String, value: Boolean) {
        if (psc.update(property, value))
            updateMediaSession()
        if (property == "shuffle") {
            mediaSession?.setShuffleMode(if (value)
                PlaybackStateCompat.SHUFFLE_MODE_ALL
            else
                PlaybackStateCompat.SHUFFLE_MODE_NONE)
        }

        if (!activityIsForeground) return
        eventUiHandler.post { eventPropertyUi(property, value) }
    }

    override fun eventProperty(property: String, value: Long) {
        if (psc.update(property, value))
            updateMediaSession()

        if (!activityIsForeground) return
        eventUiHandler.post { eventPropertyUi(property, value) }
    }

    override fun eventProperty(property: String, value: String) {
        val triggerMetaUpdate = psc.update(property, value)
        if (triggerMetaUpdate)
            updateMediaSession()

        if (!activityIsForeground) return
        eventUiHandler.post { eventPropertyUi(property, value, triggerMetaUpdate) }
    }

    override fun event(eventId: Int) {
        if (eventId == MPVLib.mpvEventId.MPV_EVENT_START_FILE) {
            for (c in onloadCommands)
                MPVLib.command(c)
            if (this.statsLuaMode > 0 && !playbackHasStarted) {
                MPVLib.command(arrayOf("script-binding", "stats/display-stats-toggle"))
                MPVLib.command(arrayOf("script-binding", "stats/${this.statsLuaMode}"))
            }

            playbackHasStarted = true
        }

        if (!activityIsForeground) return
        eventUiHandler.post { eventUi(eventId) }
    }

    // Gesture handler

    companion object {
        const val TAG = "mpv"
        // how long should controls be displayed on screen (ms)
        private const val CONTROLS_DISPLAY_TIMEOUT = 1500L
        // how long controls fade to disappear (ms)
        private const val CONTROLS_FADE_DURATION = 500L
        // resolution (px) of the thumbnail displayed with playback notification
        private const val THUMB_SIZE = 384
        // smallest aspect ratio that is considered non-square
        private const val ASPECT_RATIO_MIN = 1.2f // covers 5:4 and up
        // fraction to which audio volume is ducked on loss of audio focus
        private const val AUDIO_FOCUS_DUCKING = 0.5f
        // request codes for invoking other activities
        private const val RCODE_EXTERNAL_AUDIO = 1000
        private const val RCODE_EXTERNAL_SUB = 1001
        private const val RCODE_LOAD_FILE = 1002
        // action of result intent
        private const val RESULT_INTENT = "top.ourfor.app.iPlayClient.mpv.MPVActivity.result"
    }
}

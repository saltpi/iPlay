package top.ourfor.app.iPlayClient.mpv

import android.content.Context
import android.content.res.AssetManager
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.View
import android.view.ViewGroup
import top.ourfor.lib.mpv.MPVLib
import java.io.*

object Utils {
    fun copyAssets(context: Context) {
        val assetManager = context.assets
        val files = arrayOf("subfont.ttf", "cacert.pem")
        val configDir = context.filesDir.path
        for (filename in files) {
            var ins: InputStream? = null
            var out: OutputStream? = null
            try {
                ins = assetManager.open(filename, AssetManager.ACCESS_STREAMING)
                val outFile = File("$configDir/$filename")
                // Note that .available() officially returns an *estimated* number of bytes available
                // this is only true for generic streams, asset streams return the full file size
                if (outFile.length() == ins.available().toLong()) {
                    Log.v(TAG, "Skipping copy of asset file (exists same size): $filename")
                    continue
                }
                out = FileOutputStream(outFile)
                ins.copyTo(out)
                Log.w(TAG, "Copied asset file: $filename")
            } catch (e: IOException) {
                Log.e(TAG, "Failed to copy asset file: $filename", e)
            } finally {
                ins?.close()
                out?.close()
            }
        }
    }

    fun findRealPath(fd: Int): String? {
        var ins: InputStream? = null
        try {
            val path = File("/proc/self/fd/${fd}").canonicalPath
            if (!path.startsWith("/proc") && File(path).canRead()) {
                // Double check that we can read it
                ins = FileInputStream(path)
                ins.read()
                return path
            }
        } catch(e: Exception) { } finally { ins?.close() }
        return null
    }

    fun visibleChildren(view: View): Int {
        if (view is ViewGroup && view.visibility == View.VISIBLE) {
            return (0 until view.childCount).sumOf { visibleChildren(view.getChildAt(it)) }
        }
        return if (view.visibility == View.VISIBLE) 1 else 0
    }

    class AudioMetadata {
        var mediaTitle: String? = null
            private set
        var mediaArtist: String? = null
            private set
        var mediaAlbum: String? = null
            private set

        fun readAll() {
            mediaTitle = MPVLib.getPropertyString("media-title")
            mediaArtist = MPVLib.getPropertyString("metadata/by-key/Artist")
            mediaAlbum = MPVLib.getPropertyString("metadata/by-key/Album")
        }

        fun update(property: String, value: String): Boolean {
            when (property) {
                "media-title" -> mediaTitle = value
                "metadata/by-key/Artist" -> mediaArtist = value
                "metadata/by-key/Album" -> mediaAlbum = value
                else -> return false
            }
            return true
        }

        fun formatTitle(): String? = if (!mediaTitle.isNullOrEmpty()) mediaTitle else null

        fun formatArtistAlbum(): String? {
            val artistEmpty = mediaArtist.isNullOrEmpty()
            val albumEmpty = mediaAlbum.isNullOrEmpty()
            return when {
                !artistEmpty && !albumEmpty -> "$mediaArtist / $mediaAlbum"
                !artistEmpty -> mediaAlbum
                !albumEmpty -> mediaArtist
                else -> null
            }
        }
    }

    // does about 200% more than AudioMetadata
    class PlaybackStateCache {
        val meta = AudioMetadata()
        var cachePause = false
            private set
        var pause = false
            private set
        var position = -1L // in ms
            private set
        var duration = 0L // in ms
            private set
        var playlistPos = 0
            private set
        var playlistCount = 0
            private set

        val position_s get() = (position / 1000).toInt()
        val duration_s get() = (duration / 1000).toInt()

        fun reset() {
            position = -1
            duration = 0
        }

        fun update(property: String, value: String): Boolean {
            if (meta.update(property, value))
                return true
            return false
        }

        fun update(property: String, value: Boolean): Boolean {
            when (property) {
                "pause" -> pause = value
                "paused-for-cache" -> cachePause = value
                else -> return false
            }
            return true
        }

        fun update(property: String, value: Long): Boolean {
            when (property) {
                "time-pos" -> position = value * 1000
                "duration" -> duration = value * 1000
                "playlist-pos" -> playlistPos = value.toInt()
                "playlist-count" -> playlistCount = value.toInt()
                else -> return false
            }
            return true
        }

        private var mediaMetadataBuilder = MediaMetadataCompat.Builder()
        private var playbackStateBuilder = PlaybackStateCompat.Builder()

        private fun buildMediaMetadata(includeThumb: Boolean): MediaMetadataCompat {
            // TODO could provide: genre, num_tracks, track_number, year
            return with (mediaMetadataBuilder) {
                build()
            }
        }

        private fun buildPlaybackState(): PlaybackStateCompat {
            val stateInt = when {
                position < 0 || duration <= 0 -> PlaybackStateCompat.STATE_NONE
                cachePause -> PlaybackStateCompat.STATE_BUFFERING
                pause -> PlaybackStateCompat.STATE_PAUSED
                else -> PlaybackStateCompat.STATE_PLAYING
            }
            var actions = PlaybackStateCompat.ACTION_PLAY or
                    PlaybackStateCompat.ACTION_PLAY_PAUSE or
                    PlaybackStateCompat.ACTION_PAUSE or
                    PlaybackStateCompat.ACTION_SET_REPEAT_MODE
            if (duration > 0)
                actions = actions or PlaybackStateCompat.ACTION_SEEK_TO
            if (playlistCount > 1) {
                // we could be very pedantic here but it's probably better to either show both or none
                actions = actions or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                        PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE
            }
            return with (playbackStateBuilder) {
                setState(stateInt, position, 1.0f)
                setActions(actions)
                //setActiveQueueItemId(0) TODO
                build()
            }
        }

        fun write(session: MediaSessionCompat, includeThumb: Boolean = true) {
            with (session) {
                setMetadata(buildMediaMetadata(includeThumb))
                val ps = buildPlaybackState()
                isActive = ps.state != PlaybackStateCompat.STATE_NONE
                setPlaybackState(ps)
                //setQueue(listOf()) TODO
            }
        }
    }

    private const val TAG = "mpv"

}

package org.rtsda.android.presentation.video

import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.upstream.DefaultLoadErrorHandlingPolicy
import dagger.hilt.android.AndroidEntryPoint
import org.rtsda.android.R
import org.rtsda.android.databinding.ActivityVideoPlayerBinding
import org.rtsda.android.viewmodels.MessagesViewModel
import javax.inject.Inject

@AndroidEntryPoint
class VideoPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoPlayerBinding
    private var player: ExoPlayer? = null
    private var videoUrl: String? = null
    private var playWhenReady: Boolean = true
    private var currentPosition: Long = 0
    
    private val messagesViewModel: MessagesViewModel by viewModels()
    
    @Inject
    lateinit var httpDataSourceFactory: DefaultHttpDataSource.Factory
    
    private val pipActionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_PLAY -> player?.play()
                ACTION_PAUSE -> player?.pause()
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Get the video URL from either the intent or saved state
        videoUrl = savedInstanceState?.getString(EXTRA_VIDEO_URL) ?: intent.getStringExtra(EXTRA_VIDEO_URL)
        if (videoUrl == null) {
            finish()
            return
        }
        
        hideSystemUi()
        binding.backButton.setOnClickListener { 
            finish()
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(pipActionReceiver, IntentFilter().apply {
                addAction(ACTION_PLAY)
                addAction(ACTION_PAUSE)
            }, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(pipActionReceiver, IntentFilter().apply {
                addAction(ACTION_PLAY)
                addAction(ACTION_PAUSE)
            })
        }
        
        initializePlayer()
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EXTRA_VIDEO_URL, videoUrl)
    }
    
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        
        // Get the new video URL
        val newVideoUrl = intent?.getStringExtra(EXTRA_VIDEO_URL)
        if (newVideoUrl == null) {
            finish()
            return
        }
        
        // Reset playback state
        playWhenReady = true
        currentPosition = 0
        
        // If we're in PiP mode, close it first
        if (isInPictureInPictureMode) {
            // Release the current player
            releasePlayer()
            // Update the video URL
            videoUrl = newVideoUrl
            // Exit PiP mode first
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val params = PictureInPictureParams.Builder()
                    .setAspectRatio(Rational(16, 9))
                    .build()
                setPictureInPictureParams(params)
                // Instead of moveTaskToBack, just finish the activity
                finish()
                // Start a new instance of the activity
                val newIntent = Intent(this, VideoPlayerActivity::class.java).apply {
                    putExtra(EXTRA_VIDEO_URL, newVideoUrl)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(newIntent)
            }
        } else {
            // If not in PiP mode, just update the video
            videoUrl = newVideoUrl
            releasePlayer()
            initializePlayer()
        }
    }
    
    override fun onStart() {
        super.onStart()
        if (!isInPictureInPictureMode) {
            initializePlayer()
        }
    }
    
    override fun onResume() {
        super.onResume()
        if (!isInPictureInPictureMode) {
            hideSystemUi()
            initializePlayer()
        }
    }
    
    override fun onPause() {
        super.onPause()
        if (!isInPictureInPictureMode) {
            releasePlayer()
        }
    }
    
    override fun onStop() {
        super.onStop()
        if (!isInPictureInPictureMode) {
            releasePlayer()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (!isInPictureInPictureMode) {
            releasePlayer()
            messagesViewModel.onPlayerClosed()
        }
        unregisterReceiver(pipActionReceiver)
    }
    
    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        binding.playerView.useController = !isInPictureInPictureMode
        binding.backButton.visibility = if (isInPictureInPictureMode) View.GONE else View.VISIBLE
        
        if (!isInPictureInPictureMode) {
            // When exiting PiP mode, just restore the UI
            hideSystemUi()
        }
        
        // Update the ViewModel with the current PiP state
        if (player != null) {
            messagesViewModel.updatePlaybackState(MessagesViewModel.PlaybackState.Playing(videoUrl!!, isInPictureInPictureMode))
        }
    }
    
    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    
    private fun initializePlayer() {
        if (player == null) {
            Log.d("VideoPlayer", "Initializing player with URL: $videoUrl")
            
            // Validate URL
            if (videoUrl.isNullOrEmpty()) {
                showError("Invalid video URL")
                return
            }

            // Configure HTTP data source with logging
            val dataSourceFactory = DefaultHttpDataSource.Factory()
                .setAllowCrossProtocolRedirects(true)
                .setConnectTimeoutMs(30000)
                .setReadTimeoutMs(30000)
                .setUserAgent("RTSDA-Android")
                .setTransferListener(object : androidx.media3.datasource.TransferListener {
                    override fun onTransferInitializing(
                        source: androidx.media3.datasource.DataSource,
                        dataSpec: androidx.media3.datasource.DataSpec,
                        isNetwork: Boolean
                    ) {
                        Log.d("VideoPlayer", "Transfer initializing for URL: ${dataSpec.uri}")
                    }

                    override fun onTransferStart(
                        source: androidx.media3.datasource.DataSource,
                        dataSpec: androidx.media3.datasource.DataSpec,
                        isNetwork: Boolean
                    ) {
                        Log.d("VideoPlayer", "Transfer started for URL: ${dataSpec.uri}")
                    }

                    override fun onBytesTransferred(
                        source: androidx.media3.datasource.DataSource,
                        dataSpec: androidx.media3.datasource.DataSpec,
                        isNetwork: Boolean,
                        bytesTransferred: Int
                    ) {
                        // Not needed for our use case
                    }

                    override fun onTransferEnd(
                        source: androidx.media3.datasource.DataSource,
                        dataSpec: androidx.media3.datasource.DataSpec,
                        isNetwork: Boolean
                    ) {
                        if (source is DefaultHttpDataSource) {
                            Log.d("VideoPlayer", "Transfer ended for URL: ${dataSpec.uri}")
                            Log.d("VideoPlayer", "Response code: ${source.responseCode}")
                            Log.d("VideoPlayer", "Response headers: ${source.responseHeaders}")
                            val contentType = source.responseHeaders.entries
                                .find { it.key.equals("Content-Type", ignoreCase = true) }
                                ?.value
                            Log.d("VideoPlayer", "Content type: $contentType")
                        }
                    }
                })

            player = ExoPlayer.Builder(this)
                .setLoadControl(
                    androidx.media3.exoplayer.DefaultLoadControl.Builder()
                        .setBufferDurationsMs(
                            30000,  // Minimum buffer duration in milliseconds
                            60000,  // Maximum buffer duration in milliseconds
                            5000,   // Buffer duration for playback in milliseconds
                            10000   // Buffer duration after rebuffering in milliseconds
                        )
                        .setPrioritizeTimeOverSizeThresholds(true)
                        .build()
                )
                .setRenderersFactory(
                    DefaultRenderersFactory(this)
                        .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
                        .setMediaCodecSelector(MediaCodecSelector.DEFAULT)
                )
                .build()
                .also { exoPlayer ->
                    // Set up the player view first
                    binding.playerView.player = exoPlayer
                    binding.playerView.controllerHideOnTouch = true
                    binding.playerView.controllerAutoShow = true
                    
                    // Set up controller visibility listener
                    binding.playerView.setControllerVisibilityListener(object : PlayerView.ControllerVisibilityListener {
                        override fun onVisibilityChanged(visibility: Int) {
                            binding.pipButton.visibility = if (visibility == View.VISIBLE && 
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) View.VISIBLE else View.GONE
                        }
                    })
                    
                    // Set up PiP button
                    binding.pipButton.setOnClickListener {
                        enterPiPMode()
                    }
                    
                    // Create a new MediaItem for the current video URL
                    val mediaItem = MediaItem.Builder()
                        .setUri(videoUrl!!)
                        .setDrmConfiguration(null) // Disable DRM
                        .build()
                    
                    exoPlayer.setMediaItem(mediaItem)
                    
                    // Always start from the beginning
                    exoPlayer.seekTo(0)
                    exoPlayer.playWhenReady = true
                    exoPlayer.prepare()
                    
                    // Add player listener
                    exoPlayer.addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            when (playbackState) {
                                Player.STATE_ENDED -> {
                                    finish()
                                }
                                Player.STATE_READY -> {
                                    Log.d("VideoPlayer", "Player is ready")
                                }
                                Player.STATE_BUFFERING -> {
                                    Log.d("VideoPlayer", "Player is buffering")
                                }
                                Player.STATE_IDLE -> {
                                    Log.d("VideoPlayer", "Player is idle")
                                }
                            }
                        }
                        
                        override fun onPlayerError(error: PlaybackException) {
                            Log.e("VideoPlayer", "Playback error: ${error.message}")
                            Log.e("VideoPlayer", "Error type: ${error.errorCodeName}")
                            Log.e("VideoPlayer", "Error code: ${error.errorCode}")
                            Log.e("VideoPlayer", "Error cause: ${error.cause?.message}")
                            
                            val errorMessage = when (error.errorCode) {
                                PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED -> {
                                    "This video format is not supported. Please try a different video or contact support."
                                }
                                PlaybackException.ERROR_CODE_IO_UNSPECIFIED -> "Network error. Please check your connection."
                                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> "Network connection failed. Please check your connection."
                                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> "Connection timed out. Please try again."
                                PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND -> "Video file not found."
                                PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS -> "Server error. Please try again later."
                                PlaybackException.ERROR_CODE_IO_INVALID_HTTP_CONTENT_TYPE -> "Invalid video format. Please contact support."
                                PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED -> "Video file is corrupted or in an unsupported format."
                                else -> "An error occurred during playback: ${error.message}"
                            }
                            
                            showError(errorMessage)
                        }
                    })
                }
        }
    }
    
    private fun determineMimeType(url: String): String {
        // First try to get from URL extension
        val extension = url.substringAfterLast('.', "").lowercase()
        val mimeTypeFromExtension = when (extension) {
            "mp4" -> "video/mp4"
            "webm" -> "video/webm"
            "mkv" -> "video/x-matroska"
            "3gp" -> "video/3gpp"
            "mov" -> "video/quicktime"
            "avi" -> "video/x-msvideo"
            "wmv" -> "video/x-ms-wmv"
            "flv" -> "video/x-flv"
            "m4v" -> "video/x-m4v"
            "mpeg" -> "video/mpeg"
            "mpg" -> "video/mpeg"
            "ts" -> "video/mp2t"
            else -> null
        }
        
        if (mimeTypeFromExtension != null) {
            Log.d("VideoPlayer", "Determined MIME type from extension: $mimeTypeFromExtension")
            return mimeTypeFromExtension
        }
        
        // Default to mp4 if we can't determine
        Log.d("VideoPlayer", "Using default MIME type: video/mp4")
        return "video/mp4"
    }
    
    private fun showError(message: String) {
        binding.errorText.text = message
        binding.errorText.visibility = View.VISIBLE
        binding.playerView.visibility = View.GONE
        binding.pipButton.visibility = View.GONE
    }
    
    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playWhenReady = exoPlayer.playWhenReady
            currentPosition = exoPlayer.currentPosition
            exoPlayer.release()
            player = null
        }
    }
    
    private fun enterPiPMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Save the current playback state
            player?.let { exoPlayer ->
                playWhenReady = exoPlayer.playWhenReady
                currentPosition = exoPlayer.currentPosition
            }
            
            val params = PictureInPictureParams.Builder()
                .setAspectRatio(Rational(16, 9))
                .setActions(listOf(
                    RemoteAction(
                        Icon.createWithResource(this, R.drawable.ic_play),
                        "Play",
                        "Play video",
                        PendingIntent.getBroadcast(
                            this,
                            0,
                            Intent(ACTION_PLAY).setPackage(packageName),
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    ),
                    RemoteAction(
                        Icon.createWithResource(this, R.drawable.ic_pause),
                        "Pause",
                        "Pause video",
                        PendingIntent.getBroadcast(
                            this,
                            1,
                            Intent(ACTION_PAUSE).setPackage(packageName),
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                ))
                .build()
            
            try {
                enterPictureInPictureMode(params)
                // Update ViewModel with PiP state
                messagesViewModel.updatePlaybackState(MessagesViewModel.PlaybackState.Playing(videoUrl!!, true))
            } catch (e: IllegalStateException) {
                Log.e("VideoPlayer", "Failed to enter PiP mode: ${e.message}")
            }
        }
    }
    
    companion object {
        const val EXTRA_VIDEO_URL = "extra_video_url"
        private const val ACTION_PLAY = "org.rtsda.android.ACTION_PLAY"
        private const val ACTION_PAUSE = "org.rtsda.android.ACTION_PAUSE"
    }
} 
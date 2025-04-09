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
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
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
                    binding.playerView.player = exoPlayer
                    binding.playerView.controllerHideOnTouch = true
                    binding.playerView.controllerAutoShow = true
                    binding.playerView.setControllerVisibilityListener(object : PlayerView.ControllerVisibilityListener {
                        override fun onVisibilityChanged(visibility: Int) {
                            binding.pipButton.visibility = if (visibility == View.VISIBLE && 
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) View.VISIBLE else View.GONE
                        }
                    })
                    
                    binding.pipButton.setOnClickListener {
                        enterPiPMode()
                    }
                    
                    // Create a new MediaItem for the current video URL
                    val mediaItem = MediaItem.fromUri(videoUrl!!)
                    exoPlayer.setMediaItem(mediaItem)
                    
                    // Always start from the beginning
                    exoPlayer.seekTo(0)
                    exoPlayer.playWhenReady = true
                    exoPlayer.prepare()
                    
                    exoPlayer.addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            if (playbackState == Player.STATE_ENDED) {
                                finish()
                            }
                        }
                        
                        override fun onPlayerError(error: PlaybackException) {
                            Log.e("VideoPlayer", "Playback error: ${error.message}")
                            finish()
                        }
                    })
                }
        }
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
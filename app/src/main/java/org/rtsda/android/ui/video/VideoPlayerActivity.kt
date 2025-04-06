package org.rtsda.android.ui.video

import android.app.PictureInPictureParams
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import dagger.hilt.android.AndroidEntryPoint
import org.rtsda.android.databinding.ActivityVideoPlayerBinding
import javax.inject.Inject

@AndroidEntryPoint
class VideoPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoPlayerBinding
    private var player: ExoPlayer? = null
    private var isInPiPMode = false
    private var currentPosition: Long = 0
    private var isPlaying = false
    private var videoUrl: String? = null
    private var shouldResumePlayback = false
    private var wasInPiPMode = false
    private var mediaSource: ProgressiveMediaSource? = null
    private var isActivityStopped = false
    private var surfaceDestroyed = false
    
    @Inject
    lateinit var httpDataSourceFactory: DefaultHttpDataSource.Factory
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // If we're in PiP mode and this is a new instance, just finish
        if (isInPiPMode && intent.flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0) {
            finish()
            return
        }
        
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Restore state if available
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getLong(KEY_POSITION)
            isPlaying = savedInstanceState.getBoolean(KEY_IS_PLAYING)
            videoUrl = savedInstanceState.getString(KEY_VIDEO_URL)
            shouldResumePlayback = isPlaying
            wasInPiPMode = savedInstanceState.getBoolean(KEY_WAS_IN_PIP)
        } else {
            videoUrl = intent.getStringExtra(EXTRA_VIDEO_URL)
            shouldResumePlayback = true
        }
        
        if (videoUrl == null) {
            finish()
            return
        }
        
        // Hide system UI for immersive mode
        hideSystemUi()
        
        // Setup back button
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        
        // Initialize player
        initializePlayer(videoUrl!!)
    }
    
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        
        // Update video URL if provided
        val newVideoUrl = intent?.getStringExtra(EXTRA_VIDEO_URL)
        if (newVideoUrl != null) {
            // If it's the same video, just bring the activity to front
            if (newVideoUrl == videoUrl) {
                // Reset the activity state
                isInPiPMode = false
                binding.playerView.useController = true
                binding.playerView.showController()
                binding.backButton.visibility = View.VISIBLE
                return
            }
            
            // If it's a different video, update the player
            videoUrl = newVideoUrl
            releasePlayer()
            initializePlayer(newVideoUrl)
            
            // Exit PiP mode since we're playing a new video
            if (isInPiPMode) {
                isInPiPMode = false
                binding.playerView.useController = true
                binding.playerView.showController()
                binding.backButton.visibility = View.VISIBLE
            }
        }
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        player?.let {
            outState.putLong(KEY_POSITION, it.currentPosition)
            outState.putBoolean(KEY_IS_PLAYING, it.isPlaying)
        }
        outState.putString(KEY_VIDEO_URL, videoUrl)
        outState.putBoolean(KEY_WAS_IN_PIP, isInPiPMode)
    }
    
    override fun onResume() {
        super.onResume()
        isActivityStopped = false
        
        // Only initialize if we don't have a player
        if (player == null) {
            initializePlayer(videoUrl!!)
        } else {
            // Just reattach the player to the view
            binding.playerView.player = player
        }
    }
    
    override fun onPause() {
        super.onPause()
        if (!isInPiPMode) {
            player?.let {
                currentPosition = it.currentPosition
                isPlaying = it.isPlaying
                it.playWhenReady = false
            }
        }
    }
    
    override fun onStop() {
        super.onStop()
        isActivityStopped = true
        if (!isInPiPMode && !isChangingConfigurations) {
            releasePlayer()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (!isChangingConfigurations) {
            releasePlayer()
        }
    }
    
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !isInPiPMode) {
            enterPictureInPictureMode(PictureInPictureParams.Builder()
                .setAspectRatio(Rational(16, 9))
                .build())
        }
    }
    
    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        isInPiPMode = isInPictureInPictureMode
        
        if (isInPictureInPictureMode) {
            // Hide UI elements in PiP mode
            binding.playerView.useController = false
            binding.backButton.visibility = View.GONE
            
            // Save current state and ensure player is playing in PiP mode
            player?.let {
                currentPosition = it.currentPosition
                isPlaying = it.isPlaying
                it.playWhenReady = true
            }
        } else {
            // Only restore full-screen UI if we're not in the background
            if (!isActivityStopped) {
                binding.playerView.useController = true
                binding.playerView.showController()
                binding.backButton.visibility = View.VISIBLE
            }
            
            // Let the player handle the surface transition naturally
            player?.let {
                // Don't seek or change play state here - let the player handle it
                // The surface will be reattached automatically by PlayerView
            }
        }
    }
    
    override fun onBackPressed() {
        if (isInPiPMode) {
            // If in PiP mode, just finish this activity
            finish()
        } else {
            // If not in PiP mode, go back to previous screen
            super.onBackPressed()
        }
    }
    
    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        
        // Keep screen on while playing
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    
    private fun initializePlayer(videoUrl: String) {
        // Create media source if not already created
        if (mediaSource == null) {
            mediaSource = ProgressiveMediaSource.Factory(httpDataSourceFactory)
                .createMediaSource(MediaItem.fromUri(videoUrl))
        }
        
        if (player == null) {
            player = ExoPlayer.Builder(this)
                .setLoadControl(
                    androidx.media3.exoplayer.DefaultLoadControl.Builder()
                        .setBufferDurationsMs(
                            15000,  // Minimum buffer duration in milliseconds
                            50000,  // Maximum buffer duration in milliseconds
                            2500,   // Buffer duration for playback in milliseconds
                            5000    // Buffer duration after rebuffering in milliseconds
                        )
                        .build()
                )
                .build()
                .also { exoPlayer ->
                    binding.playerView.player = exoPlayer
                    
                    // Prepare player with existing media source
                    exoPlayer.setMediaSource(mediaSource!!)
                    exoPlayer.prepare()
                    
                    // Add player listeners
                    exoPlayer.addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            when (playbackState) {
                                Player.STATE_BUFFERING -> {
                                    binding.bufferingIndicator.visibility = View.VISIBLE
                                }
                                Player.STATE_READY -> {
                                    binding.bufferingIndicator.visibility = View.GONE
                                    if (currentPosition > 0) {
                                        exoPlayer.seekTo(currentPosition)
                                    }
                                    if (shouldResumePlayback) {
                                        exoPlayer.playWhenReady = true
                                    }
                                }
                                Player.STATE_ENDED -> {
                                    // Video playback has ended, close the player
                                    finish()
                                }
                            }
                        }
                    })
                }
        }
    }
    
    private fun releasePlayer() {
        player?.let {
            it.stop()
            it.release()
        }
        player = null
        // Don't release mediaSource to maintain buffer
    }
    
    companion object {
        const val EXTRA_VIDEO_URL = "extra_video_url"
        private const val KEY_POSITION = "position"
        private const val KEY_IS_PLAYING = "is_playing"
        private const val KEY_VIDEO_URL = "video_url"
        private const val KEY_WAS_IN_PIP = "was_in_pip"
    }
} 
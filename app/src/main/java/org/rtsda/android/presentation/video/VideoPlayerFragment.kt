package org.rtsda.android.presentation.video

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.rtsda.android.databinding.FragmentVideoPlayerBinding

@AndroidEntryPoint
class VideoPlayerFragment : Fragment() {

    private var _binding: FragmentVideoPlayerBinding? = null
    private val binding get() = _binding!!

    private var player: ExoPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val videoUrl = arguments?.getString("videoUrl") ?: return
        initializePlayer(videoUrl)

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initializePlayer(videoUrl: String) {
        player = ExoPlayer.Builder(requireContext()).build().also { exoPlayer ->
            binding.playerView.player = exoPlayer
            val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onResume() {
        super.onResume()
        player?.play()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.release()
        player = null
        _binding = null
    }
} 
package com.scoutmotors.musicplayer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.scoutmotors.musicplayer.R
import com.scoutmotors.musicplayer.databinding.FragmentMusicPlayerBinding
import com.scoutmotors.musicplayer.databinding.LayoutCustomMediaPlayerBinding
import com.scoutmotors.musicplayer.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicPlayerFragment: Fragment() {

    private val viewModel: MainActivityViewModel by activityViewModels()

    private lateinit var binding: FragmentMusicPlayerBinding

    private lateinit var customMediaPlayerViewBinding: LayoutCustomMediaPlayerBinding

    private var player: ExoPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMusicPlayerBinding.inflate(inflater)
        customMediaPlayerViewBinding =
            LayoutCustomMediaPlayerBinding.inflate(inflater, binding.root, true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().actionBar?.show()
    }

    override fun onStart() {
        super.onStart()
        player?.let {
            populateCurrentSongInformation(viewModel.getCurrentSongMetadata())
            setupListeners()
        } ?: run {
            initMediaPlayer()
            setupListeners()
            playMusic()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (player?.isCommandAvailable(Player.COMMAND_RELEASE) == true) {
            player?.release()
        }
    }

    private fun initMediaPlayer() {
        player =
            ExoPlayer.Builder(requireContext())
            .build()
            .also { exoPlayer ->
                with (exoPlayer) {
                    playWhenReady = true
                    repeatMode = Player.REPEAT_MODE_ALL
                    setHandleAudioBecomingNoisy(true)
                }
                binding.mediaPlayerView.player = exoPlayer
            }
    }

    private fun setupListeners() {
        player?.addListener(object: Player.Listener {

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                super.onMediaMetadataChanged(mediaMetadata)
                populateCurrentSongInformation(mediaMetadata)
                viewModel.currentSongIndex = player?.currentMediaItemIndex ?: 0
            }
        })

        customMediaPlayerViewBinding.previousSongButton.setOnClickListener {
            if (player?.isCommandAvailable(Player.COMMAND_SEEK_TO_PREVIOUS) == true) {
                player?.seekToPrevious()
            }
        }

        customMediaPlayerViewBinding.nextSongButton.setOnClickListener {
            if (player?.isCommandAvailable(Player.COMMAND_SEEK_TO_NEXT) == true) {
                player?.seekToNext()
            }
        }

        customMediaPlayerViewBinding.playPauseButton.setOnClickListener {
            val imageRes = if (player?.isPlaying == true) {
                player?.pause()
                R.drawable.play_arrow
            } else {
                player?.play()
                R.drawable.pause
            }
            customMediaPlayerViewBinding.playPauseButton.setImageResource(imageRes)
        }
    }

    private fun playMusic() {
        player?.let {
            it.setMediaItems(viewModel.mediaItemList)
            it.prepare()
            if (it.isCommandAvailable(Player.COMMAND_PLAY_PAUSE)) {
                it.play()
            }
        }
    }

    private fun populateCurrentSongInformation(metadata: MediaMetadata) {
        with (customMediaPlayerViewBinding) {
            songTitleTextView.text = metadata.title
            songArtistTextView.text = metadata.artist
        }
    }
}
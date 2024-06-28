package com.scoutmotors.musicplayer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
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
        initObservers()
    }

    override fun onStart() {
        super.onStart()
        initMediaPlayer()
        prepareMediaItems()
    }

    override fun onStop() {
        super.onStop()
        player?.release()
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

        player?.addListener(object: Player.Listener {

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                super.onMediaMetadataChanged(mediaMetadata)
                populateCurrentSongInformation(mediaMetadata.title, mediaMetadata.artist)
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

    private fun prepareMediaItems() {
        val assets = requireContext().assets.list("")
        viewModel.prepareMediaItems(assets)
    }

    private fun initObservers() {
        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MainActivityViewModel.ViewState.StartPlayingMusic -> {
                    customMediaPlayerViewBinding.controllerPanelView.isVisible = true
                    player?.let {
                        it.addMediaItems(state.mediaItemList)
                        it.prepare()
                        it.play()
                    }
                }
                is MainActivityViewModel.ViewState.NoSongToPlay -> {
                    customMediaPlayerViewBinding.controllerPanelView.isVisible = false
                    Toast.makeText(
                        requireContext(),
                        R.string.message_no_song_available,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun populateCurrentSongInformation(title: CharSequence?, artist: CharSequence?) {
        with (customMediaPlayerViewBinding) {
            songTitleTextView.text = title
            songArtistTextView.text = artist
        }
    }
}
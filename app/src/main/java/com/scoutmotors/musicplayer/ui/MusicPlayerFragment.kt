package com.scoutmotors.musicplayer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.media3.common.MediaMetadata
import com.scoutmotors.musicplayer.R
import com.scoutmotors.musicplayer.databinding.FragmentMusicPlayerBinding
import com.scoutmotors.musicplayer.databinding.LayoutCustomMediaPlayerBinding
import com.scoutmotors.musicplayer.helper.MusicPlayerHelper
import com.scoutmotors.musicplayer.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicPlayerFragment: Fragment() {

    private val viewModel: MainActivityViewModel by activityViewModels()

    private lateinit var binding: FragmentMusicPlayerBinding

    private lateinit var customMediaPlayerViewBinding: LayoutCustomMediaPlayerBinding

    @Inject
    lateinit var musicPlayerHelper: MusicPlayerHelper

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
        musicPlayerHelper.setCustomView(binding.mediaPlayerView)

        setupListeners()

        populateCurrentSongInformation(viewModel.getCurrentSongMetadata())

        managePlayPauseButton(musicPlayerHelper.isPlaying())
    }

    private fun setupListeners() {
        setupViewListeners()
        musicPlayerHelper.setupListeners(
            onMediaChangedAction = {
                populateCurrentSongInformation(it)
                viewModel.onSongChanged(musicPlayerHelper.getCurrentSongIndex())
            },
            onPlaybackStateChangedAction = { isPlaying -> managePlayPauseButton(isPlaying) }
        )
    }

    private fun setupViewListeners() {
        customMediaPlayerViewBinding.previousSongButton.setOnClickListener {
            musicPlayerHelper.goToPreviousSong()
        }

        customMediaPlayerViewBinding.nextSongButton.setOnClickListener {
            musicPlayerHelper.goToNextSong()
        }

        customMediaPlayerViewBinding.playPauseButton.setOnClickListener {
            musicPlayerHelper.togglePlayPauseState()
            managePlayPauseButton(musicPlayerHelper.isPlaying())
        }
    }

    private fun managePlayPauseButton(isPlaying: Boolean) {
        val imageRes = if (isPlaying) R.drawable.pause else R.drawable.play_arrow
        customMediaPlayerViewBinding.playPauseButton.setImageResource(imageRes)
    }

    private fun populateCurrentSongInformation(metadata: MediaMetadata) {
        with (customMediaPlayerViewBinding) {
            songTitleTextView.text = metadata.title
            songArtistTextView.text = metadata.artist
        }
    }
}
package com.scoutmotors.musicplayer.helper

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class MusicPlayerHelper
@Inject constructor(
    @ActivityContext private val context: Context
) {

    private var musicPlayer: ExoPlayer? = null

    init {
        musicPlayer = ExoPlayer.Builder(context)
            .build()
            .also { exoPlayer ->
                with (exoPlayer) {
                    playWhenReady = true
                    repeatMode = Player.REPEAT_MODE_ALL
                    setHandleAudioBecomingNoisy(true)
                }
            }
    }

    fun setCustomView(playerView: PlayerView) {
        playerView.player = musicPlayer
    }

    fun setupListeners(
        onMediaChangedAction: (mediaMetadata: MediaMetadata) -> Unit = {},
        onPlaybackStateChangedAction: (isPlaying: Boolean) -> Unit = {}
    ) {
        musicPlayer?.addListener(object: Player.Listener {
            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                super.onMediaMetadataChanged(mediaMetadata)
                onMediaChangedAction.invoke(mediaMetadata)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == Player.STATE_READY) {
                    onPlaybackStateChangedAction.invoke(musicPlayer?.isPlaying ?: false)
                }
            }
        })
    }

    fun startPlayingAllSongs(list: List<MediaItem>) {
        musicPlayer?.let {
            it.setMediaItems(list)
            it.prepare()
            if (it.isCommandAvailable(Player.COMMAND_PLAY_PAUSE)) {
                it.play()
            }
        }
    }

    fun playSongAtIndex(index: Int) {
        musicPlayer?.let {
            if (it.isCommandAvailable(Player.COMMAND_SEEK_TO_MEDIA_ITEM)) {
                it.seekTo(index, 0)
                if (it.isCommandAvailable(Player.COMMAND_PLAY_PAUSE)) {
                    it.play()
                }
            }
        }
    }

    fun goToNextSong() {
        musicPlayer?.let {
            if (it.isCommandAvailable(Player.COMMAND_SEEK_TO_NEXT)) {
                it.seekToNext()
            }
        }
    }

    fun goToPreviousSong() {
        musicPlayer?.let {
            if (it.isCommandAvailable(Player.COMMAND_SEEK_TO_PREVIOUS)) {
                it.seekToPrevious()
            }
        }
    }

    fun togglePlayPauseState() {
        musicPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }

    fun release() {
        musicPlayer?.let {
            if (it.isCommandAvailable(Player.COMMAND_RELEASE)) {
                it.release()
            }
        }
    }

    fun getCurrentSongIndex() = musicPlayer?.currentMediaItemIndex
    fun isPlaying() = musicPlayer?.isPlaying ?: false
}
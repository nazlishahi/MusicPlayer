package com.scoutmotors.musicplayer.viewmodel

import android.content.res.AssetManager
import android.media.MediaMetadataRetriever
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.launch
import java.io.FileNotFoundException

class MainActivityViewModel: ViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState

    private val _musicLibraryViewState = MutableLiveData<MusicLibraryViewState>()
    val musicLibraryViewState: LiveData<MusicLibraryViewState> = _musicLibraryViewState

    private val _action = MutableLiveData<Action>()
    val action: LiveData<Action> = _action

    var mediaItemList = mutableListOf<MediaItem>()

    @VisibleForTesting
    var mediaMetadataRetriever = MediaMetadataRetriever()

    var currentSongIndex = 0

    @OptIn(UnstableApi::class)
    fun prepareMediaItems(assetManager: AssetManager) {
        viewModelScope.launch {
            mediaItemList.clear()
            // Get all audio files in the assets folder
            val allAssets = assetManager.list("")
            allAssets?.let {
                if (it.isEmpty()) {
                    _viewState.value = ViewState.NoSongToPlay
                } else {
                    it.forEachIndexed { index, assetItem ->
                        try {
                            val assetFileDescriptor = assetManager.openFd(assetItem)
                            mediaMetadataRetriever.setDataSource(
                                assetFileDescriptor.fileDescriptor,
                                assetFileDescriptor.startOffset,
                                assetFileDescriptor.length
                            )
                            val mimeType = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
                            if(MimeTypes.isAudio(mimeType)) {
                                val title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                                val artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                                val duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt() ?: 0

                                val extras = Bundle().apply {
                                    putString(EXTRA_DURATION, updateSongDurationForDisplay(duration))
                                }
                                val metadata = MediaMetadata.Builder()
                                    .setTrackNumber(index + 1)
                                    .setTitle(title)
                                    .setArtist(artist)
                                    .setExtras(extras)
                                    .build()

                                val mediaItem =
                                    MediaItem.Builder()
                                        .setUri("file:///android_asset/$assetItem")
                                        .setMediaMetadata(metadata)
                                        .build()
                                mediaItemList.add(mediaItem)
                            }
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        }
                    }

                    if (mediaItemList.isNotEmpty()) {
                        _viewState.value = ViewState.NavigateToMusicPlayer
                    } else {
                        _viewState.value = ViewState.NoSongToPlay
                    }
                }
            } ?: run {
                _viewState.value = ViewState.NoSongToPlay
            }
        }
    }

    private fun updateSongDurationForDisplay(durationInMilliseconds: Int): String {
        val durationMinutes = (durationInMilliseconds / 1000) / 60
        val durationSeconds = (durationInMilliseconds / 1000) % 60
        val displaySeconds = if (durationSeconds < 10) "0$durationSeconds" else durationSeconds
        return "$durationMinutes:$displaySeconds"
    }

    fun getCurrentSongMetadata(): MediaMetadata {
        return mediaItemList[currentSongIndex].mediaMetadata
    }

    fun onSongChanged(songIndex: Int?) {
        currentSongIndex = songIndex ?: 0
        _musicLibraryViewState.value = MusicLibraryViewState.UpdateMusicLibraryIndex
    }

    fun onMusicLibraryItemClicked(index: Int) {
        if (index != currentSongIndex) {
            _action.value = Action.PlaySongAtIndex(index)
        }
    }

    sealed class ViewState {
        data object NavigateToMusicPlayer: ViewState()
        data object NoSongToPlay: ViewState()
    }

    sealed class MusicLibraryViewState {
        data object UpdateMusicLibraryIndex: MusicLibraryViewState()
    }

    sealed class Action {
        data class PlaySongAtIndex(val index: Int): Action()
    }

    companion object {
        const val EXTRA_DURATION = "duration_extra_key"
    }
}
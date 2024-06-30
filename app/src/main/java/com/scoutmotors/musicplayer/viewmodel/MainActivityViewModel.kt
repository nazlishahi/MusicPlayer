package com.scoutmotors.musicplayer.viewmodel

import android.content.res.AssetManager
import android.media.MediaMetadataRetriever
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import com.scoutmotors.musicplayer.wrapper.MimeTypeWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(): ViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState

    var mediaItemList: List<MediaItem> = listOf()
    var currentSongIndex = 0

    @VisibleForTesting
    var mimeTypeWrapper = MimeTypeWrapper()

    @OptIn(UnstableApi::class)
    fun prepareMediaItems(assetManager: AssetManager) {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        assetManager.list("")?.let {
            mediaItemList = it.filter { item ->
                val mimeType = mimeTypeWrapper.getFileExtensionFromUrl(item)
                MimeTypes.isAudio(mimeType) || !mimeType.isNullOrEmpty()
            }.map { fileName ->
                val fd = assetManager.openFd(fileName)
                mediaMetadataRetriever.setDataSource(
                    fd.fileDescriptor,
                    fd.startOffset,
                    fd.length
                )

                val title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                val artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                val album = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                val duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt() ?: 0
                val extras = Bundle().apply {
                    putString(EXTRA_DURATION, updateSongDurationForDisplay(duration))
                }
                val metadata = MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setAlbumTitle(album)
                    .setExtras(extras)
                    .build()

                val mediaItem =
                    MediaItem.Builder()
                        .setUri("file:///android_asset/$fileName")
                        .setMediaMetadata(metadata)
                        .build()
                mediaItem
            }

            if (mediaItemList.isNotEmpty()) {
                _viewState.value = ViewState.NavigateToMusicPlayer
            } else {
                _viewState.value = ViewState.NoSongToPlay
            }
        } ?: run {
            _viewState.value = ViewState.NoSongToPlay
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

    sealed class ViewState {
        data object NavigateToMusicPlayer: ViewState()
        data object NoSongToPlay: ViewState()
    }

    companion object {
        const val EXTRA_DURATION = "duration_extra_key"
    }
}
package com.scoutmotors.musicplayer.viewmodel

import android.webkit.MimeTypeMap
import androidx.annotation.OptIn
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(): ViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState

    @OptIn(UnstableApi::class)
    fun prepareMediaItems(assets: Array<String>?) {
        assets?.let {
            val mediaItemList = it.filterNot { item ->
                val mimeType = MimeTypeMap.getFileExtensionFromUrl(item)
                MimeTypes.isAudio(mimeType) || mimeType.isNullOrEmpty()
            }.map { fileName ->
                MediaItem.fromUri("file:///android_asset/$fileName")
            }

            if (mediaItemList.isNotEmpty()) {
                _viewState.value = ViewState.StartPlayingMusic(mediaItemList)
            } else {
                _viewState.value = ViewState.NoSongToPlay
            }
        } ?: run {
            _viewState.value = ViewState.NoSongToPlay
        }
    }

    sealed class ViewState {
        data class StartPlayingMusic(val mediaItemList: List<MediaItem>): ViewState()
        data object NoSongToPlay: ViewState()
    }
}
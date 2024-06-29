package com.scoutmotors.musicplayer.data

import androidx.media3.common.MediaItem

open class ListItem {
    class HeaderItem : ListItem()
    class SongItem(val mediaItem: MediaItem) : ListItem()
}
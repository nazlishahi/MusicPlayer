package com.scoutmotors.musicplayer.wrapper

import android.webkit.MimeTypeMap

class MimeTypeWrapper {
    fun getFileExtensionFromUrl(url: String): String? {
        return MimeTypeMap.getFileExtensionFromUrl(url)
    }
}
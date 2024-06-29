package com.scoutmotors.musicplayer.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaMetadata
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.scoutmotors.musicplayer.R
import com.scoutmotors.musicplayer.data.ListItem
import com.scoutmotors.musicplayer.databinding.ItemSongInformationBinding
import com.scoutmotors.musicplayer.viewmodel.MainActivityViewModel

class SongListAdapter: RecyclerView.Adapter<ViewHolder>() {

    private val list = mutableListOf<ListItem>()

    private var currentSelectedIndex: Int = 0

    fun populate(itemList: List<ListItem>, selectedIndex: Int) {
        list.clear()
        list.addAll(itemList)
        currentSelectedIndex = selectedIndex
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HEADER_VIEW_TYPE -> HeaderViewHolder(inflater.inflate(R.layout.item_song_list_header, parent, false))
            else -> SongViewHolder(inflater.inflate(R.layout.item_song_information, parent, false))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            SONG_VIEW_TYPE -> {
                val metadata =
                    (list[position] as ListItem.SongItem)
                        .mediaItem
                        .mediaMetadata
                val isSelected = (position == currentSelectedIndex + 1)
                (holder as SongViewHolder).bind(metadata, isSelected)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> HEADER_VIEW_TYPE
            else -> SONG_VIEW_TYPE
        }
    }

    private class SongViewHolder(itemView: View): ViewHolder(itemView) {

        private var binding = ItemSongInformationBinding.bind(itemView)

        fun bind(metadata: MediaMetadata, isSelected: Boolean) {
            binding.songNameTextView.text = metadata.title
            binding.songAlbumTextView.text = metadata.albumTitle
            binding.songDurationTextView.text = metadata.extras?.getString(MainActivityViewModel.EXTRA_DURATION)
            itemView.isSelected = isSelected
        }
    }

    private class HeaderViewHolder(itemView: View): ViewHolder(itemView)

    companion object {
        private const val HEADER_VIEW_TYPE = 0
        private const val SONG_VIEW_TYPE = 1
    }
}
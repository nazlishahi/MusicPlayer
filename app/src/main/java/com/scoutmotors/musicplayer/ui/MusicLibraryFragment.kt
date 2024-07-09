package com.scoutmotors.musicplayer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.scoutmotors.musicplayer.data.ListItem
import com.scoutmotors.musicplayer.databinding.FragmentMusicLibraryBinding
import com.scoutmotors.musicplayer.viewmodel.MainActivityViewModel

class MusicLibraryFragment: Fragment() {

    private val viewModel: MainActivityViewModel by activityViewModels()

    private lateinit var binding: FragmentMusicLibraryBinding

    private val adapter by lazy {
        SongListAdapter(object: OnSongClickedListener {
            override fun onSongClicked(index: Int) {
                viewModel.onMusicLibraryItemClicked(index)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMusicLibraryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        requireActivity().actionBar?.hide()
        initObserver()
    }

    private fun initRecyclerView() {
        binding.songListRecyclerView.adapter = adapter

        val displayList = mutableListOf<ListItem>(ListItem.HeaderItem()).apply {
            addAll(viewModel.mediaItemList.map { ListItem.SongItem(it) })
        }
        adapter.populate(displayList, viewModel.currentSongIndex)
    }

    private fun initObserver() {
        viewModel.musicLibraryViewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MainActivityViewModel.MusicLibraryViewState.UpdateMusicLibraryIndex -> {
                    adapter.modifySelectedSong(viewModel.currentSongIndex)
                }
            }
        }
    }

    interface OnSongClickedListener {
        fun onSongClicked(index: Int)
    }
}
package com.scoutmotors.musicplayer.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.scoutmotors.musicplayer.R
import com.scoutmotors.musicplayer.databinding.ActivityMainBinding
import com.scoutmotors.musicplayer.viewmodel.MainActivityViewModel

class MainActivity: FragmentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!navController.navigateUp()) {
                    finish()
                }
            }
        })

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initToolbar()

        initMediaPlayer()

        initObservers()

        initNavigationGraph()

        viewModel.prepareMediaItems(assets)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.viewMusicLibraryItem -> {
                navController.navigate(R.id.musicLibraryFragment)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initToolbar() {
        setActionBar(binding.toolbar)
        actionBar?.title = ""
    }

    private fun initNavigationGraph() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun initMediaPlayer() {
        player =
            ExoPlayer.Builder(this)
                .build()
                .also { exoPlayer ->
                    with (exoPlayer) {
                        playWhenReady = true
                        repeatMode = Player.REPEAT_MODE_ALL
                        setHandleAudioBecomingNoisy(true)
                    }
                }
    }

    private fun startPlayingAllSongs() {
        player?.let {
            it.setMediaItems(viewModel.mediaItemList)
            it.prepare()
            if (it.isCommandAvailable(Player.COMMAND_PLAY_PAUSE)) {
                it.play()
            }
        }
    }

    private fun initObservers() {
        viewModel.viewState.observe(this) { state ->
            when (state) {
                is MainActivityViewModel.ViewState.NavigateToMusicPlayer -> {
                    startPlayingAllSongs()
                    navController.currentDestination?.let {
                        navController.navigate(R.id.musicPlayerFragment)
                    } ?: run {
                        navController.setGraph(R.navigation.nav_graph)
                    }
                }
                is MainActivityViewModel.ViewState.NoSongToPlay -> {
                    Toast.makeText(
                        this,
                        R.string.message_no_song_available,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        viewModel.action.observe(this) { state ->
            when (state) {
                is MainActivityViewModel.Action.PlaySongAtIndex -> {
                    player?.let {
                        if (it.isCommandAvailable(Player.COMMAND_SEEK_TO_MEDIA_ITEM)) {
                            it.seekTo(state.index, 0)
                            if (it.isCommandAvailable(Player.COMMAND_PLAY_PAUSE)) {
                                it.play()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (player?.isCommandAvailable(Player.COMMAND_RELEASE) == true) {
            player?.release()
        }
    }
}
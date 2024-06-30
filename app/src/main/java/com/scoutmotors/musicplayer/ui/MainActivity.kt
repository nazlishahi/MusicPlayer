package com.scoutmotors.musicplayer.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.scoutmotors.musicplayer.R
import com.scoutmotors.musicplayer.databinding.ActivityMainBinding
import com.scoutmotors.musicplayer.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: FragmentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

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

        initObservers()

        initNavigationGraph()

        prepareMediaItems()
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

    private fun initObservers() {
        viewModel.viewState.observe(this) { state ->
            when (state) {
                is MainActivityViewModel.ViewState.NavigateToMusicPlayer -> {
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
    }

    private fun prepareMediaItems() {
        viewModel.prepareMediaItems(assets)
    }
}
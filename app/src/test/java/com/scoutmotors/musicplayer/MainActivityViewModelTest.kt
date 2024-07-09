package com.scoutmotors.musicplayer

import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.media.MediaMetadataRetriever
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.scoutmotors.musicplayer.viewmodel.MainActivityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.KArgumentCaptor
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import java.io.FileDescriptor

@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class MainActivityViewModelTest {

    private lateinit var viewModel: MainActivityViewModel

    @Mock
    lateinit var viewStateObserver: Observer<MainActivityViewModel.ViewState>

    private lateinit var viewStateCaptor: KArgumentCaptor<MainActivityViewModel.ViewState>

    @Mock
    lateinit var musicLibraryViewStateObserver: Observer<MainActivityViewModel.MusicLibraryViewState>

    private lateinit var musicLibraryViewStateCaptor: KArgumentCaptor<MainActivityViewModel.MusicLibraryViewState>

    @Mock
    lateinit var actionObserver: Observer<MainActivityViewModel.Action>

    private lateinit var actionCaptor: KArgumentCaptor<MainActivityViewModel.Action>

    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(dispatcher)

        viewStateCaptor = argumentCaptor<MainActivityViewModel.ViewState>()
        musicLibraryViewStateCaptor = argumentCaptor<MainActivityViewModel.MusicLibraryViewState>()
        actionCaptor = argumentCaptor<MainActivityViewModel.Action>()

        viewModel = MainActivityViewModel()

        viewModel.viewState.observeForever(viewStateObserver)
        viewModel.musicLibraryViewState.observeForever(musicLibraryViewStateObserver)
        viewModel.action.observeForever(actionObserver)
    }

    @After
    fun tearDown() {
        viewModel.viewState.removeObserver(viewStateObserver)
        viewModel.musicLibraryViewState.removeObserver(musicLibraryViewStateObserver)
        viewModel.action.removeObserver(actionObserver)
        Dispatchers.resetMain()
    }

    @Test
    fun `when asset folder contains one or more songs, then verify navigate to music player`() = runTest {
        val mockAssetManager = mock<AssetManager>()
        val assetFileDescriptor = mock<AssetFileDescriptor>()
        val mockMetadataRetriever = mock<MediaMetadataRetriever>()
        val fileDescriptor = mock<FileDescriptor>()
        val mockUrl = "Mock song name.mp3"
        viewModel.mediaMetadataRetriever = mockMetadataRetriever
        Mockito.`when`(mockAssetManager.list("")).thenReturn(arrayOf(mockUrl))
        Mockito.`when`(mockAssetManager.openFd(mockUrl)).thenReturn(assetFileDescriptor)
        Mockito.`when`(mockMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)).thenReturn("audio/mpeg")
        Mockito.`when`(assetFileDescriptor.fileDescriptor).thenReturn(fileDescriptor)

        viewModel.prepareMediaItems(mockAssetManager)

        Mockito.verify(viewStateObserver, times(1))
            .onChanged(viewStateCaptor.capture())

        Assert.assertEquals(viewStateCaptor.allValues.size, 1)
        Assert.assertEquals(viewStateCaptor.allValues[0], MainActivityViewModel.ViewState.NavigateToMusicPlayer)
    }

    @Test
    fun `when asset folder contains no songs, then verify error is displayed`() = runTest {
        val mockAssetManager = mock<AssetManager>()
        Mockito.`when`(mockAssetManager.list("")).thenReturn(arrayOf())

        viewModel.prepareMediaItems(mockAssetManager)

        Mockito.verify(viewStateObserver, times(1))
            .onChanged(viewStateCaptor.capture())

        Assert.assertEquals(viewStateCaptor.allValues.size, 1)
        Assert.assertEquals(viewStateCaptor.allValues[0], MainActivityViewModel.ViewState.NoSongToPlay)
    }

    @Test
    fun `when song index changes, verify that music library index is updated`() {
        viewModel.onSongChanged(1)

        Mockito.verify(musicLibraryViewStateObserver, times(1))
            .onChanged(musicLibraryViewStateCaptor.capture())

        Assert.assertEquals(musicLibraryViewStateCaptor.allValues.size, 1)
        Assert.assertEquals(musicLibraryViewStateCaptor.allValues[0], MainActivityViewModel.MusicLibraryViewState.UpdateMusicLibraryIndex)
    }

    @Test
    fun `when a new song is clicked in the music library, verify that proper action is taken`() {
        viewModel.currentSongIndex = 0
        viewModel.onMusicLibraryItemClicked(1)

        Mockito.verify(actionObserver, times(1))
            .onChanged(actionCaptor.capture())

        Assert.assertEquals(actionCaptor.allValues.size, 1)
        Assert.assertEquals(actionCaptor.allValues[0], MainActivityViewModel.Action.PlaySongAtIndex(1))
    }

    @Test
    fun `when playing song is clicked in the music library, verify that nothing happens`() {
        viewModel.currentSongIndex = 1
        viewModel.onMusicLibraryItemClicked(1)

        Mockito.verify(actionObserver, never())
            .onChanged(actionCaptor.capture())

        Assert.assertTrue(actionCaptor.allValues.isEmpty())
    }
}
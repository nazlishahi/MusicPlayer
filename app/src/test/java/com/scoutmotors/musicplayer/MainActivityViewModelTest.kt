package com.scoutmotors.musicplayer

import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.scoutmotors.musicplayer.viewmodel.MainActivityViewModel
import com.scoutmotors.musicplayer.wrapper.MimeTypeWrapper
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
import org.mockito.kotlin.times

@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class MainActivityViewModelTest {

    private lateinit var viewModel: MainActivityViewModel

    @Mock
    private lateinit var mimeTypeWrapper: MimeTypeWrapper

    @Mock
    lateinit var viewStateObserver: Observer<MainActivityViewModel.ViewState>

    private lateinit var viewStateCaptor: KArgumentCaptor<MainActivityViewModel.ViewState>

    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(dispatcher)

        viewStateCaptor = argumentCaptor<MainActivityViewModel.ViewState>()

        viewModel = MainActivityViewModel()
        viewModel.mimeTypeWrapper = mimeTypeWrapper

        viewModel.viewState.observeForever(viewStateObserver)
    }

    @After
    fun tearDown() {
        viewModel.viewState.removeObserver(viewStateObserver)
        Dispatchers.resetMain()
    }

    @Test
    fun `when asset folder contains one or more songs, then verify navigate to music player`() = runTest {
        val mockAssetManager = mock<AssetManager>()
        val assetFileDescriptor = mock<AssetFileDescriptor>()
        val mockUrl = "Mock song name.mp3"
        Mockito.`when`(mockAssetManager.list("")).thenReturn(arrayOf(mockUrl))
        Mockito.`when`(mockAssetManager.openFd(mockUrl)).thenReturn(assetFileDescriptor)
        Mockito.`when`(mimeTypeWrapper.getFileExtensionFromUrl(mockUrl)).thenReturn("mp3")

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
}
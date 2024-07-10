package com.scoutmotors.musicplayer.di

import android.content.Context
import com.scoutmotors.musicplayer.helper.MusicPlayerHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
class MusicModule {

    @Provides
    @ActivityScoped
    fun provideMusicPlayerHelper(@ActivityContext context: Context): MusicPlayerHelper {
        return MusicPlayerHelper(context)
    }
}
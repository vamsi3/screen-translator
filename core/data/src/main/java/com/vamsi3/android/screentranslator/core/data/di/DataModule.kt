package com.vamsi3.android.screentranslator.core.data.di

import com.vamsi3.android.screentranslator.core.data.repository.OfflineFirstUserDataRepository
import com.vamsi3.android.screentranslator.core.data.repository.UserDataRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindsUserDataRepository(
        userDataRepository: OfflineFirstUserDataRepository
    ): UserDataRepository
}

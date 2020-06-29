package com.odhiambopaul.wonder.di.module

import com.odhiambopaul.wonder.data.WonderDb
import com.odhiambopaul.wonder.repository.DatabaseRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class WonderModule {
    @Singleton
    @Provides
    fun providesDataBaseRepository(wonderDb: WonderDb): DatabaseRepository {
        return DatabaseRepository(wonderDb)
    }
}
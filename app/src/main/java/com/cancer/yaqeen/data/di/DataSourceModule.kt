package com.cancer.yaqeen.data.di

import com.cancer.yaqeen.data.features.home.schedule.data_sources.ScheduleLocalDataSourceImpl
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.error.ErrorHandlerImpl
import com.cancer.yaqeen.data.room.YaqeenDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Singleton
    @Provides
    fun provideScheduleLocalDataSource(
        yaqeenDao: YaqeenDao,
        errorHandlerImpl: ErrorHandlerImpl,
        sharedPrefEncryptionUtil: SharedPrefEncryptionUtil
    ) = ScheduleLocalDataSourceImpl(yaqeenDao, errorHandlerImpl, sharedPrefEncryptionUtil)

}
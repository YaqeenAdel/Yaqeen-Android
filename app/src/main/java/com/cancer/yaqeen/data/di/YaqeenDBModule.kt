package com.cancer.yaqeen.data.di

import android.content.Context
import androidx.room.Room
import com.cancer.yaqeen.data.room.YaqeenDao
import com.cancer.yaqeen.data.room.YaqeenDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object YaqeenDBModule {

    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context): YaqeenDatabase {
        return Room.databaseBuilder(
            context,
            YaqeenDatabase::class.java,
            "YaqeenDatabase"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideYaqeenDao(yaqeenDatabase: YaqeenDatabase): YaqeenDao {
        return yaqeenDatabase.yaqeenDao()
    }
}
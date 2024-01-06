package com.cancer.yaqeen.domain.di

import com.cancer.yaqeen.data.features.auth.AuthRepositoryImpl
import com.cancer.yaqeen.data.features.onboarding.HomeRepositoryImpl
import com.cancer.yaqeen.data.features.profile.ProfileRepositoryImpl
import com.cancer.yaqeen.domain.features.auth.login.usecases.LoginUseCase
import com.cancer.yaqeen.domain.features.auth.login.usecases.LogoutUseCase
import com.cancer.yaqeen.domain.features.onboarding.usecases.GetResourcesUseCase
import com.cancer.yaqeen.domain.features.onboarding.usecases.GetUniversitiesUseCase
import com.cancer.yaqeen.domain.features.onboarding.usecases.GetUserProfileUseCase
import com.cancer.yaqeen.domain.features.onboarding.usecases.UpdateInterestsUserUseCase
import com.cancer.yaqeen.domain.features.onboarding.usecases.UpdateUserProfileUseCase
import com.cancer.yaqeen.domain.features.profile.GetProfileUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Singleton
    @Provides
    fun provideLoginUseCase(repository: AuthRepositoryImpl) =
        LoginUseCase(repository)

    @Singleton
    @Provides
    fun provideLogoutUseCase(repository: AuthRepositoryImpl) =
        LogoutUseCase(repository)

    @Singleton
    @Provides
    fun provideGetResourcesUseCase(repository: HomeRepositoryImpl) =
        GetResourcesUseCase(repository)

    @Singleton
    @Provides
    fun provideGetUniversitiesUseCase(repository: HomeRepositoryImpl) =
        GetUniversitiesUseCase(repository)

    @Singleton
    @Provides
    fun provideUpdateUserProfileUseCase(repository: HomeRepositoryImpl) =
        UpdateUserProfileUseCase(repository)

    @Singleton
    @Provides
    fun provideUpdateInterestsUserUseCase(repository: HomeRepositoryImpl) =
        UpdateInterestsUserUseCase(repository)

    @Singleton
    @Provides
    fun provideGetUserProfileUseCase(repository: HomeRepositoryImpl) =
        GetUserProfileUseCase(repository)
    @Singleton
    @Provides
    fun provideGetProfileUseCase(repository: ProfileRepositoryImpl) =
        GetProfileUseCase(repository)

}
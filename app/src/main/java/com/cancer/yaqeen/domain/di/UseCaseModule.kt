package com.cancer.yaqeen.domain.di

import com.cancer.yaqeen.data.features.auth.AuthRepositoryImpl
import com.cancer.yaqeen.data.features.home.articles.HomeRepositoryImpl
import com.cancer.yaqeen.data.features.onboarding.OnboardingRepositoryImpl
import com.cancer.yaqeen.data.features.profile.ProfileRepositoryImpl
import com.cancer.yaqeen.data.features.home.schedule.ScheduleRepositoryImpl
import com.cancer.yaqeen.domain.features.auth.login.usecases.LoginUseCase
import com.cancer.yaqeen.domain.features.auth.login.usecases.LogoutUseCase
import com.cancer.yaqeen.domain.features.auth.login.usecases.RefreshTokenUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.BookmarkArticleUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.GetArticlesUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.GetBookmarkedArticlesUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.GetLocalBookmarkedArticlesUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.RemoveBookmarkedArticleUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.RemoveBookmarkedArticlesUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.SaveArticleUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.UnBookmarkArticleUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.AddMedicationUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.EditMedicationUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.GetTodayRemindersUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.GetMedicationRemindersUseCase
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
    fun provideRefreshTokenUseCase(repository: AuthRepositoryImpl) =
        RefreshTokenUseCase(repository)

    @Singleton
    @Provides
    fun provideGetResourcesUseCase(repository: OnboardingRepositoryImpl) =
        GetResourcesUseCase(repository)

    @Singleton
    @Provides
    fun provideGetUniversitiesUseCase(repository: OnboardingRepositoryImpl) =
        GetUniversitiesUseCase(repository)

    @Singleton
    @Provides
    fun provideUpdateUserProfileUseCase(repository: OnboardingRepositoryImpl) =
        UpdateUserProfileUseCase(repository)

    @Singleton
    @Provides
    fun provideUpdateInterestsUserUseCase(repository: OnboardingRepositoryImpl) =
        UpdateInterestsUserUseCase(repository)

    @Singleton
    @Provides
    fun provideGetUserProfileUseCase(repository: OnboardingRepositoryImpl) =
        GetUserProfileUseCase(repository)
    @Singleton
    @Provides
    fun provideGetProfileUseCase(repository: ProfileRepositoryImpl) =
        GetProfileUseCase(repository)
    @Singleton
    @Provides
    fun provideGetArticleUseCase(repository: HomeRepositoryImpl) =
        GetArticlesUseCase(repository)
    @Singleton
    @Provides
    fun provideBookmarkArticleUseCase(repository: HomeRepositoryImpl) =
        BookmarkArticleUseCase(repository)
    @Singleton
    @Provides
    fun provideGetBookmarkedArticlesUseCase(repository: HomeRepositoryImpl) =
        GetBookmarkedArticlesUseCase(repository)
    @Singleton
    @Provides
    fun provideUnBookmarkArticleUseCase(repository: HomeRepositoryImpl) =
        UnBookmarkArticleUseCase(repository)
    @Singleton
    @Provides
    fun provideGetLocalBookmarkedArticlesUseCase(repository: HomeRepositoryImpl) =
        GetLocalBookmarkedArticlesUseCase(repository)
    @Singleton
    @Provides
    fun provideRemoveBookmarkedArticlesUseCase(repository: HomeRepositoryImpl) =
        RemoveBookmarkedArticlesUseCase(repository)
    @Singleton
    @Provides
    fun provideRemoveBookmarkedArticleUseCase(repository: HomeRepositoryImpl) =
        RemoveBookmarkedArticleUseCase(repository)
    @Singleton
    @Provides
    fun provideSaveArticleUseCase(repository: HomeRepositoryImpl) =
        SaveArticleUseCase(repository)
    @Singleton
    @Provides
    fun provideAddMedicationUseCase(repository: ScheduleRepositoryImpl) =
        AddMedicationUseCase(repository)
    @Singleton
    @Provides
    fun provideGetMedicationRemindersUseCase(repository: ScheduleRepositoryImpl) =
        GetMedicationRemindersUseCase(repository)
    @Singleton
    @Provides
    fun provideEditMedicationUseCase(repository: ScheduleRepositoryImpl) =
        EditMedicationUseCase(repository)
    @Singleton
    @Provides
    fun provideGetMedicationRemindersFromNowUseCase(repository: ScheduleRepositoryImpl) =
        GetTodayRemindersUseCase(repository)


}
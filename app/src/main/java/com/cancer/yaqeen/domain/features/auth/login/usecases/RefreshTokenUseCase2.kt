package com.cancer.yaqeen.domain.features.auth.login.usecases

import com.cancer.yaqeen.data.network.base.DataState
import android.content.Context
import com.cancer.yaqeen.data.features.auth.IAuthRepository
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.features.auth.responses.RefreshTokenResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RefreshTokenUseCase2 @Inject constructor(private val repository: IAuthRepository) {
    suspend operator fun invoke(context: Context) =
        repository.refresh(context)
}
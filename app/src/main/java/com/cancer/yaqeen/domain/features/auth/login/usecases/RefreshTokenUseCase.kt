package com.cancer.yaqeen.domain.features.auth.login.usecases

import android.content.Context
import com.cancer.yaqeen.data.features.auth.IAuthRepository
import javax.inject.Inject

class RefreshTokenUseCase @Inject constructor(private val repository: IAuthRepository) {
    suspend operator fun invoke(context: Context) =
        repository.refreshToken(context)
}
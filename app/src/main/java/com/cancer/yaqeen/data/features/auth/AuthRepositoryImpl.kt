package com.cancer.yaqeen.data.features.auth

import com.cancer.yaqeen.data.base.BaseDataSource
import com.cancer.yaqeen.data.base.Resource
import com.cancer.yaqeen.data.base.flowResponseAPI
import com.cancer.yaqeen.data.features.auth.mappers.MappingLoginRemoteAsUser
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.features.auth.requests.LoginRequestBody
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil.Companion.PREF_USER
import com.cancer.yaqeen.data.network.YaqeenAPI
import com.cancer.yaqeen.data.network.error.ErrorHandlerImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: YaqeenAPI,
    errorHandler: ErrorHandlerImpl,
    private val sharedPrefEncryptionUtil: SharedPrefEncryptionUtil
): BaseDataSource(errorHandler), IAuthRepository {

    override suspend fun login(requestBody: LoginRequestBody): Flow<Resource<User>> =
        flowResponseAPI {
            getResultRemote(MappingLoginRemoteAsUser()) {
                val loginResponse = apiService.login(requestBody)

                // Save the token, refresh token, and the user data at the local storage.
                loginResponse.body()?.apply {
                    sharedPrefEncryptionUtil.setToken(token)
                    sharedPrefEncryptionUtil.setModelData(user, PREF_USER)
                    sharedPrefEncryptionUtil.isLogged = true
                }

                loginResponse
            }

    }

}
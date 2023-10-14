package com.cancer.yaqeen.data.features.auth

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials

import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.base.BaseDataSource
import com.cancer.yaqeen.data.base.Resource
import com.cancer.yaqeen.data.base.flowResponseAPI
import com.cancer.yaqeen.data.features.auth.mappers.MappingLoginRemoteAsUser
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.features.auth.requests.LoginRequestBody
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil.Companion.PREF_USER
import com.cancer.yaqeen.data.network.apis.YaqeenAPI
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.data.network.error.ErrorHandlerImpl
import com.cancer.yaqeen.data.utils.Constants
import com.cancer.yaqeen.presentation.ui.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth0: Auth0,
    errorHandler: ErrorHandlerImpl,
    private val sharedPrefEncryptionUtil: SharedPrefEncryptionUtil
): BaseDataSource(errorHandler), IAuthRepository {

    override suspend fun login(context: Context): Flow<Resource<User>> =
        withContext(Dispatchers.Main){
            try {
                val credentials = WebAuthProvider.login(auth0)
                    .withScheme("demo")
                    .withScope("openid profile email read:current_user update:current_user_metadata")
                    .withAudience(Constants.AUTH_0_AUDINCE_URL)
                    .start(context,object : Callback<Credentials, AuthenticationException>
                    {
                        override fun onFailure(error: AuthenticationException) {
                            TODO("Not yet implemented")
                        }

                        override fun onSuccess(result: Credentials) {
                            TODO("Not yet implemented")
                        }


                    })


              //  sharedPrefEncryptionUtil.setToken(credentials.accessToken)
                flow {
                    emit(
                        Resource.Success(
                      //      MappingLoginRemoteAsUser().map(credentials.)
                        )
                    )
                }
            }catch (e: AuthenticationException){
                flow {
                    emit(
                        Resource.Error(
                            ErrorEntity.ApiError.Network("")
                        )
                    )
                }
            }
        }
}
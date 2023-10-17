package com.cancer.yaqeen.data.network

import android.content.Context
import android.net.ConnectivityManager
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class NetworkConnectionInterceptor @Inject constructor(
    val context: Context,
    private val sharedPrefUtil: SharedPrefEncryptionUtil
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isConnected()) {
            throw NoConnectivityException()
        }
        val originalRequest = chain.request()
        val isAuthorization = originalRequest.headers["isAuthorization"] != "false"
        val builder: Request.Builder = originalRequest
            .newBuilder()
            .addHeader("lang", sharedPrefUtil.selectedLanguage)
            .addHeader("Content-Type","application/json")
        if (isAuthorization) {
            builder
                .addHeader("Authorization", "${sharedPrefUtil.getTokenType()} ${sharedPrefUtil.getToken()}")
        }
        return chain.proceed(builder.build())
    }

    private fun isConnected(): Boolean {
        val connectivityManager   =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo
        return netInfo?.isConnected ?: false
    }
}

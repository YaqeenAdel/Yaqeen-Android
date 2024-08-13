package com.cancer.yaqeen.data.local

import android.content.SharedPreferences
import com.cancer.yaqeen.data.features.onboarding.models.Language
import com.google.gson.Gson
import java.util.*
import javax.inject.Inject

class SharedPrefEncryptionUtil @Inject constructor(
    val sharedPreferences: SharedPreferences,
    val gson: Gson
) {
    companion object {
        const val PREF_TOKEN = "prefs-token"
        const val PREF_REFRESH_TOKEN = "prefs-refresh-token"
        const val PREF_TOKEN_EXPIRED_DATE = "prefs-token-expired-date"
        const val PREF_TOKEN_TYPE = "prefs-token-type"
        const val PREF_USER = "prefs-user"
        const val PREF_SELECTED_LANGUAGE = "prefs-selected-language"
        const val PREF_HAS_SEEN_BOARDS = "prefs-has-seen-boards"
        const val PREF_IS_LOGGED_IN = "prefs-is-logged-in"
        const val PREF_HAS_WORKER = "prefs-has-worker"
        const val PREF_WORK_ID = "prefs-work-id"
        const val PREF_WORK_RUNNING_TIME_IN_MILLIS = "prefs-work-running-time-in-millis"
    }


    fun <T> setModelData(model: T, key: String) {
        sharedPreferences
            .edit()
            .putString(key, gson.toJson(model).toString())
            .apply()
    }

    inline fun <reified T> getModelData(key: String, classType: Class<T>): T? {
        val jsonUser = sharedPreferences.getString(key, "")
        if (jsonUser.isNullOrEmpty())
            return null
        return gson.fromJson(jsonUser, classType)
    }

    inline fun <reified T> modelDataIsNotNull(key: String, classType: Class<T>): Boolean =
        getModelData(key, classType) != null

    fun setToken(token: String) =
        sharedPreferences
            .edit()
            .putString(PREF_TOKEN, token)
            .apply()
    fun getToken(): String =
        sharedPreferences.getString(PREF_TOKEN, "").toString()


    fun setRefreshToken(refreshToken: String?) =
        sharedPreferences
            .edit()
            .putString(PREF_REFRESH_TOKEN, refreshToken)
            .apply()
    fun getRefreshToken(): String =
        sharedPreferences.getString(PREF_REFRESH_TOKEN, "").toString()


    fun setTokenExpiredDate(timeInMillis: Long) =
        sharedPreferences
            .edit()
            .putLong(PREF_TOKEN_EXPIRED_DATE, timeInMillis)
            .apply()
    fun getTokenExpiredDate(): Long =
        sharedPreferences.getLong(PREF_TOKEN_EXPIRED_DATE, 0L)


    fun setTokenType(tokenType: String) =
        sharedPreferences
            .edit()
            .putString(PREF_TOKEN_TYPE, tokenType)
            .apply()
    fun getTokenType(): String =
        sharedPreferences.getString(PREF_TOKEN_TYPE, "").toString()

    var hasSeenBoards: Boolean
        get() = sharedPreferences.getBoolean(PREF_HAS_SEEN_BOARDS, false)
        set(hasSeenBoards) {
            sharedPreferences
                .edit()
                .putBoolean(PREF_HAS_SEEN_BOARDS, hasSeenBoards)
                .apply()
        }
    var isLogged: Boolean
        get() = sharedPreferences.getBoolean(PREF_IS_LOGGED_IN, false)
        set(isLogged) {
            sharedPreferences
                .edit()
                .putBoolean(PREF_IS_LOGGED_IN, isLogged)
                .apply()
        }

    var selectedLanguage: String
        get() = sharedPreferences.getString(PREF_SELECTED_LANGUAGE, Locale.getDefault().language).toString()
        set(lang) {
            val edit = sharedPreferences.edit()
            edit.putString(PREF_SELECTED_LANGUAGE, lang)
            edit.apply()
        }

    fun selectedLanguageIsEnglish(): Boolean = selectedLanguage == Language.ENGLISH.lang

    fun selectedLanguageIsArabic(): Boolean = selectedLanguage == Language.ARABIC.lang

    var hasWorker: Boolean
        get() = sharedPreferences.getBoolean(PREF_HAS_WORKER, false)
        set(hasWorker) {
            sharedPreferences
                .edit()
                .putBoolean(PREF_HAS_WORKER, hasWorker)
                .apply()
        }
    var workId: String
        get() = sharedPreferences.getString(PREF_WORK_ID, "").toString()
        set(workId) {
            sharedPreferences
                .edit()
                .putString(PREF_WORK_ID, workId)
                .apply()
        }
    var workRunningInMillis: Long
        get() = sharedPreferences.getLong(PREF_WORK_RUNNING_TIME_IN_MILLIS, 0L)
        set(workRunningInMillis) {
            sharedPreferences
                .edit()
                .putLong(PREF_WORK_RUNNING_TIME_IN_MILLIS, workRunningInMillis)
                .apply()
        }

    fun clearUserPreferenceStorage() {
        sharedPreferences
            .edit()
            .remove(PREF_TOKEN)
            .remove(PREF_USER)
            .remove(PREF_IS_LOGGED_IN)
            .remove(PREF_HAS_WORKER)
            .remove(PREF_WORK_ID)
            .remove(PREF_WORK_RUNNING_TIME_IN_MILLIS)
            .apply()
    }

}
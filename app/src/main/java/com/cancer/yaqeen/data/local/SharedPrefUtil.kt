package com.cancer.yaqeen.data.local

import android.content.SharedPreferences
import com.google.gson.Gson
import java.util.*
import javax.inject.Inject

public class SharedPrefEncryptionUtil @Inject constructor(
    val sharedPreferences: SharedPreferences,
    val gson: Gson
) {
    companion object {
        const val PREF_TOKEN = "prefs-token"
        const val PREF_USER = "prefs-user"
        const val PREF_SELECTED_LANGUAGE = "prefs-selected-language"
        const val PREF_HAS_SEEN_BOARDS = "prefs-has-seen-boards"
        const val PREF_IS_LOGGED_IN = "prefs-is-logged-in"
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

    fun selectedLanguageIsEnglish(): Boolean = selectedLanguage == Locale.ENGLISH.language


    fun clearUserPreferenceStorage() {
        sharedPreferences
            .edit()
            .remove(PREF_TOKEN)
            .remove(PREF_USER)
            .remove(PREF_IS_LOGGED_IN)
            .apply()
    }

}
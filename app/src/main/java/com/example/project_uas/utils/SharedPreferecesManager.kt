package com.example.project_uas.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesManager {
    private const val PREF_NAME = "user_session"
    private const val KEY_USER_ID = "user_id"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserId(context: Context, userId: Int) {
        val prefs = getPreferences(context)
        prefs.edit().putInt(KEY_USER_ID, userId).apply()
    }

    fun getUserId(context: Context): Int {
        val prefs = getPreferences(context)
        return prefs.getInt(KEY_USER_ID, -1) // -1 jika belum login
    }

    fun clearSession(context: Context) {
        getPreferences(context).edit().clear().apply()
    }
}
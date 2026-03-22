package com.example.pm2examengrupo2.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val USER_ID = "user_id"
        private const val IS_LOGGED_IN = "is_logged_in"
    }

    fun saveSession(userId: Int) {
        prefs.edit().apply {
            putInt(USER_ID, userId)
            putBoolean(IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getUserId(): Int = prefs.getInt(USER_ID, -1)

    fun isLoggedIn(): Boolean = prefs.getBoolean(IS_LOGGED_IN, false)

    fun logout() {
        prefs.edit().clear().apply()
    }
}
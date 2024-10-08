package com.ead.ecommerceapp.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        const val KEY_TOKEN = "token"
        const val KEY_ROLE = "role"
        const val KEY_EMAIL = "email"
        const val KEY_USER_ID = "userId"
    }

    fun saveUserSession(token: String, role: String, email: String, userId: String) {
        val editor = prefs.edit()
        editor.putString(KEY_TOKEN, token)
        editor.putString(KEY_ROLE, role)
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_USER_ID, userId)
        editor.apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)
    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)
    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)
    fun isLoggedIn(): Boolean = prefs.getString(KEY_TOKEN, null) != null

    fun logout() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}

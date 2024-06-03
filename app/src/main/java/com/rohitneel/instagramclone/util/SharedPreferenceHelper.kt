package com.rohitneel.instagramclone.util

import android.content.Context


object SharedPreferencesHelper {
    private const val PREF_NAME = "appPrefs"
    private const val KEY_FIRST_LAUNCH = "firstLaunch"

    fun isFirstLaunch(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun setFirstLaunch(context: Context, isFirstLaunch: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, isFirstLaunch).apply()
    }

    fun getCredentials(context: Context): Pair<String, String>? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val password = prefs.getString("password", null)
        if (email != null && password != null) {
            return Pair(email, password)
        }
        return null
    }

    fun saveCredentials(context: Context, email: String, password: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString("email", email)
            .putString("password", password)
            .apply()
    }

}

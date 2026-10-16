package com.mypes.vinto.sync

import android.content.Context

object AuthSession {
    private const val PREFERENCES = "mypes_auth"
    private const val ACCESS_TOKEN = "access_token"

    fun token(context: Context): String? =
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .getString(ACCESS_TOKEN, null)

    fun saveToken(context: Context, token: String) {
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .edit()
            .putString(ACCESS_TOKEN, token)
            .apply()
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .edit()
            .remove(ACCESS_TOKEN)
            .apply()
    }
}

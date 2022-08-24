package com.example.kurlyflow.hr.worker

import android.content.Context

object WorkerLoginSharedPreference {
    private val key = "login"

    fun setUserAccessToken(context: Context, token: String) {
        val preference = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putString("accessToken", token)
        editor.commit()
    }

    fun getUserAccessToken(context: Context): String {
        val preference = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        return preference.getString("accessToken", "").toString()
    }

    fun clearUserAccessToken(context: Context) {
        val preference = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val editor = preference.edit()
        editor.clear()
        editor.commit()
    }
}
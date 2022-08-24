package com.example.kurlyflow.working

import android.content.Context

object WorkingLoginSharedPreference {
    /*
    * key = [picking, packing, end]
    * */
    fun setUserAccessToken(key: String, context: Context, token: String) {
        val preference = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putString("accessToken", token)
        editor.commit()
    }

    fun getUserAccessToken(key: String, context: Context): String {
        val preference = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        return preference.getString("accessToken", "").toString()
    }

    fun clearUserAccessToken(key: String, context: Context) {
        val preference = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val editor = preference.edit()
        editor.clear()
        editor.commit()
    }
}
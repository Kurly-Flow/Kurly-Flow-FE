package com.example.kurlyflow.hr.manager

import android.content.Context

object ManagerLoginSharedPreference {
    private val key = "login"

    fun setUserAccessToken(context: Context, token: String) {
        val preference = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putString("accessToken", token)
        editor.commit()
    }

    fun setUserName(context: Context, name: String) {
        val preference = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putString("name", name)
        editor.commit()
    }

    fun setUserRegion(context: Context, region: String) {
        val preference = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putString("region", region)
        editor.commit()
    }

    fun getUserAccessToken(context: Context): String {
        val preference = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        return preference.getString("accessToken", "").toString()
    }

    fun getUserName(context: Context): String {
        val preference = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        return preference.getString("name", "").toString()
    }

    fun getUserRegion(context: Context): String {
        val preference = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        return preference.getString("region", "").toString()
    }

    fun clearUserInfo(context: Context) {
        val preference = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val editor = preference.edit()
        editor.clear()
        editor.commit()
    }
}
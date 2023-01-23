package com.swapnilk.truelink.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferences(context: Context) {
    private val mApppref: String = "mAppPref"
    private val clearPref: String = "Truelink"


    var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(mApppref, Context.MODE_PRIVATE)

    fun setLogin(
        userName: String,
        userRole: String,
        userEmail: String,
        userPhone: String,
        isLoogedIn: Boolean
    ) {
        val editor = sharedPreferences.edit()
        editor.putString("userName", userName)
        editor.putString("email", userEmail)
        editor.putString("phone", userPhone)
        editor.putBoolean("isLoggedIn", isLoogedIn)
        editor.commit()
    }

    fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean("firstlaunch", true)
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    fun isProfileUpdate(): Boolean {
        return sharedPreferences.getBoolean("isProfile", false)
    }

    fun setAccessToken(accessToken: String) {
        val editor = sharedPreferences.edit()
        editor.putString("accessToken", accessToken)
        editor.commit()
    }

    fun setFirstLaunch(b: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("firstlaunch", b)
        editor.commit()
    }

    fun setLoggedIn(b: Boolean) {
        val edotor = sharedPreferences.edit()
        edotor.putBoolean("isLoggedIn", b)
        edotor.commit()
    }

    fun setProfileUpdate(b: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isProfile", b)
        editor.commit()

    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString("accessToken", "")

    }

    fun storeFCM(token: String?) {
        val editor = sharedPreferences.edit()
        editor.putString("fcmToken", token)
        editor.commit()

    }

    fun getFCM(): String? {
        return sharedPreferences.getString("fcmToken", "")
    }

}
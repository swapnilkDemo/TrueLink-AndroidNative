package com.sapnilk.truelink.utils

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

}
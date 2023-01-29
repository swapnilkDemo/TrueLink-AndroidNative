package com.swapnilk.truelink.data.online

import android.content.Context
import com.swapnilk.truelink.utils.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor(context: Context) : Interceptor {
    private val sharedPreferences: SharedPreferences = SharedPreferences(context)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", sharedPreferences.getAccessToken()!!).build()

        return chain.proceed(request)
    }

}
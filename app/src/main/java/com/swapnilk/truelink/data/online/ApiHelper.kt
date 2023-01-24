package com.swapnilk.truelink.data.online

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.swapnilk.truelink.utils.SharedPreferences
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

class ApiHelper(context: Context) {
    // Create a client
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthorizationInterceptor(context))
        .build()

    public val apolloClient = ApolloClient.Builder()
        .serverUrl("https://tuelink.neki.dev/graphql")
        .okHttpClient(okHttpClient)
        .build()

    class AuthorizationInterceptor(context: Context) : Interceptor {
        private val sharedPreferences: SharedPreferences = SharedPreferences(context)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .addHeader("Authorization", sharedPreferences.getAccessToken()!!)
                .build()

            return chain.proceed(request)
        }

    }
}

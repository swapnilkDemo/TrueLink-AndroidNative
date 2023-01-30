package com.swapnilk.truelink.data.online

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.google.android.datatransport.runtime.dagger.Module
import com.google.android.datatransport.runtime.dagger.Provides
import com.swapnilk.truelink.utils.SharedPreferences
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import javax.inject.Singleton

@Module
object ApolloHelper {
    private const val SERVER_URL = "https://tuelink.neki.dev/graphql"
    private const val HEADER_AUTHORIZATION = "Authorization"

    @Provides
    @Singleton
    fun provideApolloClient(applicationContext: Context): ApolloClient {
        val okHttpClient =
            OkHttpClient.Builder().addInterceptor(AuthorizationInterceptor(applicationContext))
                .build()

        return ApolloClient.Builder().serverUrl(SERVER_URL)
            .okHttpClient(okHttpClient).build()
    }

    // Create a client
    class AuthorizationInterceptor(context: Context) : Interceptor {
        private val sharedPreferences: SharedPreferences = SharedPreferences(context)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .addHeader(HEADER_AUTHORIZATION, sharedPreferences.getAccessToken()!!).build()

            return chain.proceed(request)
        }

    }
}

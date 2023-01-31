package com.swapnilk.truelink.data.online

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.google.android.datatransport.runtime.dagger.Module
import com.google.android.datatransport.runtime.dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import javax.inject.Singleton

@Module
object ApolloHelper {
    const val SERVER_URL = "https://tuelink.neki.dev/graphql"
    private const val HEADER_AUTHORIZATION = "Authorization"

    @Provides
    @Singleton
    fun provideApolloClient(accessToken: String): ApolloClient {
        val okHttpClient =
            OkHttpClient.Builder().addInterceptor(AuthorizationInterceptor(accessToken))
                .build()

        return ApolloClient.Builder().serverUrl(SERVER_URL)
            .okHttpClient(okHttpClient).build()
    }

    // Create a client
    class AuthorizationInterceptor(accessToken: String) : Interceptor {
        val token = accessToken
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .addHeader(HEADER_AUTHORIZATION, token).build()

            return chain.proceed(request)
        }

    }
}

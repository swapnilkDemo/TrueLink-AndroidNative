package com.sapnilk.truelink.data.online

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Query

class ApiHelper(context: Context) {
    // Create a client
    val apolloClient = ApolloClient.Builder()
        .serverUrl("https://example.com/graphql")
        .build()

    // Execute your query. This will suspend until the response is received.
    fun HeroQuery {
        val response = apolloClient.query(HeroQuery(id = "1")).execute()
        println("Hero.name=${response.data?.hero?.name}")
    }

}
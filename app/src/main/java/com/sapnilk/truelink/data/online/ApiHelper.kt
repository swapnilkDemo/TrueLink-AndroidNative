package com.sapnilk.truelink.data.online

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Query

class ApiHelper(context: Context) {
    // Create a client
    private val apolloClient = ApolloClient.Builder()
        .serverUrl("https://tuelink.neki.dev/graphql")
        .build()

    // Execute your query. This will suspend until the response is received.

}
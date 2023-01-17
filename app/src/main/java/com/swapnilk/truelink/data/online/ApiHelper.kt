package com.swapnilk.truelink.data.online

import android.content.Context
import com.apollographql.apollo3.ApolloClient

class ApiHelper(context: Context) {
    // Create a client
    public val apolloClient = ApolloClient.Builder()
        .serverUrl("https://tuelink.neki.dev/graphql")
        .build()


}

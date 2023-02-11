package com.swapnilk.truelink.data.online.model

data class RecentScansModel(
    val isVerified: Boolean,
    val faviconUrl: Int,
    val domainName: String,
    val actualUrl: String,
    val time: String,
    val source: String,
    val sourceIcon: Int,
    val isPhishing: Boolean,
    val isSocialMedia: Boolean,
    val reportCount: String
) {
}
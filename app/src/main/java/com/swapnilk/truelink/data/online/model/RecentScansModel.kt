package com.swapnilk.truelink.data.online.model

data class RecentScansModel(
    val isVerified: Boolean?,
    val faviconUrl: String?,
    val domainName: String?,
    val actualUrl: String?,
    val time: String?,
    val source: String?,
    val sourceIcon: String?,
    val isPhishing: Boolean?,
    val isSocialMedia: Boolean?,
    val reportCount: String?
) {
}
package com.swapnilk.truelink.data.online.model

data class RecentScansModel(
    val isVerified: Boolean?,
    val faviconUrl: String?,
    val domainName: String?,
    val actualUrl: String?,
    val time: String?,
    val source: String?,
    val sourceIcon: String?,
    val phishing: Int?,
    val spam: Int?,
    val malware: Int?,
    val fradulent: Int?,
    val category: String?,
    val https: Boolean?
) {
}
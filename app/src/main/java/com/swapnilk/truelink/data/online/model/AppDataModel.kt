package com.swapnilk.truelink.data.online.model

data class AppDataModel(
    val appIconUrl: Int?,
    val totalLinks: Int?,
    val safeLinks: Int?,
    val clickedLinks: Int?,
    val suspiciousLinks: Int?,
    val scannedFromNotification: Int?,
    val scannedWithinBrowser: Int?,
    val verifiedLinks: Int?,
    val packageName: String?,
    val bgColor: Int?,
    val isSelected: Boolean?
)
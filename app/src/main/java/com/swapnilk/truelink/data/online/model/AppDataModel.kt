package com.swapnilk.truelink.data.online.model

data class AppDataModel(
    val linksCount: Int,
    val clickedLinks: Int,
    val safeLinks: Int,
    val scannedFromNotifications: Int,
    val scannedWithinBrowser: Int,
    val suspicousLinks: Int,
    val verifiedLinks: Int
)
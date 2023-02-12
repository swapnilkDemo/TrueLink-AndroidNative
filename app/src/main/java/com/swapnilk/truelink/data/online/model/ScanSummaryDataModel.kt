package com.swapnilk.truelink.data.online.model

data class ScanSummaryDataModel(
    val linksCount: Int,
    val clickedLinks: Int,
    val safeLinks: Int,
    val scannedFromNotifications: Int,
    val scannedWithinBrowser: Int,
    val suspiciousLinks: Int,
    val verifiedLinks: Int
)
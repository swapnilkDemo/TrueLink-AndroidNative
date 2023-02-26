package com.swapnilk.truelink.data.online.model

import com.example.AppScanHistoryQuery

data class AppDataModel(
    val appIconUrl: String?,
    val appName: String?,
    val totalLinks: Int?,
    val safeLinks: Int?,
    val clickedLinks: Int?,
    val suspiciousLinks: Int?,
    val scannedFromNotification: Int?,
    val scannedWithinBrowser: Int?,
    val verifiedLinks: Int?,
    val packageName: String?,
    val bgColor: Int?,
    val isSelected: Boolean?,
    val senders: List<AppScanHistoryQuery.Sender?>?,
    val allScansMap: List<List<String?>?>?,
    val safeScansMap: List<List<String?>?>?,
    val verifiedScansMap: List<List<String?>?>?,
    val suspiciousScansMap: List<List<String?>?>?
)
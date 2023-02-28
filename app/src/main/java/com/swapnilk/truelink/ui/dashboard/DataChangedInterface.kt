package com.swapnilk.truelink.ui.dashboard

import com.example.AppScanHistoryQuery
import com.swapnilk.truelink.data.online.model.AppDataModel

interface DataChangedInterface {
    fun onAppSelected(appDataModel: AppDataModel)
    fun onSenderSelected(senderList: ArrayList<AppScanHistoryQuery.Sender>, packageName: String?)
}
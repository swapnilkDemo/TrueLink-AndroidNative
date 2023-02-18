package com.swapnilk.truelink.ui.dashboard

import com.swapnilk.truelink.data.online.model.AppDataModel

interface DataChangedInterface {
    fun onAppSelected(appDataModel: AppDataModel)
}
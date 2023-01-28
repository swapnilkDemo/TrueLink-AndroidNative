package com.swapnilk.truelink.ui.bulk_scan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BulkScanViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Bulk Scan Fragment"
    }
    val text: LiveData<String> = _text
}
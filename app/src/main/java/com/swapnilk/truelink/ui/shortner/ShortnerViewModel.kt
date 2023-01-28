package com.swapnilk.truelink.ui.shortner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShortnerViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Shortener Fragment"
    }
    val text: LiveData<String> = _text
}
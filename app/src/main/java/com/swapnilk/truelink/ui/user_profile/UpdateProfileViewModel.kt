package com.swapnilk.truelink.ui.user_profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UpdateProfileViewModel : ViewModel() {


    private val _text = MutableLiveData<String>().apply {
        value = "This is Update Profile Fragment"
    }
    val text: LiveData<String> = _text
}
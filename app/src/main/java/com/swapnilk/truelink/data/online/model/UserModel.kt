package com.swapnilk.truelink.data.online.model

import android.location.Location
import com.example.type.Gender

data class UserModel(
    val uId: String,
    val uName: String,
    val uPhone: String,
    val uDialCode: String,
    val uEmail: String,
    val uDOB: String,
    val uLocation: Location?,
    val uGender: Gender?,
    val uCity: String,
    val uState: String,
    val uCountry: String,
    val uAddress: String,
    val uAvtar: String
)
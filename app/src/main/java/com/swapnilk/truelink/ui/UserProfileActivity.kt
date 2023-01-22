package com.swapnilk.truelink.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.apollographql.apollo3.ApolloClient
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.swapnilk.truelink.R
import com.swapnilk.truelink.utils.CommonFunctions
import com.swapnilk.truelink.utils.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.util.*
import kotlin.coroutines.CoroutineContext

class UserProfileActivity : AppCompatActivity(), CoroutineScope {
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    lateinit var sharedPreferences: SharedPreferences
    lateinit var commonFunctions: CommonFunctions
    lateinit var apolloClient: ApolloClient

    lateinit var cardViewMale: MaterialCardView
    lateinit var cardViewFemale: MaterialCardView
    lateinit var cardViewOther: MaterialCardView

    lateinit var textViewMale: TextView
    lateinit var textViewFemale: TextView
    lateinit var textViewOther: TextView

    lateinit var textInputName: TextInputEditText
    lateinit var autotextDay: AutoCompleteTextView
    lateinit var autoTextMonth: AutoCompleteTextView
    lateinit var autoTextYear: AutoCompleteTextView
    lateinit var ivGoogle: ImageView
    lateinit var ivLinkedInL: ImageView
    lateinit var progressButton: CircularProgressButton

    var gender: String = ""
    var dob: String = ""
    var name: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        /////////////////////Initialize Required Classes//////////
        sharedPreferences = SharedPreferences(this@UserProfileActivity)
        commonFunctions = CommonFunctions(this@UserProfileActivity)
        apolloClient = ApolloClient.Builder()
            .serverUrl("https://truelink.neki.dev/graphql/")
            .build()
        /////////////////////Initialize UI//////////////
        Initialize()


    }

    private fun Initialize() {
        ////
        cardViewMale = findViewById(R.id.cardViewMale)
        cardViewFemale = findViewById(R.id.cardViewFemale)
        cardViewOther = findViewById(R.id.cardViewOther)

        cardViewMale.setOnClickListener {
            cardViewMale.toggle()
            cardViewFemale.isChecked = false
            cardViewOther.isChecked = true
            gender = "Male"
        }

        cardViewFemale.setOnClickListener {
            cardViewFemale.toggle()
            cardViewFemale.isChecked = false
            cardViewFemale.isChecked = false
            gender = "Female"

        }

        cardViewOther.setOnClickListener {
            cardViewOther.toggle()
            cardViewMale.isChecked = false
            cardViewFemale.isChecked = false
            gender = "Other"
        }

        textInputName = findViewById(R.id.edit_name)

        autotextDay = findViewById(R.id.autoTextDay)
        autoTextMonth = findViewById(R.id.autoTextMonth)
        autoTextYear = findViewById(R.id.autoTextYear)

        val calendar: Calendar = Calendar.getInstance()
        var dayArray: ArrayList<Int> = ArrayList()
        for (i in 0..30) {
            //var day: Int = calendar.get(Calendar.DAY_OF_MONTH) +1
            dayArray.add(i + 1)
        }
        autotextDay.setAdapter(
            ArrayAdapter<Int>(
                this@UserProfileActivity,
                android.R.layout.simple_dropdown_item_1line,
                dayArray
            )
        )
    }
}
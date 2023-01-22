package com.swapnilk.truelink.ui

import android.os.Bundle
import android.view.View
import android.widget.*
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
    lateinit var autotextDay: Spinner
    lateinit var autoTextMonth: Spinner
    lateinit var autoTextYear: Spinner
    lateinit var ivGoogle: ImageView
    lateinit var ivLinkedInL: ImageView
    lateinit var progressButton: CircularProgressButton

    var gender: String = ""
    var dob: String = ""
    var name: String = ""
    var day: Int = 0
    var monthh: String = ""
    var yearh: String = ""

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
            cardViewOther.isChecked = false
            gender = "Male"
        }

        cardViewFemale.setOnClickListener {
            cardViewFemale.toggle()
            cardViewMale.isChecked = false
            cardViewOther.isChecked = false
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
            ArrayAdapter(
                this@UserProfileActivity,
                android.R.layout.simple_dropdown_item_1line,
                dayArray
            )
        )
        autotextDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    day = parent.getItemAtPosition(position) as Int
                } else {
                    commonFunctions.showErrorSnackBar(
                        this@UserProfileActivity,
                        view,
                        getString(R.string.error)
                    )
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        val monthName = arrayOf(
            "January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"
        )

        val cal = Calendar.getInstance()
        val month1 = monthName[cal[Calendar.MONTH]]

        val mnth = arrayOf(
            month1,
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mnth)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        autoTextMonth.setAdapter(adapter)
        autoTextMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    monthh = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        val years = ArrayList<String>()
        val thisYear = Calendar.getInstance()[Calendar.YEAR]
        for (i in 1900..thisYear) {
            years.add(Integer.toString(i))
        }
        val adapterYear = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)

        autoTextYear.adapter = adapterYear

        autoTextYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    (parent.getChildAt(0) as TextView).setTextColor(getColor(R.color.gray_200))
                    yearh = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }// End Initialize
}
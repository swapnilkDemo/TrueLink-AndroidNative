package com.swapnilk.truelink.ui

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import br.com.simplepass.loadingbutton.animatedDrawables.ProgressType
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.example.UpdateUserMutation
import com.example.type.Gender
import com.example.type.UpdateUserInput
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.ozcanalasalvar.library.utils.DateUtils
import com.ozcanalasalvar.library.view.popup.DatePickerPopup
import com.ozcanalasalvar.library.view.popup.DatePickerPopup.OnDateSelectListener
import com.swapnilk.truelink.MainActivity
import com.swapnilk.truelink.R
import com.swapnilk.truelink.utils.CommonFunctions
import com.swapnilk.truelink.utils.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class UserProfileActivity : AppCompatActivity(), CoroutineScope {
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var commonFunctions: CommonFunctions
    private lateinit var apolloClient: ApolloClient

    private lateinit var cardViewMale: MaterialCardView
    private lateinit var cardViewFemale: MaterialCardView
    private lateinit var cardViewOther: MaterialCardView


    private lateinit var editName: TextInputEditText
    private lateinit var tvSelectDob: TextView

    private lateinit var ivGoogle: ImageView
    private lateinit var ivLinkedInL: ImageView
    private lateinit var progressButton: CircularProgressButton
    private lateinit var datePicker: LinearLayout
    private var datePickerPopup: DatePickerPopup? = null

    var gender: String = ""
    var dob: String = ""
    var name: String = ""
    var dateOfBirth: Long? = null

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
        ///////////////////////////////////////////////

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

        editName = findViewById(R.id.edit_name)
        tvSelectDob = findViewById(R.id.tv_select_dob)

        datePickerPopup = DatePickerPopup.Builder()
            .from(this@UserProfileActivity)
            .offset(3)
            .pickerMode(com.ozcanalasalvar.library.view.datePicker.DatePicker.MONTH_ON_FIRST)
            .textSize(19)
            .endDate(DateUtils.getCurrentTime())
            .currentDate(DateUtils.getTimeMiles(1997, 7, 7))
            .startDate(DateUtils.getTimeMiles(1900, 1, 1))
            .build()
        datePicker = findViewById(R.id.ll_select_dob)
        datePicker.setOnClickListener {
            datePickerPopup?.show()
        }

        datePickerPopup?.setListener(OnDateSelectListener { dp, date, day, month, year ->
            tvSelectDob.setText("$month/$day/$year")
            dateOfBirth = commonFunctions.convertDate2TimeStamp("$month/$day/$year")
        })


        progressButton = findViewById(R.id.btn_finish)
        progressButton.run {
            setOnClickListener {
                if (gender.isNullOrEmpty()) {
                    commonFunctions.showErrorSnackBar(
                        this@UserProfileActivity,
                        progressButton,
                        getString(R.string.error_select_gender)
                    )
                } else if (editName.text.isNullOrEmpty()) {
                    commonFunctions.showErrorSnackBar(
                        this@UserProfileActivity,
                        progressButton,
                        getString(R.string.error_enter_full_name)
                    )
                } else if (dateOfBirth == null) {
                    commonFunctions.showErrorSnackBar(
                        this@UserProfileActivity,
                        progressButton,
                        getString(R.string.error_dob)
                    )
                } else {
                    //////////////////Initiate Login///////////////////
                    morphDoneAndRevert(this@UserProfileActivity)
                    /////////////////Call Update Mutation /////////////
                    val genderEnum: Gender = Gender.safeValueOf(gender)
                    val updateUserInput = UpdateUserInput(
                        Optional.Present(editName.text.toString()),
                        Optional.Absent,
                        Optional.Absent,
                        Optional.Absent,
                        Optional.Absent,
                        Optional.Absent,
                        Optional.Absent,
                        Optional.Absent,
                        Optional.Present(sharedPreferences.getAccessToken()),
                        Optional.Present(dateOfBirth)
                        //,Optional.Present(genderEnum)
                    )
                    val updateUserMutation: UpdateUserMutation =
                        UpdateUserMutation(
                            updateUserInput

                        )
                    launch {
                        val response: ApolloResponse<UpdateUserMutation.Data> =
                            apolloClient.mutation(updateUserMutation).execute()
                        afterResult(response)
                    }
                }
            }
        }
    }// End Initialize

    private fun afterResult(response: ApolloResponse<UpdateUserMutation.Data>) {
        if (response.data?.updateUser?.success == true) {
            sharedPreferences.setProfileUpdate(true)
            startMain()
        } else {
            commonFunctions.showErrorSnackBar(
                this@UserProfileActivity,
                progressButton,
                response.data?.updateUser?.message.toString()
            )
        }

    }

    private fun startMain() {
        val intent = Intent(this@UserProfileActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    ///////////////////////Progress Button Animations/////////////////////////////
    private fun defaultColor(context: Context) =
        ContextCompat.getColor(context, android.R.color.black)

    private fun defaultDoneImage(resources: Resources): Bitmap =
        BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_foreground)

    private fun CircularProgressButton.morphDoneAndRevert(
        context: Context,
        fillColor: Int = defaultColor(context),
        bitmap: Bitmap = defaultDoneImage(context.resources),
        doneTime: Long = 3000,
        revertTime: Long = 4000
    ) {
        progressType = ProgressType.INDETERMINATE
        startAnimation()
        Handler().run {
            postDelayed({ doneLoadingAnimation(fillColor, bitmap) }, doneTime)
            postDelayed(::revertAnimation, revertTime)
        }
    }
}
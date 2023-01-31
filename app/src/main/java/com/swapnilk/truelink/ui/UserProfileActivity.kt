package com.swapnilk.truelink.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.NetworkOnMainThreadException
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.com.simplepass.loadingbutton.animatedDrawables.ProgressType
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.network.okHttpClient
import com.auth0.android.jwt.JWT
import com.example.TokenUpdateMutation
import com.example.UpdateUserMutation
import com.example.type.Gender
import com.example.type.LocationInput
import com.example.type.LocationType
import com.example.type.UpdateUserInput
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.messaging.FirebaseMessaging
import com.ozcanalasalvar.library.utils.DateUtils
import com.ozcanalasalvar.library.view.popup.DatePickerPopup
import com.swapnilk.truelink.MainActivity
import com.swapnilk.truelink.R
import com.swapnilk.truelink.data.online.AuthorizationInterceptor
import com.swapnilk.truelink.ui.VerifyOTPFragment.Companion.TAG
import com.swapnilk.truelink.utils.CommonFunctions
import com.swapnilk.truelink.utils.PermissionUtils
import com.swapnilk.truelink.utils.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.text.SimpleDateFormat
import java.util.*
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
    private lateinit var llCardmMale: LinearLayout
    private lateinit var llCardFemale: LinearLayout
    private lateinit var llCardOther: LinearLayout

    private lateinit var editName: TextInputEditText
    private lateinit var tvSelectDob: TextView

    private lateinit var ivGoogle: ImageView
    private lateinit var ivLinkedInL: ImageView
    private lateinit var progressButton: CircularProgressButton
    private lateinit var datePicker: LinearLayout
    private lateinit var datePickerPopup: DatePickerPopup

    var gender: Gender? = null
    var dob: String = ""
    var name: String = ""
    var dateOfBirth: Long? = null
    private var latLang: List<Double>? = null
    lateinit var permissionUtils: PermissionUtils
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val calendar = Calendar.getInstance()

    ///////////////////////required Permissions/////////////
    private lateinit var permissionsArray: Array<String>

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        /////////////////////Initialize Required Classes//////////
        permissionUtils = PermissionUtils(this@UserProfileActivity)
        sharedPreferences = SharedPreferences(this@UserProfileActivity)
        commonFunctions = CommonFunctions(this@UserProfileActivity)
        commonFunctions.setStatusBar(this)
        ///////////////////////Initialize ApolloClient////////////////////////////
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor(this@UserProfileActivity)).build()
        apolloClient = ApolloClient.Builder().serverUrl("https://truelink.neki.dev/graphql/")
            .okHttpClient(okHttpClient).build()

        /////////////////////Initialize UI//////////////
        initialize()
        //////////////////////refresh Token if Expired///////////////////////
        val refreshToken = sharedPreferences.getRefreshToken();
        if (!refreshToken.isNullOrEmpty() && commonFunctions.checkConnection(this@UserProfileActivity))
            refreshAccessToken(refreshToken)
        /////////////////Request Permissions////////////
        getUserLocation()
        generateFCMToken()
        ////////////////////Get Extras//////////////////
        if (intent.extras != null) {
            gender = intent.extras!!.getSerializable("gender") as Gender?
            dob = intent.extras!!.getString("dob").toString()
            name = intent.extras!!.getString("fullName").toString()

            ///////////////////Set Values///////////////////////
            if (gender == Gender.MALE) setSelected(
                cardViewMale,
                llCardmMale,
                getColor(R.color.colorSecondary),
                getColor(R.color.male_blue)
            )
            else if (gender == Gender.FEMALE) setSelected(
                cardViewFemale,
                llCardFemale,
                getColor(R.color.colorSecondary),
                getColor(R.color.female_marun)
            )
            else if (gender == Gender.OTHERS) setSelected(
                cardViewOther,
                llCardOther,
                getColor(R.color.colorSecondary),
                getColor(R.color.other_voilet)
            )

            tvSelectDob.text = commonFunctions.convertTimeStamp2Date(dob)
            editName.setText(name)
        }
    }

    private fun getUserLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationClient.lastLocation.addOnCompleteListener(this) {
                    val location: Location? = it.result
                    if (location != null) {
                        latLang?.plus(location.longitude)
                        latLang?.plus(location.longitude)
                    }
                }
            }
        } else {
            requestPermissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), 1
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getUserLocation()
            }
        }
    }

    @SuppressLint("StringFormatInvalid")
    private fun generateFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = getString(R.string.msg_token_fmt, token)
            if (msg.isNotEmpty()) sharedPreferences.storeFCM(token)
            Log.d(TAG, msg)
//            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })
    }

    private fun initialize() {
        ////
        cardViewMale = findViewById(R.id.cardViewMale)
        cardViewFemale = findViewById(R.id.cardViewFemale)
        cardViewOther = findViewById(R.id.cardViewOther)
        llCardmMale = findViewById(R.id.ll_card_male)
        llCardFemale = findViewById(R.id.ll_card_female)
        llCardOther = findViewById(R.id.ll_card_other)

        cardViewMale.setOnClickListener {
            setSelected(
                cardViewMale,
                llCardmMale,
                getColor(R.color.colorSecondary),
                getColor(R.color.male_blue)
            )
            setDeselected(
                cardViewFemale, llCardFemale, getColor(R.color.light_background)
            )
            setDeselected(
                cardViewOther, llCardOther, getColor(R.color.light_background)
            )
            gender = Gender.MALE
        }

        cardViewFemale.setOnClickListener {
            setSelected(
                cardViewFemale,
                llCardFemale,
                getColor(R.color.colorSecondary),
                getColor(R.color.female_marun)
            )
            setDeselected(
                cardViewMale, llCardmMale, getColor(R.color.light_background)
            )
            setDeselected(
                cardViewOther, llCardOther, getColor(R.color.light_background)
            )
            gender = Gender.FEMALE

        }

        cardViewOther.setOnClickListener {
            setSelected(
                cardViewOther,
                llCardOther,
                getColor(R.color.colorSecondary),
                getColor(R.color.other_voilet)
            )
            setDeselected(
                cardViewMale, llCardmMale, getColor(R.color.light_background)
            )
            setDeselected(
                cardViewFemale, llCardFemale, getColor(R.color.light_background)
            )
            gender = Gender.OTHERS
        }

        editName = findViewById(R.id.edit_name)
        tvSelectDob = findViewById(R.id.tv_select_dob)

        datePickerPopup = DatePickerPopup.Builder().from(this@UserProfileActivity).offset(3)
            //.darkModeEnabled(true)
            .pickerMode(com.ozcanalasalvar.library.view.datePicker.DatePicker.MONTH_ON_FIRST)
            .textSize(19).endDate(DateUtils.getCurrentTime())
            .currentDate(DateUtils.getTimeMiles(1997, 7, 7))
            .startDate(DateUtils.getTimeMiles(1900, 1, 1))
            .build()
        datePicker = findViewById(R.id.ll_select_dob)
        datePicker.setOnClickListener {
            datePickerPopup?.show()
        }

        datePickerPopup?.setListener(DatePickerPopup.OnDateSelectListener { dp, date, day, month, year ->
            val monthName: Int = (month + 1)
            calendar.set(Calendar.MONTH, month)
            val sdf = SimpleDateFormat("MMM")
            var monthStr = sdf.format(calendar.time)
            tvSelectDob.text = "$monthStr, $day   $year"
            dateOfBirth = commonFunctions.convertDate2TimeStamp("$monthName/$day/$year")
        })

        /* val calendar = Calendar.getInstance()
         calendar.set(1900, 1, 1)
         val dateTimeSelectedListener =
             object : OnDateTimeSelectedListener {
                 override fun onDateTimeSelected(selectedDateTime: Calendar) {
                     val month = selectedDateTime.time.month
                     val day = selectedDateTime.time.day
                     val year = selectedDateTime.time.year
                     dateOfBirth = commonFunctions.convertDate2TimeStamp("$month/$day/$year")
                 }
             }
         val dateTimePickerDialog = DialogDateTimePicker(
             this,
             calendar,
             1440,
             dateTimeSelectedListener,
             getString(R.string.select_dob)
         )
         dateTimePickerDialog.setCancelBtnColor(R.color.login_btn)
         dateTimePickerDialog.setSubmitBtnColor(R.color.login_btn)
         dateTimePickerDialog.setSubmitBtnText(getString(R.string.ok))
         dateTimePickerDialog.setCancelBtnTextColor(R.color.gray_200)
         dateTimePickerDialog.setSubmitBtnTextColor(R.color.gray_200)
         datePicker = findViewById(R.id.ll_select_dob)
         datePicker.setOnClickListener {
             dateTimePickerDialog.show()
         }*/

        progressButton = findViewById(R.id.btn_finish)
        progressButton.run {
            setOnClickListener {
                if (commonFunctions.checkConnection(this@UserProfileActivity)) if (gender == null) {
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
                        this@UserProfileActivity, progressButton, getString(R.string.error_dob)
                    )
                } else {
                    //////////////////Initiate Update///////////////////
                    morphDoneAndRevert(this@UserProfileActivity)
                    if (commonFunctions.checkConnection(this@UserProfileActivity))
                        updateUser()
                    else
                        commonFunctions.showErrorSnackBar(
                            this@UserProfileActivity,
                            progressButton,
                            getString(R.string.no_internet)
                        )
                }
                else commonFunctions.showErrorSnackBar(
                    this@UserProfileActivity, progressButton, getString(R.string.no_internet)
                )
            }
        }
    }// End Initialize

    private fun updateUser() {

        /////////////////Call Update Mutation /////////////
        val locationType: LocationType = LocationType.Point
        val locationInput: LocationInput? = latLang?.let { it1 ->
            LocationInput(
                Optional.present(locationType),
                it1
            )
        }
        ////////////////Add required parameters//////////////////
        val updateUserInput = UpdateUserInput(
            Optional.Present(editName.text.toString()),
            Optional.Absent,
            Optional.Present(locationInput),
            Optional.Absent,
            Optional.Absent,
            Optional.Absent,
            Optional.Absent,
            Optional.Absent,
            Optional.Present(sharedPreferences.getFCM()),
            Optional.Present(dateOfBirth),
            Optional.Present(gender)
        )
        ///////////////Initialize mutation/////////////
        val updateUserMutation = UpdateUserMutation(
            updateUserInput

        )
        /////////////////Perform Background Task///////////////////
        try {
            launch {
                val response: ApolloResponse<UpdateUserMutation.Data> =
                    apolloClient.mutation(updateUserMutation).execute()
                afterResult(response)
            }
        } catch (e: Exception) {
            e.stackTrace
        } catch (e: NetworkOnMainThreadException) {
            e.stackTrace
        }
    }

    //////////////////Deselect CardView////////////////////////////
    private fun setDeselected(cardView: MaterialCardView?, linearLayout: LinearLayout, color: Int) {
        if (cardView != null) {
            linearLayout.setBackgroundColor(color)
            cardView.strokeColor = color
            cardView.strokeWidth = 0
        }

    }

    ///////////////////Handle response from server///////////////////
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

    /////////////////////Select CardView /////////////////////////////////
    private fun setSelected(
        cardView: MaterialCardView?, linearLayout: LinearLayout, color: Int, color1: Int
    ) {
        if (cardView != null) {
            linearLayout.setBackgroundColor(color)
            cardView.strokeColor = color1
            cardView.strokeWidth = 2
        }

    }


    //////////////////////////Start MainActivity////////////////////////////
    private fun startMain() {
        val intent = Intent(this@UserProfileActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    ////////////////////////Refresh Token if expired/////////////////////////////////
    private fun refreshAccessToken(refreshToken: String) {
        val jwt = JWT(sharedPreferences.getAccessToken().toString())
        if (jwt.isExpired(10)) {
            var tokenRefresh = TokenUpdateMutation(
                refreshToken
            )

            launch {
                var response: ApolloResponse<TokenUpdateMutation.Data> =
                    apolloClient.mutation(tokenRefresh).execute()
                afterResponse(response)
            }
        }

    }

    ////////////////////////////save tokens//////////////////////////////////////
    private fun afterResponse(response: ApolloResponse<TokenUpdateMutation.Data>) {
        if (response?.data?.tokenUpdate?.success == true) {
            sharedPreferences.setAccessToken(response?.data?.tokenUpdate!!.payload!!.accessToken.toString())
            sharedPreferences.setRefreshToken(response?.data?.tokenUpdate!!.payload!!.refreshToken.toString())
            commonFunctions.showErrorSnackBar(
                this@UserProfileActivity,
                progressButton,
                getString(R.string.token_refresh)
            )
        }
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
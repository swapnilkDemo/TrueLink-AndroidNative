package com.swapnilk.truelink.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.com.simplepass.loadingbutton.animatedDrawables.ProgressType
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.network.ws.WebSocketNetworkTransport
import com.example.UpdateUserMutation
import com.example.type.Gender
import com.example.type.UpdateUserInput
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.messaging.FirebaseMessaging
import com.ozcanalasalvar.library.utils.DateUtils
import com.ozcanalasalvar.library.view.popup.DatePickerPopup
import com.ozcanalasalvar.library.view.popup.DatePickerPopup.OnDateSelectListener
import com.swapnilk.truelink.MainActivity
import com.swapnilk.truelink.R
import com.swapnilk.truelink.ui.VerifyOTPFragment.Companion.TAG
import com.swapnilk.truelink.utils.CommonFunctions
import com.swapnilk.truelink.utils.PermissionUtils
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
    private var latLang: List<Double?>? = null
    lateinit var permissionUtils: PermissionUtils
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        /////////////////////Initialize Required Classes//////////
        permissionUtils = PermissionUtils(this@UserProfileActivity)
        sharedPreferences = SharedPreferences(this@UserProfileActivity)
        commonFunctions = CommonFunctions(this@UserProfileActivity)
        apolloClient = ApolloClient.Builder()
            .serverUrl("https://truelink.neki.dev/graphql/")
            .subscriptionNetworkTransport(
                WebSocketNetworkTransport.Builder()
                    .serverUrl("wss://truelink.neki.dev/graphql/")
                    .addHeader("Authorization", sharedPreferences.getAccessToken()!!)
                    .build()
            )
            .build()
        /////////////////Request Permissions////////////
        //Manifest.permission.POST_NOTIFICATIONS,  Manifest.permission.ACCESS_BACKGROUND_LOCATION
        if (hasPermission(Manifest.permission.POST_NOTIFICATIONS)) generateFCMToken() else
            askPermission(Manifest.permission.POST_NOTIFICATIONS)
        if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) getUserLocation() else
            askPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        /////////////////////Initialize UI//////////////
        initialize()
        ///////////////////////////////////////////////

    }

    private fun getUserLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    latLang = latLang?.plus(location.latitude)
                    latLang = latLang?.plus(location.longitude)
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
            if (msg.isNotEmpty())
                sharedPreferences.storeFCM(token)
            Log.d(TAG, msg)
//            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })
    }

    private fun initialize() {
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
            val monthName: Int = (month + 1)
            tvSelectDob.setText("$monthName/$day/$year")
            dateOfBirth = commonFunctions.convertDate2TimeStamp("$monthName/$day/$year")
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
                        Optional.Present(latLang),
                        Optional.Absent,
                        Optional.Absent,
                        Optional.Absent,
                        Optional.Absent,
                        Optional.Absent,
                        Optional.Present(sharedPreferences.getFCM()),
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

    fun askPermission(permissionName: String) {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    this@UserProfileActivity,
                    permissionName
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@UserProfileActivity,
                    permissionName
                )
            ) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(permissionName)
            }
        }

    }

    private fun hasPermission(permission: String): Boolean {
        try {
            val info: PackageInfo = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_PERMISSIONS
            )
            if (info.requestedPermissions != null) {
                for (p in info.requestedPermissions) {
                    if (p == permission) {
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}
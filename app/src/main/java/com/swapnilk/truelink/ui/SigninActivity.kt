package com.swapnilk.truelink.ui

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import br.com.simplepass.loadingbutton.animatedDrawables.ProgressType
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.exception.ApolloException
import com.example.GetOTPMutation
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.SAVE_ALL
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.hbb20.CountryCodePicker
import com.swapnilk.truelink.MainActivity
import com.swapnilk.truelink.R
import com.swapnilk.truelink.data.online.ApolloHelper
import com.swapnilk.truelink.utils.CommonFunctions
import com.swapnilk.truelink.utils.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class SigninActivity : AppCompatActivity(), CoroutineScope {
    ////////////Start Coroutine for Background Task../////////////
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    //// Required variables///////////////
    lateinit var chktermns: CheckBox
    lateinit var textView: TextView

    private var isPrivacyChecked: Boolean = false

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var countryCodePicker: CountryCodePicker
    private lateinit var editPhone: TextInputEditText
    private lateinit var editCountryCode: TextInputEditText
    private lateinit var circularProgressButton: CircularProgressButton
    private lateinit var txtPrivacyMsg: TextView

    lateinit var commonFunctions: CommonFunctions

    lateinit var apolloClient: ApolloClient
    lateinit var apiHelper: ApolloHelper
    lateinit var view: CoordinatorLayout

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        //////////////////////////Initialize required classes//////
        sharedPrefs = SharedPreferences(applicationContext)
        commonFunctions = CommonFunctions(applicationContext)
        commonFunctions.setStatusBar(this@SigninActivity)

        /////////////Check if previously logged in user////////////
        if (sharedPrefs.isLoggedIn()) startMain()
        else initialize()

    }

    /////////////////////////////Initialize UI////////////////////////
    private fun initialize() {
        editPhone = findViewById(R.id.edit_phone)
        editCountryCode = findViewById(R.id.edit_coutryCode)
        countryCodePicker = findViewById(R.id.ccp)
        txtPrivacyMsg = findViewById(R.id.txt_privacy_msg)
        txtPrivacyMsg.text = commonFunctions.spanTextWithColor(
            getString(R.string.privacy_msg), Color.CYAN, 79, 93
        )

        chktermns = findViewById(R.id.chk_privacy)
        chktermns.setOnCheckedChangeListener { buttonView, isChecked ->
            isPrivacyChecked = isChecked
        }
        txtPrivacyMsg.setOnClickListener {
            showPrivacyPolicy()
        }

        ///////////////Set default selected country code////////////////////
        var code = Integer.parseInt(getString(R.string.default_country))
        editCountryCode.setText(code.toString())
        @Suppress("DEPRECATION") countryCodePicker.setDefaultCountryUsingPhoneCode(code)
        countryCodePicker.setOnCountryChangeListener {
            //////////////Change country code//////////////////////
            code = countryCodePicker.selectedCountryCodeAsInt
            editCountryCode.setText(code.toString())
        }
        /////////////////Perform generate OTP operation////////////////////
        circularProgressButton = findViewById(R.id.btn_generateOtp)
        circularProgressButton.run {
            setOnClickListener {
                ////////////////Data Validations////////////////////////////////
                if (commonFunctions.checkConnection(this@SigninActivity))
                    if (isPrivacyChecked) if (editPhone.text.toString().isNotEmpty()) {
                        //circularProgressButton.isEnabled = false
                        //////////////////Initiate Login///////////////////
                        morphDoneAndRevert(this@SigninActivity)
                        if (commonFunctions.checkConnection(this@SigninActivity)) {
                            try {
                                apolloClient =
                                    ApolloClient.Builder()
                                        .serverUrl(commonFunctions.getServerUrl()).build()
//            apiHelper = ApiHelper(this@SigninActivity)
                            } catch (e: ApolloException) {
                                e.message?.let { Log.d("Exception ", it) }
                            }
                            login()
                        } else {
                            commonFunctions.showErrorSnackBar(
                                this@SigninActivity,
                                editPhone,
                                getString(R.string.no_internet),
                                true
                            )
                        }

                    } else commonFunctions.showErrorSnackBar(
                        this@SigninActivity,
                        circularProgressButton,
                        getString(R.string.enter_mobile),
                        true
                    )
                    else commonFunctions.showErrorSnackBar(
                        this@SigninActivity,
                        circularProgressButton,
                        getString(R.string.error_privacy),
                        true
                    )
                else
                    commonFunctions.showErrorSnackBar(
                        this@SigninActivity,
                        circularProgressButton,
                        getString(R.string.no_internet),
                        true
                    )
            }//End onCLickListener
        }//End Run
    }//End Function

    ////////////////////Login using mobile number/////////////////
    private fun login() {
        try {
            /////////////////Call GetOTP Mutation /////////////
            val getOTPMutation = GetOTPMutation(
                editPhone.text.toString(), editCountryCode.text.toString()
            )
            ///////////Start background thread//////////
            launch {
                val response: ApolloResponse<GetOTPMutation.Data> =
                    apolloClient.mutation(
                        getOTPMutation

                    ).execute()
                if (response != null) afterResult(response)
            }
        } catch (e: ApolloException) {
            e.stackTrace
            commonFunctions.showToast(this@SigninActivity, e.message)
        } catch (e: Exception) {
            e.stackTrace
            commonFunctions.showToast(this@SigninActivity, e.message)
        }
    }

    //////////////////////Handle Response from getOTP server operation////////////
    private fun afterResult(response: ApolloResponse<GetOTPMutation.Data>) {
        if (response.data?.getOTP!!.success) response.data!!.getOTP.request_id?.let {

            var bundle = Bundle()
            bundle.putString("requestId", it)
            bundle.putString("phone", editPhone.text.toString())
            bundle.putString("dailCode", editCountryCode.text.toString())
            bundle.putString("message", response.data?.getOTP!!.message.toString())
            VerifyOTPFragment(bundle).show(supportFragmentManager, VerifyOTPFragment.TAG);
        }
        else commonFunctions.showErrorSnackBar(
            this@SigninActivity,
            circularProgressButton,
            getString(R.string.login_error) + " " + response.data?.getOTP!!.message,
            true
        )
    }

    ///////////////Start Mani Activity////////////////////////
    private fun startMain() {
        if (sharedPrefs.isProfileUpdate()) {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        } else {
            val mainIntent = Intent(this, UserProfileActivity::class.java)
            startActivity(mainIntent)
            finish()
        }
    }

    //////////////////Show Privacy Policy/////////////////////////
    private fun showPrivacyPolicy() {
        var bottomSheetDialog: BottomSheetDialog =
            BottomSheetDialog(this, R.style.BottomSheetDialog)
        val v: View = layoutInflater.inflate(R.layout.bottom_sheet_privacy, null)
        bottomSheetDialog.setContentView(v)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.show()

        val linearLayout: LinearLayout? =
            bottomSheetDialog.findViewById<LinearLayout>(R.id.ll_child)
        val behavior = linearLayout?.let { BottomSheetBehavior.from(it) }
        if (behavior != null) {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            behavior.peekHeight = SAVE_ALL
        }
        bottomSheetDialog.findViewById<TextView>(R.id.txt_privacy)?.text = Html.fromHtml(getString(R.string.privacy_text))

        bottomSheetDialog.findViewById<CircularProgressButton?>(R.id.btn_accept)
            ?.setOnClickListener {
                bottomSheetDialog.dismiss()
                chktermns.isChecked = true
            }
        bottomSheetDialog.findViewById<TextView?>(R.id.txt_privacy_header)?.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
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
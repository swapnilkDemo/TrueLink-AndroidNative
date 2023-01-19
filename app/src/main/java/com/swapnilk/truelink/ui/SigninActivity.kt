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
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import br.com.simplepass.loadingbutton.animatedDrawables.ProgressType
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import br.com.simplepass.loadingbutton.customViews.ProgressButton
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.exception.ApolloException
import com.chaos.view.PinView
import com.example.GetOTPMutation
import com.example.VerifyOTPMutation
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.hbb20.CountryCodePicker
import com.swapnilk.truelink.MainActivity
import com.swapnilk.truelink.R
import com.swapnilk.truelink.utils.CommonFunctions
import com.swapnilk.truelink.utils.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class SigninActivity : AppCompatActivity(), CoroutineScope {
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    lateinit var chktermns: CheckBox
    lateinit var textView: TextView

    private var isPrivacyChecked: Boolean = false

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var countryCodePicker: CountryCodePicker
    private lateinit var edit_phone: TextInputEditText
    private lateinit var edit_countryCode: TextInputEditText
    private lateinit var circulaProgressButton: CircularProgressButton
    private lateinit var txt_privacy_msg: TextView

    lateinit var commonFunctions: CommonFunctions
    lateinit var apolloClient: ApolloClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        //////////////////////////Initialize required objects//////
        sharedPrefs = SharedPreferences(applicationContext)
        commonFunctions = CommonFunctions(applicationContext)
        try {
            apolloClient =
                ApolloClient.Builder().serverUrl("https://truelink.neki.dev/graphql/").build()
        } catch (e: ApolloException) {
            e.message?.let { Log.d("Exception ", it) }
        }
        /////////////Check if previously logged in user////////////
        if (sharedPrefs.isLoggedIn()) startMain()
        else {
            initialize()
        }
    }

    fun initialize() {
        edit_phone = findViewById(R.id.edit_phone)
        edit_countryCode = findViewById(R.id.edit_coutryCode)
//        edit_countryCode.setText(getString(R.string.default_country))
        countryCodePicker = findViewById(R.id.ccp)
        txt_privacy_msg = findViewById(R.id.txt_privacy_msg)
        txt_privacy_msg.text = commonFunctions.spanTextWithColor(
            getString(R.string.privacy_msg), Color.CYAN, 79, 93
        )

        chktermns = findViewById(R.id.chk_privacy)
        chktermns.setOnCheckedChangeListener { buttonView, isChecked ->
            isPrivacyChecked = isChecked
        }
        txt_privacy_msg.setOnClickListener {
            showPrivacyPolicy()
        }

        var code = Integer.parseInt(getString(R.string.default_country))
        edit_countryCode.setText(code.toString())
        @Suppress("DEPRECATION") countryCodePicker.setDefaultCountryUsingPhoneCode(code)
        countryCodePicker.setOnCountryChangeListener {
            code = countryCodePicker.selectedCountryCodeAsInt
            edit_countryCode.setText(code.toString())
        }

        circulaProgressButton = findViewById(R.id.btn_generateOtp)
        circulaProgressButton.run {
            setOnClickListener {
                if (isPrivacyChecked) if (edit_phone.text.toString().isNotEmpty()) {
                    circulaProgressButton.isEnabled = false
                    //////////////////Initiate Login///////////////////
                    morphDoneAndRevert(this@SigninActivity)
                    /////////////////Call GetOTP Mutation /////////////
                    val getOTPMutation = GetOTPMutation(
                        edit_phone.text.toString(), edit_countryCode.text.toString()
                    )
                    ///////////Start background thread//////////
                    launch {
                        val response: ApolloResponse<GetOTPMutation.Data> = apolloClient.mutation(
                            getOTPMutation

                        ).execute()
                        if (response != null) afterResult(response)
                    }

                } else Toast.makeText(
                    this@SigninActivity, getString(R.string.enter_mobile), Toast.LENGTH_SHORT
                ).show()
                else Toast.makeText(
                    this@SigninActivity, getString(R.string.error_privacy), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun afterResult(response: ApolloResponse<GetOTPMutation.Data>) {
        if (response.data?.getOTP!!.success) response.data!!.getOTP.request_id?.let {
            showOtpDialog(
                it
            )
        }
        else Toast.makeText(
            this, "Login Error" + " " + response.data?.getOTP!!.message, Toast.LENGTH_SHORT
        ).show()
    }


    private fun startLogin() {
        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_foreground)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            circulaProgressButton.doneLoadingAnimation(getColor(R.color.login_btn), bitmap)
        }
        Handler().postDelayed(Runnable {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)

    }

    private fun signout() {
        updateUI()
    }

    fun updateUI() {
        /* if (null) {
         } else {
             startMain()
         }*/
    }


    private fun startMain() {
        val mainintent = Intent(this, MainActivity::class.java)
        startActivity(mainintent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === 123) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
        }
    }


    protected open fun showOtpDialog(request_id: String) {
        var bottomSheetDialog: BottomSheetDialog =
            BottomSheetDialog(this, R.style.BottomSheetDialog)
        val v: View = layoutInflater.inflate(R.layout.bottom_sheet_otp, null)
        bottomSheetDialog.setContentView(v)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.show()

        val linearLayout: LinearLayout? =
            bottomSheetDialog.findViewById<LinearLayout>(R.id.ll_child)
        val behavior = linearLayout?.let { BottomSheetBehavior.from(it) }
        if (behavior != null) {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            behavior.peekHeight = SAVE_FIT_TO_CONTENTS
        }
        var pinView = bottomSheetDialog.findViewById<PinView>(R.id.pinView)
        bottomSheetDialog.findViewById<CircularProgressButton>(R.id.btnotp)?.run {
            setOnClickListener {
                if (pinView?.text.isNullOrEmpty()) {
                    Toast.makeText(this@SigninActivity, "Enter OTP", Toast.LENGTH_SHORT).show()
                } else if (pinView!!.text!!.length < 4) {
                    Toast.makeText(
                        this@SigninActivity, "OTP should be of 4 digit", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    //////////////////Initiate OTPVerification///////////////////
                    morphDoneAndRevert(this@SigninActivity)
                    /////////////////Call VerifyOTP Mutation /////////////
                    val otp = pinView?.text.toString()

                    /* val verifyOtpMutation = VerifyOTPMutation(
                         "336173624345383039333231",
                         otp,
                         edit_phone.text.toString(),
                         edit_countryCode.text.toString()
                     )*/
                    val verifyOtpMutation = VerifyOTPMutation(
                        request_id,
                        otp,
                        edit_phone.text.toString(),
                        edit_countryCode.text.toString()
                    )

                    ///////////Start background thread//////////
                    launch {
                        val responseVerify: ApolloResponse<VerifyOTPMutation.Data> =
                            apolloClient.mutation(verifyOtpMutation).execute()
                        if (responseVerify.data?.verifyOTP!!.success == true) startMain()
                        else afterResultVerify(responseVerify)

                    }
                }

            }

        }
    }


    private fun afterResultVerify(response: ApolloResponse<VerifyOTPMutation.Data>) {
        Toast.makeText(
            this, "Login Error " + response.data?.verifyOTP!!.message, Toast.LENGTH_SHORT
        ).show()
    }


    protected open fun showPrivacyPolicy() {
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
        bottomSheetDialog.findViewById<TextView>(R.id.txt_privacy)
            ?.setText(Html.fromHtml(getString(R.string.privacy_text)))

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

    ///////////////////////Generate OPT Button Animations/////////////////////////////
    private fun defaultColor(context: Context) =
        ContextCompat.getColor(context, android.R.color.black)

    private fun defaultDoneImage(resources: Resources): Bitmap =
        BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_foreground)

    fun ProgressButton.morphDoneAndRevert(
        context: Context,
        fillColor: Int = defaultColor(context),
        bitmap: Bitmap = defaultDoneImage(context.resources),
        doneTime: Long = 3000,
        revertTime: Long = 4000
    ) {
        progressType = ProgressType.INDETERMINATE
        startAnimation()
        /* Handler().run {
             postDelayed({ doneLoadingAnimation(fillColor, bitmap) }, doneTime)
             postDelayed(::revertAnimation, revertTime)
         }*/
    }

    fun ProgressButton.morphAndRevert(
        revertTime: Long = 3000, startAnimationCallback: () -> Unit = {}
    ) {
        startAnimation(startAnimationCallback)
        Handler().postDelayed(::revertAnimation, revertTime)
    }

    fun ProgressButton.morphStopRevert(stopTime: Long = 1000, revertTime: Long = 2000) {
        startAnimation()
        Handler().postDelayed(::stopAnimation, stopTime)
        Handler().postDelayed(::revertAnimation, revertTime)
    }
}
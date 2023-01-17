package com.sapnilk.truelink.ui

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.simplepass.loadingbutton.animatedDrawables.ProgressType
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import br.com.simplepass.loadingbutton.customViews.ProgressButton
import com.chaos.view.PinView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.SAVE_FIT_TO_CONTENTS
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.hbb20.CountryCodePicker
import com.sapnilk.truelink.MainActivity
import com.sapnilk.truelink.R
import com.sapnilk.truelink.utils.CommonFunctions
import com.sapnilk.truelink.utils.SharedPreferences


class SigninActivity : AppCompatActivity() {

    val chklogin: String = "login"
    val chkregister: String = "register"

    val TAG: String = "iia_app"
    val RC_SIGN_IN: Int = 123

    lateinit var laysignupbase: ConstraintLayout

    lateinit var laylogin: MaterialCardView
    lateinit var layregister: MaterialCardView

    lateinit var imagesigninlogo: ImageView

    lateinit var txtinpUserType: TextInputLayout
    lateinit var txtinpUserName: TextInputLayout
    lateinit var txtinpUserPhone: TextInputLayout
    lateinit var txtinpFirmName: TextInputLayout
    lateinit var txtinpAddress: TextInputLayout
    lateinit var txtinpEmail: TextInputLayout
    lateinit var txtinpUniqueId: TextInputLayout

    lateinit var autotxtUserType: AutoCompleteTextView
    lateinit var edttxtUserName: TextInputEditText
    lateinit var edttxtUserPhone: TextInputEditText
    lateinit var edtFirmName: TextInputEditText
    lateinit var edtAddress: TextInputEditText
    lateinit var edtEmail: TextInputEditText
    lateinit var edtUniqueId: TextInputEditText
    lateinit var recyclerCollege: RecyclerView
    lateinit var chkArchitect: CheckBox
    lateinit var chkCollege: CheckBox
    lateinit var btnRegister: Button
    lateinit var chktermns: CheckBox
    lateinit var txtterms: Button
    lateinit var textView: TextView
    lateinit var btnsignout: Button
    lateinit var txtregister: Button

    //lateinit var txtlogin:Button
    lateinit var txtphone: TextInputEditText
    lateinit var groupregister: Group

    val usertypes = arrayOf("Architect", "College", "IIA Authority")

    lateinit var storedVerificationId: String

    private var empid: String = ""
    private var username: String = ""
    private var phone: String = ""
    private var email: String = ""
    private var address: String = ""
    private var type: String = ""

    private var enabled: Boolean = false
    private var mat: AlertDialog? = null
    private var autoverified: Boolean = false
    private var isPrivacyChecked: Boolean = false

    private lateinit var btnokterms: Button
    private lateinit var termsview: View
    private lateinit var recyclerterms: RecyclerView
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var countryCodePicker: CountryCodePicker
    private lateinit var edit_phone: TextInputEditText
    private lateinit var edit_countryCode: TextInputEditText
    private lateinit var circulaProgressButton: CircularProgressButton
    private lateinit var txt_privacy_msg: TextView

    lateinit var commonFunctions: CommonFunctions
    //private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Thread.sleep(300)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        sharedPrefs = SharedPreferences(applicationContext)
        commonFunctions = CommonFunctions(applicationContext)
        /////////////Check if previously logged in user////////////
        if (sharedPrefs.isLoggedIn())
            startMain()
        else {
            initialize()
        }
    }

    fun initialize() {
        edit_phone = findViewById(R.id.edit_phone)
//        edit_countryCode = findViewById(R.id.edit_coutryCOde)
//        edit_countryCode.setText(getString(R.string.default_country))
        countryCodePicker = findViewById(R.id.ccp)
        txt_privacy_msg = findViewById(R.id.txt_privacy_msg)
        txt_privacy_msg.text = commonFunctions.spanTextWithColor(
            getString(R.string.privacy_msg),
            Color.CYAN,
            79,
            93
        )

        chktermns = findViewById(R.id.chk_privacy)
        chktermns.setOnCheckedChangeListener { buttonView, isChecked ->
            isPrivacyChecked = isChecked
        }
        txt_privacy_msg.setOnClickListener {
            showPrivacyPolicy()
        }

        val code = Integer.parseInt(getString(R.string.default_country))
        @Suppress("DEPRECATION")
        countryCodePicker.setDefaultCountryUsingPhoneCode(code)

        circulaProgressButton = findViewById(R.id.btn_generateOtp)
        circulaProgressButton.run {
            setOnClickListener {
                if (isPrivacyChecked)
                    if (edit_phone.text.toString().isNotEmpty()) {
                        circulaProgressButton.isEnabled = false
                        ////Initiate Login///////////////////
                        morphDoneAndRevert(this@SigninActivity)
                        showOtpDialog()
                    } else
                        Toast.makeText(
                            this@SigninActivity,
                            getString(R.string.enter_mobile),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                else
                    Toast.makeText(
                        this@SigninActivity,
                        getString(R.string.error_privacy),
                        Toast.LENGTH_SHORT
                    )
                        .show()
            }
        }
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


    fun startMain() {
        val mainintent = Intent(this, MainActivity::class.java)
        startActivity(mainintent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
        }
    }


    protected open fun showOtpDialog() {
        var bottomSheetDialog: BottomSheetDialog =
            BottomSheetDialog(this, R.style.BottomSheetDialog)
        val v: View = layoutInflater.inflate(R.layout.bottom_sheet_otp, null)
        bottomSheetDialog.setContentView(v)
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
        bottomSheetDialog.findViewById<CircularProgressButton>(R.id.btnotp)
            ?.setOnClickListener {
                if (pinView?.text.isNullOrEmpty()) {
                    Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show()
                } else {
                    val otp = pinView?.text.toString()
                    bottomSheetDialog.dismiss()
                    startMain()
                }

            }
    }

    protected open fun showPrivacyPolicy() {
        var bottomSheetDialog: BottomSheetDialog =
            BottomSheetDialog(this, R.style.BottomSheetDialog)
        val v: View = layoutInflater.inflate(R.layout.bottom_sheet_privacy, null)
        bottomSheetDialog.setContentView(v)
        bottomSheetDialog.show()

        val linearLayout: LinearLayout? =
            bottomSheetDialog.findViewById<LinearLayout>(R.id.ll_child)
        val behavior = linearLayout?.let { BottomSheetBehavior.from(it) }
        if (behavior != null) {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            behavior.peekHeight = SAVE_FIT_TO_CONTENTS
        }

        bottomSheetDialog.findViewById<CircularProgressButton?>(R.id.btn_accept)
            ?.setOnClickListener {
                bottomSheetDialog.dismiss()
                chktermns.isChecked = true
            }
        bottomSheetDialog.findViewById<TextView?>(R.id.txt_privacy_header)
            ?.setOnClickListener {
                bottomSheetDialog.dismiss()
            }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mat != null) {
            mat?.dismiss()
            mat = null
        }
    }

    ///////////////////////Generate OPT Button Animations/////////////////////////////
    private fun defaultColor(context: Context) =
        ContextCompat.getColor(context, android.R.color.black)

    private fun defaultDoneImage(resources: Resources): Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.ic_pregnant_woman_white_48dp)

    fun ProgressButton.morphDoneAndRevert(
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

    fun ProgressButton.morphAndRevert(
        revertTime: Long = 3000,
        startAnimationCallback: () -> Unit = {}
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
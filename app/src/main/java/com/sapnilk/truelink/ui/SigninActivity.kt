package com.sapnilk.truelink.ui

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.hbb20.CountryCodePicker
import com.sapnilk.truelink.MainActivity
import com.sapnilk.truelink.R
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
    lateinit var btnlogin: Button
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


    //private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Thread.sleep(300)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        sharedPrefs = SharedPreferences(applicationContext)
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
        chktermns = findViewById(R.id.chk_privacy)
        chktermns.setOnCheckedChangeListener { buttonView, isChecked ->
            isPrivacyChecked = isChecked
        }

        val code = Integer.parseInt(getString(R.string.default_country))
        @Suppress("DEPRECATION")
        countryCodePicker.setDefaultCountryUsingPhoneCode(code)

        btnlogin = findViewById(R.id.button_login)
        btnlogin.setOnClickListener {
            if (isPrivacyChecked)
                if (edit_phone.text.toString().isNotEmpty()) {
                    btnlogin.isEnabled = false
                    ////Initiate Login///////////////////
                    startLogin()
                } else
                    Toast.makeText(this, getString(R.string.enter_mobile), Toast.LENGTH_SHORT)
                        .show()
            else
                Toast.makeText(this, getString(R.string.error_privacy), Toast.LENGTH_SHORT).show()
        }

        circulaProgressButton = findViewById(R.id.btn_generateOtp)
        circulaProgressButton.setOnClickListener {
            if (isPrivacyChecked)
                if (edit_phone.text.toString().isNotEmpty()) {
                    btnlogin.isEnabled = false
                    ////Initiate Login///////////////////
                    circulaProgressButton.morphAndRevert {  }
                    startLogin()
                } else
                    Toast.makeText(this, getString(R.string.enter_mobile), Toast.LENGTH_SHORT)
                        .show()
            else
                Toast.makeText(this, getString(R.string.error_privacy), Toast.LENGTH_SHORT).show()
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

    private fun showRegister() {
        laylogin.visibility = View.GONE
    }

    private fun showLogin() {
        laylogin.visibility = View.VISIBLE
    }

    fun startMain() {
        //println("subcribing to ${auth.currentUser.uid}")
        //FirebaseMessaging.getInstance().subscribeToTopic(auth.currentUser.uid).addOnCompleteListener { task -> }
        val mainintent = Intent(this, MainActivity::class.java)
        startActivity(mainintent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        //updateUI(currentUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
        }
    }


    /* protected open fun showOtpDialog(msg: String, function: () -> Unit) {
          mat = MaterialAlertDialogBuilder(this).create()
          val view = LayoutInflater.from(this).inflate(R.layout.otp_layout, null)
          val txtotp = view.findViewById<TextInputLayout>(R.id.txtinp_otp)
          val prog = view.findViewById<ProgressBar>(R.id.progress_otp)
          val btnotp = view.findViewById<Button>(R.id.btn_otp)

          btnotp.setOnClickListener {
              if (txtotp.editText?.text.isNullOrEmpty()) {
                  Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show()
              } else {
                  val otp = txtotp.editText?.text.toString()
                  mat?.dismiss()
              }

          }
          mat?.setView(view)
          mat?.setCancelable(false)
          mat?.show()

      }
  */
    protected open fun showPrivacyPolicy() {
        var bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mat != null) {
            mat?.dismiss()
            mat = null
        }
    }

    private fun defaultColor(context: Context) = ContextCompat.getColor(context, android.R.color.black)
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
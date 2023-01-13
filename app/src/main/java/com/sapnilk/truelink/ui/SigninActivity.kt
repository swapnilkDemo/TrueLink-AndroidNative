package com.sapnilk.truelink.ui

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sapnilk.truelink.MainActivity
import com.sapnilk.truelink.R
import com.sapnilk.truelink.utils.SharedPreferences
import java.util.concurrent.TimeUnit


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
    lateinit var progressslogin: ProgressBar
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

    private lateinit var btnokterms: Button
    private lateinit var termsview: View
    private lateinit var recyclerterms: RecyclerView
    private lateinit var sharedPrefs: SharedPreferences

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
//        laylogin = findViewById(R.id.lay_login_main)
//        progressslogin = findViewById(R.id.progress_login)
        txtphone = findViewById(R.id.txtinp_userphone_login)
        chktermns = findViewById(R.id.chk_privacy)

        btnlogin = findViewById(R.id.button_login)
        btnlogin.setOnClickListener {
            if (txtphone.text.toString().isNotEmpty()) {
                progressslogin.visibility = View.VISIBLE
                btnlogin.isEnabled = false
                ////Initiate Login///////////////////
                startLogin()
            } else
                Toast.makeText(this, "Enter mobile number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startLogin() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
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


    override fun onDestroy() {
        super.onDestroy()
        if (mat != null) {
            mat?.dismiss()
            mat = null
        }
    }


}
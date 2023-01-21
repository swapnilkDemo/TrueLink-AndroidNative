package com.swapnilk.truelink.ui

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import br.com.simplepass.loadingbutton.animatedDrawables.ProgressType
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import br.com.simplepass.loadingbutton.customViews.ProgressButton
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.exception.ApolloException
import com.chaos.view.PinView
import com.example.VerifyOTPMutation
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.swapnilk.truelink.MainActivity
import com.swapnilk.truelink.R
import com.swapnilk.truelink.data.online.ApiHelper
import com.swapnilk.truelink.utils.CommonFunctions
import com.swapnilk.truelink.utils.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class VerifyOTPFragment(bundle: Bundle) : BottomSheetDialogFragment(), CoroutineScope {
    // TODO: Rename and change types of parameters
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    lateinit var viewL: View
    lateinit var commonFunctions: CommonFunctions

    lateinit var apolloClient: ApolloClient
    lateinit var apiHelper: ApiHelper

    //    lateinit var view: CoordinatorLayout
    lateinit var sharedPrefs: SharedPreferences
    lateinit var pinView: PinView
    var bundleGet: Bundle = bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //////////////////////////Initialize required objects//////
        sharedPrefs = activity?.let { SharedPreferences(it) }!!
        commonFunctions = activity?.let { CommonFunctions(it) }!!
        try {
            apolloClient =
                ApolloClient.Builder().serverUrl("https://truelink.neki.dev/graphql/").build()
//            apiHelper = ApiHelper(activity!!)
        } catch (e: ApolloException) {
            e.message?.let { Log.d("Exception ", it) }
        }
        // Inflate the layout for this fragment
        viewL = inflater.inflate(R.layout.bottom_sheet_otp, container, false)
        pinView = viewL.findViewById<PinView>(R.id.pinView)
        viewL.findViewById<CircularProgressButton>(R.id.btnotp)?.run {
            setOnClickListener {
                ///////////////Data validations/////////////////////////
                if (pinView?.text.isNullOrEmpty()) {
                    commonFunctions.showErrorSnackBar(
                        requireActivity(),
                        pinView,
                        getString(R.string.enter_otp)
                    )
                } else if (pinView!!.text!!.length < 4) {
                    commonFunctions.showErrorSnackBar(
                        requireActivity(),
                        pinView,
                        getString(R.string.otp_lenght_error)
                    )
                } else {
                    //////////////////Initiate OTPVerification///////////////////
                    morphDoneAndRevert(requireActivity())
                    /////////////////Call VerifyOTP Mutation /////////////
                    val otp = pinView?.text.toString()
                    ////////////////Object with required parameters/////////////
                    val verifyOtpMutation = bundleGet.getString("requestId")?.let { it1 ->
                        VerifyOTPMutation(
                            it1,
                            otp,
                            bundleGet.getString("phone")!!,
                            bundleGet.getString("dailCode")!!
                        )
                    }

                    ///////////Start background thread//////////
                    launch {
                        val responseVerify: ApolloResponse<VerifyOTPMutation.Data> =
                            apolloClient.mutation(verifyOtpMutation!!).execute()
                        if (responseVerify.data?.verifyOTP!!.success == true) {
                            Log.d("verifyOTP Response :", responseVerify.data.toString())
                            startMain()
                        } else afterResultVerify(responseVerify)

                    }//End Launch
                }//End else
            }// End onclick listener
        }//End run
        return viewL;
    }

    private fun afterResultVerify(response: ApolloResponse<VerifyOTPMutation.Data>) {
        activity?.let {
            commonFunctions.showErrorSnackBar(
                it,
                viewL,
                getString(R.string.login_error) + response.data?.verifyOTP!!.message
            )
            pinView.setText("")
        }

    }

    private fun startMain() {
        if (sharedPrefs.isPrifileUpdate()) {
            val mainIntent = Intent(activity, MainActivity::class.java)
            startActivity(mainIntent)
            activity?.finish()
        } else {
            val profileIntent = Intent(activity, UserProfileActivity::class.java)
            startActivity(profileIntent)
            activity?.finish()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupFullHeight(bottomSheetDialog)
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(R.id.ll_child) as LinearLayout?
        val behavior = bottomSheet?.let { BottomSheetBehavior.from(it) }
        if (behavior != null) {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            behavior.peekHeight = BottomSheetBehavior.SAVE_FIT_TO_CONTENTS
        }
        val layoutParams = bottomSheet?.layoutParams
        val windowHeight = getWindowHeight()
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        if (bottomSheet != null) {
            bottomSheet.layoutParams = layoutParams
        }
//        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    companion object {
        const val TAG = "CustomBottomSheetDialogFragment"
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
}



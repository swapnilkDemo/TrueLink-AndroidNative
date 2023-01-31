package com.swapnilk.truelink.ui.user_profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.NetworkOnMainThreadException
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.network.okHttpClient
import com.example.GetUserQuery
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.ss.profilepercentageview.ProfilePercentageView
import com.swapnilk.truelink.R
import com.swapnilk.truelink.data.online.AuthorizationInterceptor
import com.swapnilk.truelink.databinding.FragmentUpdateUserProfileBinding
import com.swapnilk.truelink.utils.CommonFunctions
import com.swapnilk.truelink.utils.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import kotlin.coroutines.CoroutineContext


class UpdateUserProfile : Fragment(), CoroutineScope {
    ////////////Start Coroutine for Background Task../////////////
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var _binding: FragmentUpdateUserProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var apolloClient: ApolloClient
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var commonFunctions: CommonFunctions

    private lateinit var textProgress: TextView
    private lateinit var ppvProfile: ProfilePercentageView
    private lateinit var editName: TextInputEditText
    private lateinit var editPhone: TextInputEditText
    private lateinit var editAddress: TextInputEditText
    private lateinit var editBirthday: TextInputEditText
    private lateinit var editGender: AutoCompleteTextView
    private lateinit var progressBar: ProgressBar
    private lateinit var viewF: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        showToolBar()

        viewF = inflater.inflate(R.layout.fragment_update_user_profile, container, false)

        Initialize()
        ////////////////////////////////GET shared Preferences///////////
        sharedPreferences = SharedPreferences(requireActivity())
        commonFunctions = CommonFunctions(requireContext())
        //////////////////////////////Get Apollo Client//////////////////
        try {
            ///////////////////////Initialize ApolloClient////////////////////////////
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthorizationInterceptor(requireContext().applicationContext))
                .build()
            apolloClient = ApolloClient.Builder().serverUrl("https://truelink.neki.dev/graphql/")
                .okHttpClient(okHttpClient).build()

        } catch (e: Exception) {
            e.stackTrace
        }

        ////////////////////////////////////////////////////////////////
        if (commonFunctions.checkConnection(requireContext()))
            getUserDetails()
        else
            commonFunctions.showErrorSnackBar(
                requireContext(),
                ppvProfile,
                getString(R.string.no_internet)
            )

        return viewF
    }

    private fun Initialize() {
        textProgress = viewF.findViewById(R.id.text_progress)
        ppvProfile = viewF.findViewById(R.id.ppvProfile)

        editAddress = viewF.findViewById(R.id.edit_address)
        editBirthday = viewF.findViewById(R.id.edit_dob)
        editName = viewF.findViewById(R.id.edit_name)
        editPhone = viewF.findViewById(R.id.edit_phone)
        progressBar = viewF.findViewById(R.id.progressBar)

        editGender = viewF.findViewById(R.id.edit_gender)

        val item = arrayOf(
            "Male", "Female", "Other"
        )

        editGender.setAdapter(
            ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                item
            )
        )
        //  editGender.addTextChangedListener(TextWatcher)

    }

    private fun getUserDetails() {
        try {
            launch {
                val response: ApolloResponse<GetUserQuery.Data> =
                    apolloClient.query(GetUserQuery()).execute()
                if (response != null) afterResponse(response)
            }
        } catch (e: Exception) {
            e.stackTrace
        } catch (e: NetworkOnMainThreadException) {
            e.stackTrace
        }

    }

    @SuppressLint("SetTextI18n")
    private fun afterResponse(response: ApolloResponse<GetUserQuery.Data>) {
        if (response.data != null) {
            progressBar.visibility = View.GONE
            val address = response.data!!.getUser.payload?.address.toString()
            val city = response.data!!.getUser.payload?.city.toString()
            val state = response.data!!.getUser.payload?.state.toString()
            val country = response.data!!.getUser.payload?.country.toString()
            var strAddress = ""
            if (!TextUtils.isEmpty(address) && address != "null")
                strAddress += address
            if (!TextUtils.isEmpty(city) && city != "null")
                strAddress = "$strAddress, $city"
            if (!TextUtils.isEmpty(state) && state != "null")
                strAddress = "$strAddress, $state"
            if (!TextUtils.isEmpty(country) && country != "null")
                strAddress = "$strAddress, $country"
            editAddress.setText(
                strAddress
            )
            editName.setText(response.data!!.getUser.payload?.fullname.toString())

            editPhone.setText(
                response.data!!.getUser.payload?.dialcode.toString()
                        + " " + response.data!!.getUser.payload?.phone.toString()
            )
//            val gender = response.data!!.getUser.payload.gender
//            editGender.setText()
            editBirthday.setText(commonFunctions.convertTimeStamp2Date(response.data!!.getUser.payload?.dob.toString()))
        }
    }

    private fun showToolBar() {
        activity?.findViewById<AppBarLayout>(R.id.appBarLayout)?.visibility = View.GONE
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE

        val btnHome: TextView = viewF.findViewById(R.id.btn_home_up)
        val btnEdit: TextView = viewF.findViewById(R.id.btn_edit)

        btnHome.setOnClickListener {
            activity?.onBackPressed()
        }

        btnEdit.setOnClickListener {
            editGender.isEnabled = true
            editBirthday.isEnabled = true
            editAddress.isEnabled = true
            editName.isEnabled = true
            btnEdit.setText(R.string.save)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
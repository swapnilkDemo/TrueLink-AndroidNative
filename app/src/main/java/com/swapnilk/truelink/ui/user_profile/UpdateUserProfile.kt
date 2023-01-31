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
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.network.okHttpClient
import com.example.GetUserQuery
import com.example.UpdateUserMutation
import com.example.type.Gender
import com.example.type.UpdateUserInput
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.ozcanalasalvar.library.view.popup.DatePickerPopup
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
import java.text.SimpleDateFormat
import java.util.*
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

    private lateinit var btnEdit: TextView
    private lateinit var btnHome: TextView
    private lateinit var btnSave: TextView

    private lateinit var textProgress: TextView
    private lateinit var ppvProfile: ProfilePercentageView
    private lateinit var editName: TextInputEditText
    private lateinit var editPhone: TextInputEditText
    private lateinit var editAddress: TextInputEditText
    private lateinit var editBirthday: TextInputEditText
    private lateinit var textLayoutBirthday: TextInputLayout
    private lateinit var editGender: AutoCompleteTextView
    private lateinit var progressBar: ProgressBar
    private lateinit var datePickerPopup: DatePickerPopup
    private val calendar = Calendar.getInstance()
    var gender: Gender? = null
    var dob: String = ""
    var name: String = ""
    var address: String = ""
    var dateOfBirth: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateUserProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        showToolBar()

//        viewF = inflater.inflate(R.layout.fragment_update_user_profile, container, false)

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
        if (commonFunctions.checkConnection(requireContext())) getUserDetails()
        else commonFunctions.showErrorSnackBar(
            requireContext(), ppvProfile, getString(R.string.no_internet)
        )

        return root
    }

    private fun Initialize() {
        textProgress = binding.textProgress
        ppvProfile = binding.ppvProfile
        textLayoutBirthday = binding.textLayoutBirthday
        editAddress = binding.editAddress
        editBirthday = binding.editDob
        editName = binding.editName
        editPhone = binding.editPhone
        progressBar = binding.progressBar

        editGender = binding.editGender


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
            ppvProfile.setValue(25)
            val address = response.data!!.getUser.payload?.address.toString()
            val city = response.data!!.getUser.payload?.city.toString()
            val state = response.data!!.getUser.payload?.state.toString()
            val country = response.data!!.getUser.payload?.country.toString()
            var strAddress = ""
            if (!TextUtils.isEmpty(address) && address != "null") strAddress += address
            if (!TextUtils.isEmpty(city) && city != "null") strAddress = "$strAddress, $city"
            if (!TextUtils.isEmpty(state) && state != "null") strAddress = "$strAddress, $state"
            if (!TextUtils.isEmpty(country) && country != "null") strAddress =
                "$strAddress, $country"
            editAddress.setText(
                strAddress
            )
            editName.setText(response.data!!.getUser.payload?.fullname.toString())

            editPhone.setText(
                response.data!!.getUser.payload?.dialcode.toString() + " " + response.data!!.getUser.payload?.phone.toString()
            )
            val gender = response.data!!.getUser.payload?.gender as Gender
            editGender.setText(gender.rawValue.toString())
            editBirthday.setText(commonFunctions.convertTimeStamp2Date(response.data!!.getUser.payload?.dob.toString()))
        }
    }

    private fun showToolBar() {
        activity?.findViewById<AppBarLayout>(R.id.appBarLayout)?.visibility = View.GONE
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE

        btnHome = binding.toolbarProfile.btnHomeUp
        btnEdit = binding.toolbarProfile.btnEdit
        btnSave = binding.toolbarProfile.btnSave
        btnHome.setOnClickListener {
            activity?.onBackPressed()
        }

        btnEdit.setOnClickListener {
            editGender.isEnabled = true
            editBirthday.isEnabled = true
            editAddress.isEnabled = true
            editName.isEnabled = true

            val item = arrayOf(
                "Male", "Female", "Other"
            )

            editGender.setAdapter(
                ArrayAdapter<String>(
                    requireContext(), android.R.layout.simple_spinner_dropdown_item, item
                )
            )
            editGender.setOnClickListener {
                editGender.showDropDown()
            }

            datePickerPopup = commonFunctions.createDatePickerDialog(requireContext())
            textLayoutBirthday.setOnClickListener {
                datePickerPopup.show()
            }
            datePickerPopup?.setListener(DatePickerPopup.OnDateSelectListener { dp, date, day, month, year ->
                val monthName: Int = (month + 1)
                calendar.set(Calendar.MONTH, month)
                val sdf = SimpleDateFormat("MMM")
                var monthStr = sdf.format(calendar.time)
                editBirthday.setText("$monthStr, $day   $year")
                dateOfBirth = commonFunctions.convertDate2TimeStamp("$monthName/$day/$year")
            })

            btnEdit.visibility = View.GONE
            btnSave.visibility = View.VISIBLE


            btnSave.setOnClickListener {
                name = editName.text.toString()
                dateOfBirth = commonFunctions.convertDate2TimeStamp(editBirthday.text.toString())
                address = editAddress.text.toString()
                gender = if (editGender.text.toString() == "Male")
                    Gender.MALE
                else if (editGender.text.toString() == "Female")
                    Gender.FEMALE
                else
                    Gender.OTHERS

                ////////////////Add required parameters//////////////////
                val updateUserInput = UpdateUserInput(
                    Optional.Present(editName.text.toString()),
                    Optional.Absent,
                    Optional.Absent,
                    Optional.Present(address),
                    Optional.Absent,
                    Optional.Absent,
                    Optional.Absent,
                    Optional.Absent,
                    Optional.Absent,
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
                        progressBar.visibility = View.VISIBLE
                        val response: ApolloResponse<UpdateUserMutation.Data> =
                            apolloClient.mutation(updateUserMutation).execute()
                        afterResult(response)
                    }
                } catch (e: Exception) {
                    e.stackTrace
                } catch (e: NetworkOnMainThreadException) {
                    e.stackTrace
                }//////////End try
            }//////////////End Save OnClick
        }/////////////////End Edit OnClick
    }///////////////End Of Function

    private fun afterResult(response: ApolloResponse<UpdateUserMutation.Data>) {
        progressBar.visibility = View.GONE
        btnSave.visibility = View.GONE
        btnEdit.visibility = View.VISIBLE
        editGender.isEnabled = false
        editBirthday.isEnabled = false
        editAddress.isEnabled = false
        editName.isEnabled = false
        commonFunctions.showErrorSnackBar(
            requireContext(),
            progressBar,
            response.data?.updateUser?.message.toString()
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
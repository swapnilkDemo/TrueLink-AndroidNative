package com.swapnilk.truelink.ui.user_profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.swapnilk.truelink.data.online.ApolloHelper
import com.swapnilk.truelink.databinding.FragmentUpdateUserProfileBinding
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

    private lateinit var textProgress: TextView
    private lateinit var ppvProfile: ProfilePercentageView
    private lateinit var editId: TextInputEditText
    private lateinit var editName: TextInputEditText
    private lateinit var editPhone: TextInputEditText
    private lateinit var editAddress: TextInputEditText
    private lateinit var editBirthday: TextInputEditText
    private lateinit var progressBar: ProgressBar
    private lateinit var viewF: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        showToolBar()

        viewF = inflater.inflate(R.layout.fragment_update_user_profile, container, false)

        Initialize()
        //////////////////////////////Get Apollo Client//////////////////
        try {
            val okHttpClient =
                OkHttpClient.Builder().addInterceptor(
                    ApolloHelper.AuthorizationInterceptor(
                        requireActivity()
                    )
                )
                    .build()

            apolloClient = ApolloClient.Builder().serverUrl(ApolloHelper.SERVER_URL)
                .okHttpClient(okHttpClient).build()
        } catch (e: Exception) {
            e.stackTrace
        }

        ////////////////////////////////////////////////////////////////
        getUserDetails()

        return viewF
    }

    private fun Initialize() {
        textProgress = viewF.findViewById(R.id.text_progress)
        ppvProfile = viewF.findViewById(R.id.ppvProfile)

        editAddress = viewF.findViewById(R.id.edit_address)
        editBirthday = viewF.findViewById(R.id.edit_dob)
        editId = viewF.findViewById(R.id.edit_id)
        editName = viewF.findViewById(R.id.edit_name)
        editPhone = viewF.findViewById(R.id.edit_phone)
        progressBar = viewF.findViewById(R.id.progressBar)
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
        }

    }

    private fun afterResponse(response: ApolloResponse<GetUserQuery.Data>) {
        if (response.data != null) {
            progressBar.visibility = View.GONE
            editName.setText(response.data!!.getUser.payload?.fullname.toString())
        }
    }

    private fun showToolBar() {
        activity?.findViewById<AppBarLayout>(R.id.appBarLayout)?.visibility = View.GONE
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE

        /* val btnHome: TextView = binding.toolbarProfile.btnHomeUp
         val btnEdit: TextView = binding.toolbarProfile.btnEdit

         btnHome.setOnClickListener {
             activity?.onBackPressed()
         }*/
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
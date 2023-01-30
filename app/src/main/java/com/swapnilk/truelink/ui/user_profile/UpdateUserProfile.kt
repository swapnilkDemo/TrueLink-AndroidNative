package com.swapnilk.truelink.ui.user_profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ss.profilepercentageview.ProfilePercentageView
import com.swapnilk.truelink.R
import com.swapnilk.truelink.databinding.FragmentUpdateUserProfileBinding


class UpdateUserProfile : Fragment() {
    private var _binding: FragmentUpdateUserProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val updateProfileViewModel = ViewModelProvider(this)[UpdateProfileViewModel::class.java]
        showToolBar()

        _binding = FragmentUpdateUserProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textProgress
        val ppvProfile: ProfilePercentageView = binding.ppvProfile
        updateProfileViewModel.text.observe(viewLifecycleOwner) {
            textView.text = "50%"
            ppvProfile.setValue(50)
        }

        return root
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


}
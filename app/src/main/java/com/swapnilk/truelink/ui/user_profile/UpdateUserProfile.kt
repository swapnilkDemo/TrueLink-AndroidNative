package com.swapnilk.truelink.ui.user_profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.swapnilk.truelink.R
import com.swapnilk.truelink.databinding.FragmentUpdateUserProfileBinding


class UpdateUserProfile : Fragment() {
    private var _binding: FragmentUpdateUserProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val updateProfileViewModel = ViewModelProvider(this)[UpdateProfileViewModel::class.java]

        _binding = FragmentUpdateUserProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textName
        updateProfileViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        return inflater.inflate(R.layout.fragment_update_user_profile, container, false)
    }


}
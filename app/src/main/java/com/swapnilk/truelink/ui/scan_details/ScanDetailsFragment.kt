package com.swapnilk.truelink.ui.scan_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.swapnilk.truelink.R
import com.swapnilk.truelink.databinding.FragmentScanDetailsBinding

class ScanDetailsFragment : Fragment() {
    companion object {
        fun newInstance() = ScanDetailsFragment()
    }

    private lateinit var viewModel: ScanDetailsViewModel
    private var _binding: FragmentScanDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var btnShare: TextView
    private lateinit var btnHome: TextView
    private lateinit var tvHeader: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentScanDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        showToolBar()

        return root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ScanDetailsViewModel::class.java)
        // TODO: Use the ViewModel
    }

    //////////////////////////Set Toolbar for User Profile//////////////////////////////
    private fun showToolBar() {
        activity?.findViewById<AppBarLayout>(R.id.appBarLayout)?.visibility = View.GONE
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE

        btnHome = binding.toolbarScanDetails.btnHomeUp
        btnShare = binding.toolbarScanDetails.btnShare
        btnHome.setOnClickListener {
            activity?.onBackPressed()
        }

    }///////////////End Of Function

}
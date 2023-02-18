package com.swapnilk.truelink.ui.scan_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.swapnilk.truelink.databinding.FragmentScanDetailsBinding

class ScanDetailsFragment : Fragment() {
    companion object {
        fun newInstance() = ScanDetailsFragment()
    }

    private lateinit var viewModel: ScanDetailsViewModel
    private var _binding: FragmentScanDetailsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentScanDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        showToolBar()

        return root
    }

    private fun showToolBar() {
        TODO("Not yet implemented")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ScanDetailsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
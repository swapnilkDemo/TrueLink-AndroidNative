package com.swapnilk.truelink.ui.home

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.swapnilk.truelink.R
import com.swapnilk.truelink.data.online.adapters.RecentScansAdapter
import com.swapnilk.truelink.data.online.adapters.TopAppDataAdapter
import com.swapnilk.truelink.data.online.model.AppDataModel
import com.swapnilk.truelink.data.online.model.RecentScansModel
import com.swapnilk.truelink.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var tabGraph: TabLayout


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        bindViews()
        /* val textView: TextView = binding.textHome
         homeViewModel.text.observe(viewLifecycleOwner) {
             textView.text = it
         }*/

        loadTopAppList()
        loadTabs()
        loadRecentScans()
        return root
    }


    private fun bindViews() {
        tabGraph = binding.tabGraph

    }

    private fun loadTopAppList() {
        binding.rvApps.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = TopAppDataAdapter(createAppList(), requireContext())

        }

    }

    private fun loadTabs() {
        tabGraph.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabLayout =
                    (tabGraph.getChildAt(0) as ViewGroup).getChildAt(tab.position) as LinearLayout
                val tabTextView = tabLayout.getChildAt(1) as TextView
                tabTextView.setTypeface(tabTextView.typeface, Typeface.BOLD)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val tabLayout =
                    (tabGraph.getChildAt(0) as ViewGroup).getChildAt(tab.position) as LinearLayout
                val tabTextView = tabLayout.getChildAt(1) as TextView
                tabTextView.setTypeface(null, Typeface.NORMAL)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun loadRecentScans() {
        binding.rvRecentScan.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = RecentScansAdapter(createRecentScantList(), requireContext())
        }

    }

    private fun createRecentScantList(): ArrayList<RecentScansModel> {
        var scanList = ArrayList<RecentScansModel>()
        scanList.add(
            RecentScansModel(
                true,
                R.drawable.ic_app_link,
                "amazon.com",
                "http:///phishinguelsample/sddsvsdvsdvsdvs",
                "12 : 19 PM",
                "Whatsapp",
                R.drawable.ic_app_link,
                false,
                true,
                "89 spam reports"

            )
        )

        scanList.add(
            RecentScansModel(
                true,
                R.drawable.ic_app_link,
                "amazon.com",
                "http:///phishinguelsample/sddsvsdvsdvsdvs",
                "12 : 19 PM",
                "Whatsapp",
                R.drawable.ic_app_link,
                false,
                true,
                "89 spam reports"

            )
        )

        scanList.add(
            RecentScansModel(
                false,
                R.drawable.ic_app_link,
                "amazon.com",
                "http:///phishinguelsample/sddsvsdvsdvsdvs",
                "12 : 19 PM",
                "Whatsapp",
                R.drawable.ic_app_link,
                true,
                false,
                "89 spam reports"

            )
        )

        scanList.add(
            RecentScansModel(
                true,
                R.drawable.ic_app_link,
                "amazon.com",
                "http:///phishinguelsample/sddsvsdvsdvsdvs",
                "12 : 19 PM",
                "Whatsapp",
                R.drawable.ic_app_link,
                false,
                true,
                "89 spam reports"

            )
        )
        return scanList
    }

    private fun createAppList(): ArrayList<AppDataModel> {
        var appList = ArrayList<AppDataModel>()
        appList.add(
            AppDataModel(
                R.drawable.ic_app_link,
                144,
                "Overall",
                R.color.bottom_nav_color,
                false
            )
        )

        appList.add(
            AppDataModel(
                R.drawable.ic_safe_link,
                100,
                "whatsapp",
                R.color.orange_text,
                false
            )
        )

        appList.add(
            AppDataModel(
                R.drawable.ic_suspicious,
                40,
                "Brave",
                R.color.selected_color,
                false
            )
        )

        appList.add(
            AppDataModel(
                R.drawable.ic_verified_link,
                44,
                "Telegram",
                R.color.selected_color,
                false
            )
        )
        appList.add(
            AppDataModel(
                R.drawable.ic_verified_link,
                44,
                "Telegram",
                R.color.selected_color,
                false
            )
        )
        return appList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
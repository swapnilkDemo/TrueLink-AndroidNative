package com.swapnilk.truelink.ui.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.swapnilk.truelink.MainActivity
import com.swapnilk.truelink.R
import com.swapnilk.truelink.data.online.adapters.RecentScansAdapter
import com.swapnilk.truelink.data.online.adapters.TopAppDataAdapter
import com.swapnilk.truelink.data.online.model.AppDataModel
import com.swapnilk.truelink.data.online.model.RecentScansModel
import com.swapnilk.truelink.databinding.FragmentHomeBinding
import com.swapnilk.truelink.utils.CommonFunctions
import pl.droidsonroids.gif.GifImageView


class HomeFragment : Fragment() {
    companion object HomeGlobal {


    }

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var tabGraph: TabLayout
    lateinit var seekBar: SeekBar
    lateinit var imageViewSettings: ImageView
    lateinit var imageViewSocial: ImageView
    lateinit var imageViewBrowser: ImageView
    lateinit var cvPermissions: MaterialCardView
    private lateinit var commonFunctions: CommonFunctions
    var progress: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
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
        commonFunctions = CommonFunctions(requireContext())
        if (!commonFunctions.isDefaultBrowser(requireContext()))
            showPopupWindow(
                requireContext(),
                "",
                "",
                "",
                "",
                0,
                "browser"
            )
        else {
            progress = 33
            if (Settings.canDrawOverlays(requireContext()))
                progress = 66
            updateSeekbarProgress(requireContext())
        }
        return root
    }


    private fun bindViews() {
        tabGraph = binding.tabGraph
        seekBar = binding.seekBar
//        seekBar.thumb.mutate().alpha = 255
        seekBar.isEnabled = false
        imageViewSettings = binding.ivSettings
        imageViewSocial = binding.ivSocial
        imageViewBrowser = binding.ivBrowser
        cvPermissions = binding.cvPermissions
    }

    private fun loadTopAppList() {
        binding.rvApps.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = TopAppDataAdapter(createAppList(), requireContext())

        }

    }

    private fun updateSeekbarProgress(context: Context) {
        seekBar.progress = progress
        if (progress <= 33) {
            imageViewSettings.setColorFilter(
                ContextCompat.getColor(context, R.color.green),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )

        } else if (progress <= 66) {
            imageViewSocial.setColorFilter(
                ContextCompat.getColor(context, R.color.green),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            imageViewSettings.setColorFilter(
                ContextCompat.getColor(context, R.color.green),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
        } else {
            cvPermissions.visibility = View.GONE
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

    /////////////////////Common Popup window//////////////////
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.O)
    fun showPopupWindow(
        context: Context,
        title: String,
        infoText: String,
        infoText1: String,
        buttonText: String,
        iconRes: Int,
        action: String
    ) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_popup)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val rvAppList = dialog.findViewById(R.id.rv_app_list) as RecyclerView
        val gifLoader = dialog.findViewById(R.id.iv_gif) as GifImageView
        val icon = dialog.findViewById(R.id.iv_icon) as ImageView
        val body = dialog.findViewById(R.id.tv_title) as TextView
        val info = dialog.findViewById(R.id.tv_text1) as TextView
        val info1 = dialog.findViewById(R.id.tv_text2) as TextView
        val yesBtn = dialog.findViewById(R.id.id_btn_confirm) as Button
        if (!TextUtils.isEmpty(title))
            body.text = title
        if (!TextUtils.isEmpty(infoText))
            info.text = infoText
        if (!TextUtils.isEmpty(infoText1))
            info1.text = infoText
        if (!TextUtils.isEmpty(buttonText))
            yesBtn.text = buttonText
        if (iconRes != 0)
            icon.setImageDrawable(context.getDrawable(iconRes))

        if (action == "overlay") {
            info1.visibility = View.GONE
            gifLoader.visibility = View.GONE
            rvAppList.visibility = View.VISIBLE
        }
        yesBtn.setOnClickListener {
            dialog.dismiss()
            if (action == "overlay")
                MainActivity.checkOverlayPermission(context as MainActivity)
            else if (action == "notification")
                MainActivity.requestPermissions("")
            else if (action == "browser") {
                run {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        MainActivity.showLauncherSelection(context as MainActivity)
                    }

                }
            }
        }

        dialog.show()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        if (commonFunctions.checkConnection(requireContext())) {
            progress = 33
            if (Settings.canDrawOverlays(requireContext()))
                progress = 66
            else {
                showPopupWindow(
                    requireContext(),
                    getString(R.string.draw_over_apps),
                    getString(R.string.text3),
                    "",
                    getString(R.string.enable),
                    R.drawable.ic_read_ntif,
                    "overlay"
                )
            }
            updateSeekbarProgress(requireContext())
        } else if (!Settings.canDrawOverlays(context)) {
            showPopupWindow(
                requireContext(),
                getString(R.string.draw_over_apps),
                getString(R.string.text3),
                "",
                getString(R.string.enable),
                R.drawable.ic_read_ntif,
                "overlay"
            )
        } else {
            progress = 33
            updateSeekbarProgress(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
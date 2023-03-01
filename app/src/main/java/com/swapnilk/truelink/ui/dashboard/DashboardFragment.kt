package com.swapnilk.truelink.ui.dashboard

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.network.okHttpClient
import com.example.AppScanHistoryQuery
import com.example.RecentScansLatestQuery
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.swapnilk.truelink.MainActivity
import com.swapnilk.truelink.R
import com.swapnilk.truelink.data.online.AuthorizationInterceptor
import com.swapnilk.truelink.data.online.adapters.*
import com.swapnilk.truelink.data.online.model.AppDataModel
import com.swapnilk.truelink.data.online.model.RecentScansModel
import com.swapnilk.truelink.databinding.FragmentDashboardBinding
import com.swapnilk.truelink.ui.home.HomeViewModel
import com.swapnilk.truelink.utils.CommonFunctions
import com.swapnilk.truelink.utils.SharedPreferences
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import pl.droidsonroids.gif.GifImageView
import kotlin.coroutines.CoroutineContext


class DashboardFragment : Fragment(), CoroutineScope, DataChangedInterface {
    ////////////Start Coroutine for Background Task../////////////
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var tabGraph: TabLayout
    private lateinit var viewPager: ViewPager
    lateinit var seekBar: SeekBar
    lateinit var imageViewSettings: ImageView
    lateinit var imageViewSocial: ImageView
    lateinit var imageViewBrowser: ImageView
    lateinit var cvPermissions: MaterialCardView
    lateinit var tvRangeFilter: AutoCompleteTextView
    lateinit var tvSafeCount: TextView
    lateinit var tvSuspiciousCount: TextView
    lateinit var tvBrowserCount: TextView
    lateinit var tvAppLinkCount: TextView
    lateinit var tvTotalCount: TextView
    lateinit var tvClickedCount: TextView
    lateinit var tvVerifiedCount: TextView
    lateinit var rvSenderChip: RecyclerView
    lateinit var llSenders: LinearLayout
    lateinit var ivSenderFilter: ImageView
    lateinit var llOverall: LinearLayout
    lateinit var ivAppIcon: ImageView
    lateinit var ivTopAppFilter: ImageView
    lateinit var tvEmptyList: TextView
    lateinit var progressBar: ProgressBar

    private lateinit var commonFunctions: CommonFunctions
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var apolloClient: ApolloClient
    private var sendersList: ArrayList<AppScanHistoryQuery.Sender?> = ArrayList()
    private var appList = ArrayList<AppDataModel>()
    private var scanList = ArrayList<RecentScansModel>()

    var progress: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        commonFunctions = CommonFunctions(requireContext())
        sharedPreferences = SharedPreferences(requireContext())

        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        bindViews()
        /* val textView: TextView = binding.textHome
         homeViewModel.text.observe(viewLifecycleOwner) {
             textView.text = it
         }*/
        //////////////////Initialize listner////////////
        mListener = this
        ///////////////////////////////////////////////
        if (commonFunctions.checkConnection(requireContext())) {
            //////////////////////////////Get Apollo Client//////////////////
            try {
                ///////////////////////Initialize ApolloClient////////////////////////////
                val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(AuthorizationInterceptor(requireContext().applicationContext))
                    .build()
                apolloClient =
                    ApolloClient.Builder().serverUrl(commonFunctions.getServerUrl())
                        .okHttpClient(okHttpClient).build()

            } catch (e: Exception) {
                e.stackTrace
            }
            createAppList(dayFilter, "")
            //createRecentScantList(dayFilter, "", null)
        } else
            commonFunctions.showErrorSnackBar(
                requireContext(),
                rvSenderChip,
                getString(R.string.no_internet),
                true
            )
        setDateRangeFilter()

        ivSenderFilter.setOnClickListener {
            var strPackageName: String = ""
            if (appList.size > 0)
                strPackageName = appList[selectedItem].packageName.toString()
            setSendersFilter(strPackageName)

        }

        ivTopAppFilter.setOnClickListener {
            setTopAppsFilter()
        }
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

    /////////////////////////Show App Filter Screen////////////////////////////////
    private fun setTopAppsFilter() {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        dialog.setCancelable(false)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        dialog.setContentView(view)
        dialog.show()
        val linearLayout: LinearLayout? =
            dialog.findViewById<LinearLayout>(R.id.ll_child)
        val behavior = linearLayout?.let { BottomSheetBehavior.from(it) }
        if (behavior != null) {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            behavior.peekHeight = BottomSheetBehavior.SAVE_ALL
        }
        val cvBottom = view.findViewById<LinearLayout>(R.id.cv_bottom)
        cvBottom.visibility = View.GONE
        val btnClose = view.findViewById<TextView>(R.id.txt_dialog_header)
        val tvDialogTitle = view.findViewById<TextView>(R.id.txt_dialog_header)
        tvDialogTitle.text = getString(R.string.top_apps)
        var rvTopApps: RecyclerView = view.findViewById(R.id.rv_senders)
        var searchView: SearchView = view.findViewById(R.id.search_view)
        searchView.queryHint = getString(R.string.search_apps)
        rvTopApps.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            var dividerItemDecoration = DividerItemDecoration(
                context,
                (layoutManager as LinearLayoutManager).orientation
            )
            addItemDecoration(dividerItemDecoration)
            adapter = AppDataAdapterList(
                appList,
                requireContext(),
                dialog
            )
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }
    }

    ///////////////////////////////Show Senders Filter screen ////////////////////////
    private fun setSendersFilter(strPackageName: String) {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        dialog.setCancelable(false)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        dialog.setContentView(view)
        dialog.show()
        val linearLayout: LinearLayout? =
            dialog.findViewById<LinearLayout>(R.id.ll_child)
        val behavior = linearLayout?.let { BottomSheetBehavior.from(it) }
        if (behavior != null) {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            behavior.peekHeight = BottomSheetBehavior.SAVE_ALL
        }
        val btnClose = view.findViewById<TextView>(R.id.txt_dialog_header)
        var senderIcon = view.findViewById<CircleImageView>(R.id.iv_sender_icon)

        var rvSendersList: RecyclerView = view.findViewById(R.id.rv_senders)

        rvSendersList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            var dividerItemDecoration = DividerItemDecoration(
                context,
                (layoutManager as LinearLayoutManager).orientation
            )
            addItemDecoration(dividerItemDecoration)
            adapter = SenderDataAdapterList(
                sendersList,
                requireContext(),
                ""
            )
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        var btnClearFilter: MaterialButton = view.findViewById(R.id.btn_clear_filter)
        var btnApplyFilter: MaterialButton = view.findViewById(R.id.btn_apply_filter)

        btnClearFilter.setOnClickListener {
            selectedItemsList.clear()
            rvSendersList.adapter?.notifyDataSetChanged()
        }

        btnApplyFilter.setOnClickListener {
            dialog.dismiss()
            mListener.onSenderSelected(
                selectedItemsList,
                packageName
            )
        }
    }

    //////////////////////////Show DaY Filters//////////////////////////
    private fun setDateRangeFilter() {
        var filterArray = resources.getStringArray(R.array.filter)
        tvRangeFilter.setAdapter(
            ArrayAdapter<String>(
                requireContext(), android.R.layout.simple_spinner_dropdown_item, filterArray
            )
        )

        tvRangeFilter.setOnTouchListener(object : View.OnTouchListener {
            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                tvRangeFilter.showDropDown()

                return v?.onTouchEvent(event) ?: true
            }
        })
        tvRangeFilter.onItemClickListener =
            (AdapterView.OnItemClickListener { parent, view, position, id ->
                when (position) {
                    0 -> dayFilter = 30
                    1 -> dayFilter = 60
                    2 -> dayFilter = 90
                }
                var packageName: String = ""
                if (appList.size > 0)
                    packageName = appList[selectedItem].packageName.toString()
                createAppList(dayFilter, packageName)
                //createRecentScantList(dayFilter, "", null)
            })
        // tvFilter.setText(tvFilter.adapter.getItem(0).toString())
    }


    private fun bindViews() {
        tabGraph = binding.tabGraph
        viewPager = binding.viewPager
        seekBar = binding.seekBar
//        seekBar.thumb.mutate().alpha = 255
        seekBar.isEnabled = false
        imageViewSettings = binding.ivSettings
        imageViewSocial = binding.ivSocial
        imageViewBrowser = binding.ivBrowser
        cvPermissions = binding.cvPermissions
        tvRangeFilter = binding.tvFilter

        tvSafeCount = binding.tvSafeCount
        tvSuspiciousCount = binding.tvSuspiciousCount
        tvTotalCount = binding.tvTotalCount
        tvClickedCount = binding.tvClickedCount
        tvVerifiedCount = binding.tvVerifiedCount
        tvAppLinkCount = binding.tvAppLinkCount
        tvBrowserCount = binding.tvBrowserCount

        rvSenderChip = binding.rvSenders
        llSenders = binding.llSenders
        ivSenderFilter = binding.ivFilterSender
        llOverall = binding.llOverall
        ivAppIcon = binding.ivAppIcon
        ivTopAppFilter = binding.ivFilterTopApps

        progressBar = binding.progressBar
    }

    /////////////////////////////Set Top RecyclerView///////////////////
    private fun loadTopAppList(appList: ArrayList<AppDataModel>) {
        binding.rvApps.apply {
            if (selectedItem == -1)
                selectedItem = appList.size - 1
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
            adapter = TopAppDataAdapter(appList, requireContext(), selectedItem)
            scrollToPosition(selectedItem)
        }

    }

    /////////////////////////////Permission Progress//////////////////
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

    //////////////////////Load Graph/////////////////////////////
    private fun loadTabs(statistics: AppDataModel) {
        var adapter =
            ViewPagerAdapter(
                requireContext(),
                activity?.supportFragmentManager,
                tabGraph.tabCount,
                statistics
            )
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabGraph))
        viewPager.currentItem = 0
        tabGraph.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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
                if (tabTextView.text.equals(R.string.tab_all)) {

                }

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

    ///////////////////////////////Set Recent Scans RecyclerView////////////////////////
    private fun loadRecentScans(scanList: ArrayList<RecentScansModel>) {
        binding.rvRecentScan.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = RecentScansAdapter(scanList, requireContext(), parentFragmentManager)
            var dividerItemDecoration = DividerItemDecoration(
                context,
                (layoutManager as LinearLayoutManager).orientation
            )
            addItemDecoration(dividerItemDecoration)
            isNestedScrollingEnabled = false
            //swapAdapter(adapter, true)
        }

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun createRecentScantList(
        filterDay: Int?,
        packageName: String,
        sender: List<String?>?,
    ) {
        scanList.clear()
        val recentScans = RecentScansLatestQuery(
            0,
            1,
            if (sender != null && sender.isNotEmpty())
                Optional.present(sender)
            else Optional.Absent,
            Optional.present(packageName),
            Optional.present(filterDay),
            Optional.Absent

        )
        GlobalScope.launch(Dispatchers.Main) {
            try {
                progressBar.visibility = View.VISIBLE
                var faviconStr = ""
                val response: ApolloResponse<RecentScansLatestQuery.Data> =
                    apolloClient.query(recentScans).execute()
                for (i in response.data?.recentScans?.payload?.results!!) {
                    if (i?.favicon != null)
                        faviconStr = i.favicon
                    val resentScansModel = RecentScansModel(
                        i?.verified!!,
                        faviconStr,
                        i.domainName,
                        i.full_url!!,
                        commonFunctions.convertTimeStamp2Date(i?.createdAt!!.toString()),
                        i.appName,
                        i.appIcon,
                        i.reportSummary?.phishing,
                        i.reportSummary?.spam,
                        i.reportSummary?.malware,
                        i.reportSummary?.fradulent,
                        i.category,
                        i.https,
                        i.hash

                    )
                    scanList.add(resentScansModel)
                }
                loadRecentScans(scanList)
                if (scanList.size > 0) {
                    binding.tvEmptyView.visibility = View.GONE
                } else
                    binding.tvEmptyView.visibility = View.VISIBLE
            } catch (ex: ApolloException) {
                ex.stackTrace
            } catch (ex: java.lang.NullPointerException) {
                ex.stackTrace
                Log.d("Exception ", ex.message.toString())
            } finally {
                progressBar.visibility = View.GONE

            }
        }

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun createAppList(queryParam: Int?, packageName: String?): ArrayList<AppDataModel> {
        appList.clear()
        val appScanHistory = AppScanHistoryQuery(
            0,
            10,
            Optional.present(queryParam!!),
            0,
            10,
            Optional.present(packageName)
        )
        GlobalScope.launch(Dispatchers.Main) {
            try {
                progressBar.visibility = View.VISIBLE
                val response: ApolloResponse<AppScanHistoryQuery.Data> =
                    apolloClient.query(appScanHistory).execute()
                // for (i in response)
                println(response.data?.appScanHistory?.payload)
                for (i in response.data?.appScanHistory?.payload!!) {
                    var appScansModel = AppDataModel(
                        i?.icon,
                        i?.appName,
                        i?.overallScans?.totalLinks,
                        i?.overallScans?.safeLinks,
                        i?.overallScans?.clickedLinks,
                        i?.overallScans?.suspicousLinks,
                        i?.overallScans?.scannedFromNotifications,
                        i?.overallScans?.scannedWithinBrowser,
                        i?.overallScans?.verifiedLinks,
                        i?.packageName,
                        R.color.selected_color,
                        false,
                        i?.senders,
                        i?.statistics?.allScans,
                        i?.statistics?.safeScans,
                        i?.statistics?.verifiedScans,
                        i?.statistics?.suspiciousScans

                    )
                    appList.add(appScansModel)

                }
                loadTopAppList(appList)
            } catch (ex: ApolloException) {
                ex.stackTrace
            } catch (ex: NullPointerException) {
                ex.stackTrace
            } finally {
                progressBar.visibility = View.GONE
            }
        }
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
        } else if (action == "notification") {
            info1.visibility = View.GONE
            gifLoader.visibility = View.GONE
        }
        yesBtn.setOnClickListener {
            dialog.dismiss()
            if (action == "overlay")
                MainActivity.checkOverlayPermission(context as MainActivity)
            else if (action == "notification")
                MainActivity.allowNotificationAccess(context as MainActivity)
            else if (action == "browser") {
                run {
                    MainActivity.showLauncherSelection(context as MainActivity)

                }
            }
        }

        dialog.show()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        if (commonFunctions.isDefaultBrowser(requireContext())) {
            progress = 33
            if (Settings.canDrawOverlays(requireContext())) {
                progress = 66
                /* if (!sharedPreferences.isNLServiceRunning(requireContext())) {
                     showPopupWindow(
                         requireContext(),
                         getString(R.string.notification_access),
                         getString(R.string.text3),
                         "",
                         getString(R.string.allow),
                         R.drawable.ic_read_ntif,
                         "notification"
                     )
                 }*/
            } else {
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

    @SuppressLint("NotifyDataSetChanged")
    override fun onAppSelected(appScansModel: AppDataModel) {
        tvSafeCount.text = appScansModel.safeLinks.toString()
        tvSuspiciousCount.text = appScansModel.suspiciousLinks.toString()
        tvBrowserCount.text = appScansModel.scannedWithinBrowser.toString()
        tvAppLinkCount.text = appScansModel.scannedFromNotification.toString()
        tvTotalCount.text = appScansModel.totalLinks.toString()
        tvClickedCount.text = appScansModel.clickedLinks.toString()
        tvVerifiedCount.text = appScansModel.verifiedLinks.toString()
        sendersList.clear()
        sendersList = appScansModel.senders as ArrayList<AppScanHistoryQuery.Sender?>
        if (appScansModel.packageName != null) {
            packageName = appScansModel.packageName
            llOverall.visibility = View.GONE
            ivAppIcon.visibility = View.VISIBLE
            var appIcon = commonFunctions.getAppIconFromPackageName(
                appScansModel.packageName,
                requireContext()
            )
            if (appIcon != null)
                ivAppIcon.setImageDrawable(
                    appIcon
                )
            else
                ivAppIcon.setImageDrawable(context?.getDrawable(R.drawable.ic_no_photo))
        } else {
            llOverall.visibility = View.VISIBLE
            ivAppIcon.visibility = View.GONE
        }
        if (sendersList.size > 0) {
            rvSenderChip.apply {
                llSenders.visibility = View.VISIBLE
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = SenderDataAdapterChip(
                    appScansModel.senders,
                    requireContext(),
                    appScansModel.packageName
                )
                //   scrollToPosition(appList.size - 1)
            }
        } else {
            llSenders.visibility = View.GONE
        }
        if (commonFunctions.checkConnection(requireContext()))
            createRecentScantList(dayFilter, appScansModel.packageName.toString(), null)
        else
            commonFunctions.showErrorSnackBar(
                requireContext(),
                rvSenderChip,
                getString(R.string.no_internet),
                true
            )

        loadTabs(appScansModel)

    }

    override fun onSenderSelected(
        senderList: ArrayList<AppScanHistoryQuery.Sender>,
        packageName: String?
    ) {
        val senderStrList: MutableList<String> = ArrayList()
        for (i in selectedItemsList) {
            val strName = i.sender.toString()
            senderStrList.add(strName)
        }

        if (commonFunctions.checkConnection(requireContext()))
            createRecentScantList(
                dayFilter,
                packageName!!,
                senderStrList
            )
        else
            commonFunctions.showErrorSnackBar(
                requireContext(),
                rvSenderChip,
                getString(R.string.no_internet),
                true
            )
    }

    companion object GlobalProperties {
        lateinit var mListener: DataChangedInterface
        var selectedItem: Int = -1
        var dayFilter: Int = 30
        var selectedItemsList: ArrayList<AppScanHistoryQuery.Sender> = ArrayList()
        var packageName = ""
    }

    class yAxisValueFormatter : ValueFormatter(), IAxisValueFormatter {
        override fun getFormattedValue(value: Float): String {
            return "" + value.toInt()
        }
    }
}
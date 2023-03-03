package com.swapnilk.truelink.ui.scan_details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.network.okHttpClient
import com.example.ScanLinkMutation
import com.example.type.AppType
import com.example.type.ScanLinkInput
import com.example.type.ScanTriggerType
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.swapnilk.truelink.R
import com.swapnilk.truelink.data.online.AuthorizationInterceptor
import com.swapnilk.truelink.data.online.adapters.ReportListAdapter
import com.swapnilk.truelink.data.online.model.RecentScansModel
import com.swapnilk.truelink.databinding.FragmentScanDetailsBinding
import com.swapnilk.truelink.utils.CommonFunctions
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import kotlin.coroutines.CoroutineContext

class ScanDetailsFragment : Fragment(), CoroutineScope {
    companion object {
        fun newInstance(recentScansModel: RecentScansModel) = ScanDetailsFragment()
            .apply {
                thisRecentScanModel = recentScansModel
            }
    }

    ////////////Start Coroutine for Background Task../////////////
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var thisRecentScanModel: RecentScansModel
    private lateinit var viewModel: ScanDetailsViewModel
    private var _binding: FragmentScanDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var btnShare: TextView
    private lateinit var btnHome: TextView
    private lateinit var tvHeader: TextView
    private lateinit var toolbar: Toolbar
    lateinit var llParent: LinearLayout
    private lateinit var gifImageView: LottieAnimationView
    lateinit var tvDomainName: TextView
    lateinit var tvFullURL: TextView
    lateinit var ivSafe: ImageView
    lateinit var ivMedium: ImageView
    lateinit var ivHigh: ImageView
    lateinit var ivCritical: ImageView
    lateinit var llOpenInBrowser: LinearLayout
    lateinit var llSandbox: LinearLayout
    lateinit var llNotSpam: LinearLayout
    lateinit var llReport: LinearLayout
    lateinit var tvWeekScanCount: TextView
    lateinit var tvScannedOnApp: TextView
    lateinit var tvSentBy: TextView
    lateinit var rvReports: RecyclerView
    lateinit var tvReportsCount: TextView
    private lateinit var tvShortenLink: TextView
    lateinit var tvSiteDomain: TextView
    lateinit var tvScannedUrl: TextView
    lateinit var tvPageTitle: TextView
    lateinit var tvIP: TextView
    lateinit var tvServerLocation: TextView
    lateinit var tvHosting: TextView
    lateinit var tvHttps: TextView
    lateinit var tvRegistrar: TextView
    lateinit var tvSeoCore: TextView

    lateinit var btnDetailedReport: MaterialButton
    lateinit var chipTitle: Chip
    lateinit var chipHeading: Chip
    lateinit var chipDesc: Chip
    lateinit var chipAuthor: Chip
    lateinit var chipImage: Chip
    lateinit var chipCopyrights: Chip
    lateinit var chipAboutUs: Chip
    lateinit var chipContactUs: Chip
    lateinit var chipSSLL: Chip
    lateinit var chipSitemap: Chip
    lateinit var chipListing: Chip
    lateinit var chipCloud: Chip

    lateinit var cvCompanyDetails: MaterialCardView
    lateinit var btnViewSite: MaterialButton
    lateinit var ivCoverPhoto: ImageView
    lateinit var ivCompanyLogo: CircleImageView
    lateinit var tvCategory: TextView
    lateinit var tvRank: TextView
    lateinit var tvTotalScans: TextView
    lateinit var tvSeo: TextView
    lateinit var tvFounded: TextView
    lateinit var tvHeadquarters: TextView

    lateinit var llLinkedin: LinearLayout
    lateinit var llTwitter: LinearLayout
    lateinit var llInstagram: LinearLayout
    lateinit var llFacebook: LinearLayout
    lateinit var llYoutube: LinearLayout

    lateinit var rvSimilarCompanies: RecyclerView
    lateinit var rvOtherParams: RecyclerView
    lateinit var tvEmptyReports: TextView
    lateinit var progressBar: ProgressBar

    lateinit var commonFunctions: CommonFunctions
    lateinit var apolloClient: ApolloClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentScanDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        showToolBar()
        bindViews()

        ///////////////////////////////////////////////
        commonFunctions = CommonFunctions(requireContext())
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
            bindValuesToVies()
        } else
            commonFunctions.showErrorSnackBar(
                requireContext(),
                tvTotalScans,
                getString(R.string.no_internet),
                true
            )


        return root
    }

    private fun bindValuesToVies() {
        progressBar.visibility = View.VISIBLE
        val scanLinkInput = ScanLinkInput(
            thisRecentScanModel.actualUrl.toString(),
            ScanTriggerType.MANUAL,
            Optional.Absent,
            Optional.present(AppType.SOCIAL_MEDIA),
            Optional.Absent,
            Optional.Absent,
            Optional.Absent,
            Optional.Absent,
            Optional.Absent
        )
        val scanLink = ScanLinkMutation(
            scanLinkInput
        )
        launch {
            try {
                val response: ApolloResponse<ScanLinkMutation.Data> =
                    apolloClient.mutation(scanLink).execute()

                commonFunctions.showToast(requireContext(), response.data?.scanLink!!.message)
                handleResponse(response);

            } catch (ex: ApolloException) {
                ex.stackTrace
            } catch (ex: NullPointerException) {
                ex.stackTrace
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    private fun handleResponse(response: ApolloResponse<ScanLinkMutation.Data>) {
        val scan = response.data?.scanLink?.payload?.scan
        when (scan?.severity) {
            0 -> {
                toolbar.setBackgroundColor(resources.getColor(R.color.green))
                llParent.setBackgroundColor(resources.getColor(R.color.green))
                ivSafe.visibility = View.VISIBLE
            }
            1 -> {
                toolbar.setBackgroundColor(resources.getColor(R.color.yellow_text))
                llParent.setBackgroundColor(resources.getColor(R.color.yellow_text))
                gifImageView.setAnimation(R.raw.warning)
                ivMedium.visibility = View.VISIBLE
            }
            2 -> {
                toolbar.setBackgroundColor(resources.getColor(R.color.orange_text))
                llParent.setBackgroundColor(resources.getColor(R.color.orange_text))
                gifImageView.setAnimation(R.raw.warning)
                ivHigh.visibility = View.VISIBLE
            }
            else -> {
                toolbar.setBackgroundColor(resources.getColor(R.color.red))
                llParent.setBackgroundColor(resources.getColor(R.color.red))
                gifImageView.setAnimation(R.raw.stop)
                ivCritical.visibility = View.VISIBLE
            }
        }

        tvDomainName.text = scan?.link?.whois?.domainName
        if (scan?.quickScan?.TLSexpired!! || !scan.quickScan.usingTLS!!)
            tvDomainName.setCompoundDrawables(
                resources.getDrawable(R.drawable.ic_lock),
                null,
                null,
                null
            )
        tvFullURL.text = scan?.link?.full_url
        tvScannedOnApp.text = response.data?.scanLink?.payload?.app
        tvSentBy.text = response.data?.scanLink?.payload?.sender
        tvTotalScans.text =
            scan?.triggers.toString() + " " + getString(R.string.scans_in_seven_days)

        tvShortenLink.text = "NA"
        tvSiteDomain.text = scan?.link?.whois?.domainName
        tvScannedUrl.text = scan?.link?.full_url
        tvPageTitle.text = scan?.siteDetails?.title

        tvIP.text = scan?.link?.ipinfo?.ip
        tvServerLocation.text = scan?.link?.ipinfo?.country
        tvHosting.text = "NA"
        if (!scan?.quickScan?.TLSexpired || scan?.quickScan?.usingTLS == true)
            tvHttps.text = getString(R.string.secured)
        else
            tvHttps.text = getString(R.string.insecured)
        tvRegistrar.text = scan?.link?.whois?.registrar
        tvSeoCore.text = scan?.quickScan?.SEOScore.toString()

        var reportList: ArrayList<ScanLinkMutation.Report> = ArrayList()
        for (reports in scan?.link?.reports!!)
            reportList.add(reports!!)

        if (reportList.size > 0) {
            tvEmptyReports.visibility = View.GONE
            tvReportsCount.text =
                reportList.size.toString() + " " + getString(R.string.reports_count)
            rvReports.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = ReportListAdapter(reportList, requireContext(), parentFragmentManager)
                var dividerItemDecoration = DividerItemDecoration(
                    context,
                    (layoutManager as LinearLayoutManager).orientation
                )
                addItemDecoration(dividerItemDecoration)
                isNestedScrollingEnabled = false
            }
        } else {
            rvReports.visibility = View.GONE
        }
    }

    private fun bindViews() {
        progressBar = binding.content.progressBar
        llParent = binding.content.llParent
        gifImageView = binding.content.gifIvSpam
        tvDomainName = binding.content.tvDomainName
        tvFullURL = binding.content.tvUrl
        ivSafe = binding.content.ivSafe
        ivMedium = binding.content.ivMedium
        ivHigh = binding.content.ivHigh
        ivCritical = binding.content.ivCritical

        llOpenInBrowser = binding.content.llOpen
        llSandbox = binding.content.llSandbox
        llNotSpam = binding.content.llNotSpam
        llReport = binding.content.llReport

        tvWeekScanCount = binding.content.tvWeekCount
        tvScannedOnApp = binding.content.tvAppName
        tvSentBy = binding.content.tvSenderName

        rvReports = binding.content.rvReports
        tvReportsCount = binding.content.tvReportCount
        tvEmptyReports = binding.content.tvEmptyReports

        tvShortenLink = binding.content.tvShortenLink
        tvSiteDomain = binding.content.tvSiteDomain
        tvScannedUrl = binding.content.tvScannedUrl
        tvPageTitle = binding.content.tvPageTitle

        tvIP = binding.content.tvIp
        tvServerLocation = binding.content.tvLocation
        tvHosting = binding.content.tvHosting
        tvHttps = binding.content.tvConnection
        tvRegistrar = binding.content.tvRegistrar
        tvSeoCore = binding.content.tvSeoScore

        chipAboutUs = binding.content.chipAboutUs
        chipAuthor = binding.content.chipAuthor
        chipCloud = binding.content.chipCloudflare
        chipDesc = binding.content.chipDesc
        chipCopyrights = binding.content.chipCopy
        chipContactUs = binding.content.chipContactUs
        chipHeading = binding.content.chipHeading
        chipImage = binding.content.chipImage
        chipListing = binding.content.chipListing
        chipSSLL = binding.content.chipSsl
        chipSitemap = binding.content.chipSitemap
        chipTitle = binding.content.chipTitle

        rvOtherParams = binding.content.rvOtherParams

        cvCompanyDetails = binding.content.cvCompanyDetails
        btnViewSite = binding.content.btnVisitSite
        ivCoverPhoto = binding.content.ivCover
        ivCompanyLogo = binding.content.civLogo

        tvCategory = binding.content.tvCategory
        tvRank = binding.content.tvRank
        tvTotalScans = binding.content.tvTotalScans
        tvSeo = binding.content.tvSeo
        tvFounded = binding.content.tvFounded
        tvHeadquarters = binding.content.tvHeadquarters

        llLinkedin = binding.content.llLinkedin
        llTwitter = binding.content.llTwitter
        llInstagram = binding.content.llInstagram
        llFacebook = binding.content.llFacebook
        llYoutube = binding.content.llYoutube

        rvSimilarCompanies = binding.content.rSimilarCompanies
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

        toolbar = binding.toolbarScanDetails.toolbar
        btnHome = binding.toolbarScanDetails.btnHomeUp
        btnShare = binding.toolbarScanDetails.btnShare
        tvHeader = binding.toolbarScanDetails.tvHeader
        btnHome.setOnClickListener {
            activity?.onBackPressed()
        }

    }///////////////End Of Function

}
package com.truelink

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.view.marginTop
import com.airbnb.lottie.LottieAnimationView
import com.swapnilk.truelink.R

class Window(  // declaring required variables
    private val context: Context
) {
    private var domainText: TextView? = null
    private var totalScanText: TextView? = null
    private var fullUrlText: TextView? = null
    private var flagText: TextView? = null
    private var spamReportsText: TextView? = null
    private var banner: androidx.cardview.widget.CardView? = null
    private var safePolygon: ImageView? = null
    private var dangerPolygon: ImageView? = null
    private var warningPolygon: ImageView? = null
    private var lowRiskPolygon: ImageView? = null
    private var scanIcon: LottieAnimationView? = null
    private var originIPText: TextView? = null
    private var hostingText: TextView? = null
    private var locationText: TextView? = null
    private var moreInfo: Button? = null
    private var bottomCard: LinearLayout? = null
    private var tagIcon: ImageView? = null
    private var webIcon : ImageView? = null
    private var dotIcon : TextView? = null
    private var topLayout: LinearLayout? = null
    private var httpsIcon: ImageView? = null
    private var createdOnText: TextView? = null
    private val mView: View
    private var mParams: WindowManager.LayoutParams? = null
    private val mWindowManager: WindowManager
    private val layoutInflater: LayoutInflater

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // set the layout parameters of the window
            mParams = WindowManager.LayoutParams( // Shrink the window to wrap the content rather
                // than filling the screen
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,  // Display it on top of other application windows
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,  // Don't let it grab the input focus
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,  // Make the underlying application window visible
                // through any transparent parts
                PixelFormat.TRANSLUCENT
            )
        }
        // getting a LayoutInflater
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        // inflating the view with the custom layout we created
        mView = layoutInflater.inflate(R.layout.new_scan_results, null)
        // set onClickListener on the remove button, which removes
        // the view from the window
        mView.findViewById<View>(R.id.window_close).setOnClickListener { close() }
        mView.findViewById<View>(R.id.window_close_2).setOnClickListener { close() }
        // Define the position of the
        // window within the screen
        mParams!!.gravity = Gravity.CENTER
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        domainText = mView.findViewById<View>(R.id.domain_text) as TextView
        totalScanText = mView.findViewById<View>(R.id.total_scan_text) as TextView
        fullUrlText = mView.findViewById<View>(R.id.full_url_text) as TextView
        flagText = mView.findViewById<View>(R.id.tagged_as_text) as TextView
        spamReportsText = mView.findViewById<View>(R.id.reports_count_text) as TextView
        banner = mView.findViewById<View>(R.id.banner_card) as CardView
        safePolygon = mView.findViewById<View>(R.id.safe_polygon) as ImageView
        lowRiskPolygon = mView.findViewById<View>(R.id.low_risk_polygon) as ImageView
        warningPolygon = mView.findViewById<View>(R.id.warning_polygon) as ImageView
        dangerPolygon = mView.findViewById<View>(R.id.danger_polygon) as ImageView
        scanIcon = mView.findViewById<LottieAnimationView>(R.id.scan_icon) as LottieAnimationView
        originIPText = mView.findViewById<View>(R.id.origin_ip_text) as TextView
        hostingText = mView.findViewById<View>(R.id.hosting_text) as TextView
        moreInfo = mView.findViewById<Button>(R.id.more_info_btn) as Button
        bottomCard = mView.findViewById<View>(R.id.bottom_card) as LinearLayout
        tagIcon = mView.findViewById<View>(R.id.tag_icon) as ImageView
        dotIcon = mView.findViewById<View>(R.id.dot) as TextView
        topLayout = mView.findViewById<View>(R.id.topLayout) as LinearLayout
        createdOnText = mView.findViewById<View>(R.id.created_on_text) as TextView
        httpsIcon = mView.findViewById<View>(R.id.https_icon) as ImageView
        locationText = mView.findViewById<View>(R.id.location_text) as TextView

        bottomCard?.visibility = View.INVISIBLE
    }

//    take HashMap<String, String> as a parameter

    @SuppressLint("SetTextI18n")
    fun loading(full_url: String, fact: String) {
        scanIcon?.setAnimation(R.raw.progress)
        banner?.setCardBackgroundColor(context.resources.getColor(R.color.primary_color))
        domainText?.text = "Scanning..."
        if (full_url.length > 25) {
            val substring = full_url.substring(0, 25)
            fullUrlText!!.text = "${substring}..."
        }else {
            fullUrlText!!.text = full_url
        }
        totalScanText?.text = "Secured by truelink"
        flagText?.text = fact
        spamReportsText?.visibility = View.GONE
        tagIcon?.visibility = View.GONE
        webIcon?.visibility = View.GONE
        dotIcon?.visibility = View.GONE
        createdOnText?.visibility = View.GONE
//        topLayout margin top = 0 dp
        topLayout?.setPadding(0, 10, 10, 20)

        try {
            // check if the view is already
            // inflated or present in the window
            if (mView.windowToken == null) {
                if (mView.parent == null) {
                    mWindowManager.addView(mView, mParams)
                }
            }
        } catch (e: Exception) {
            Log.d("Error1", e.toString())
        }
    }

@SuppressLint("SetTextI18n")
fun open(
    full_url: String?,
    domain: String?,
    totalScans: Int?,
    flag: String?,
    spamReports: Int?,
    malwareReports: Int?,
    phishingReports: Int?,
    fakewebReports: Int?,
    totalReports: Int?,
    severityInt: Int?,
    originIp: String?,
    category: String?,
    createdOn: String?,
    connectionType: String?,
    location: String?,
    hosting: String?
) {
//        set title text
        domainText!!.text = domain
        totalScanText!!.text = "$category | Total Scans $totalScans"
        createdOnText!!.text = "Since $createdOn"

//        if connection Type is Yes then show https icon
        if (connectionType == "Yes") {
            httpsIcon!!.setImageResource(R.drawable.https_ico)
        } else {
            httpsIcon!!.setImageResource(R.drawable.http_ico)
        }

//        substring full_url if it is too long
        if (full_url?.length!! > 25) {
            val substring = full_url.substring(0, 25)
            fullUrlText!!.text = "${substring}..."
        }else {
            fullUrlText!!.text = full_url
        }

//        check if flag contains malware, phishing, fakeweb
        if (flag?.contains("MALWARE") == true) {
            flagText!!.text = "Malware"
            tagIcon!!.setImageResource(R.drawable.malwareicon)
            spamReportsText!!.text = "$malwareReports Malware Reports"
        } else if (flag?.contains("PHISHING") == true) {
            flagText!!.text = "PHISHING"
            tagIcon!!.setImageResource(R.drawable.phishing_icon)
            spamReportsText!!.text = "$phishingReports Phishing Reports"
        } else if (flag?.contains("FAKE SITE") == true) {
            flagText!!.text = "FAKE SITE"
            tagIcon!!.setImageResource(R.drawable.fakeicon)
            spamReportsText!!.text = "$fakewebReports Fake Web Reports"
        } else if (flag?.contains("SPAM") == true) {
            flagText!!.text = "SPAM"
            tagIcon!!.setImageResource(R.drawable.spam_icon)
            spamReportsText!!.text = "$spamReports Spam Reports"
        }else {
            flagText!!.visibility = View.GONE
            tagIcon!!.visibility = View.GONE
            dotIcon!!.visibility = View.GONE
            spamReportsText!!.text = "$totalReports Reports Found"
        }

        banner!!.setCardBackgroundColor(context.resources.getColor(R.color.safe))
        val color = context.resources.getColor(R.color.safe)

//        if severity === "safe" then set color to green
//       make all invisible
        safePolygon?.visibility = View.INVISIBLE
        lowRiskPolygon?.visibility = View.INVISIBLE
        warningPolygon?.visibility = View.INVISIBLE
        dangerPolygon?.visibility = View.INVISIBLE
        bottomCard?.visibility = View.VISIBLE
    if (severityInt == 0) {
            banner?.setCardBackgroundColor(context.resources.getColor(R.color.safe))
            safePolygon?.visibility = ImageView.VISIBLE
            scanIcon?.setAnimation(R.raw.shield_success)
        } else if (severityInt == 1) {
            banner?.setCardBackgroundColor(context.resources.getColor(R.color.themeBlue))
            lowRiskPolygon?.visibility = ImageView.VISIBLE
            scanIcon?.setAnimation(R.raw.question)
        } else if (severityInt == 2) {
            banner?.setCardBackgroundColor(context.resources.getColor(R.color.themeBlue))
            warningPolygon?.visibility = ImageView.VISIBLE
            scanIcon?.setAnimation(R.raw.angry_emoji)
        } else if (severityInt == 3) {
            banner?.setCardBackgroundColor(context.resources.getColor(R.color.danger))
            dangerPolygon?.visibility = ImageView.VISIBLE
            scanIcon?.setAnimation(R.raw.stop)
        }
        originIPText?.text = originIp
        locationText?.text = location
//    substring hosting if it is too long
        if (hosting?.length!! > 12) {
            val substring = hosting.substring(0, 12)
            hostingText!!.text = "${substring}..."
        }else {
            hostingText?.text = hosting
        }

        moreInfo?.setOnClickListener {
                try {
//                    convert full_url to base64 url safe
                    val base64Url = Base64.encodeToString(full_url.toByteArray(), Base64.URL_SAFE)
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("truelink://scan/$base64Url"))
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.setPackage("com.truelink")
                    context.startActivity(intent)
                    close()
                } catch (e: Exception) {
                    Log.e("Error", e.toString())
                    Toast.makeText(context, "Error opening link", Toast.LENGTH_SHORT).show()
                }
        }

        val openBrowser = mView.findViewById<Button>(R.id.open_in_browser) as Button
        openBrowser.setOnClickListener {
            try {

//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(full_url))
//            intent.setPackage("com.android.chrome")
//            context.startActivity(intent)
                Log.d("Window", "open in browser")
//            open in browser
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(full_url))
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.setPackage("com.android.chrome")
                context.startActivity(intent)
                close()
            }catch (
                e: Exception
            ){
                Log.d("Window", "open in browser error")
                e.printStackTrace()
                close()
            }
        }
        try {
            // check if the view is already
            // inflated or present in the window
            if (mView.windowToken == null) {
                if (mView.parent == null) {
                    mWindowManager.addView(mView, mParams)
                }
            }
        } catch (e: Exception) {
            Log.d("Error1", e.toString())
        }
    }

    fun close() {
        try {
            // remove the view from the window
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(mView)
            // invalidate the view
            mView.invalidate()
            // remove all views
            (mView.parent as ViewGroup).removeAllViews()
//            close activity ScanResultActivity if it is open via intent
            // the above steps are necessary when you are adding and removing
            // the view simultaneously, it might give some exceptions
        } catch (e: Exception) {
            Log.d("Error2", e.toString())
        }
    }
}
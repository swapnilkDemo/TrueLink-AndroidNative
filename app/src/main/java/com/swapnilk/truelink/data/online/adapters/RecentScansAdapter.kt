package com.swapnilk.truelink.data.online.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.swapnilk.truelink.MainActivity
import com.swapnilk.truelink.R
import com.swapnilk.truelink.data.online.model.RecentScansModel
import com.swapnilk.truelink.ui.scan_details.ScanDetailsFragment
import kotlin.math.max

class RecentScansAdapter(
    private val scanList: ArrayList<RecentScansModel>,
    val context: Context,
    val fm: FragmentManager
) :
    RecyclerView.Adapter<RecentScansAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.list_items_recent_scan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recentScansModel = scanList[position]
//        holder.civFavicon.setImageDrawable(context.getDrawable(recentScansModel.faviconUrl!!))
        Picasso.with(context)
            .load(recentScansModel.faviconUrl)
            .into(holder.civFavicon)
        holder.tvDomain.text = recentScansModel.domainName
        holder.tvTime.text = recentScansModel.time
        holder.tvUrl.text = recentScansModel.actualUrl

        if (recentScansModel.https != true) {
            holder.ivSafe.setImageDrawable(context.getDrawable(R.drawable.ic_lock))
        } else {
            holder.ivSafe.setImageDrawable(context.getDrawable(R.drawable.ic_unlock))
        }
        if (recentScansModel.isVerified != true) {
            holder.tvVerified.visibility = View.GONE
            holder.tvSocialMedia.visibility = View.GONE
            holder.tvPhishing.visibility = View.VISIBLE
            val max1: Int = max(recentScansModel.phishing!!, recentScansModel.malware!!)
            val max2: Int = max(recentScansModel.spam!!, recentScansModel.fradulent!!)
            val max3: Int = maxOf(max1, max2)
            if (max3 != null) {
                holder.tvSpamCount.visibility = View.VISIBLE
                if (max3 == recentScansModel.spam) {
                    holder.tvSpamCount.text = recentScansModel.spam.toString()
                    holder.tvPhishing.text = context.getText(R.string.spam)
                    holder.tvPhishing.background.setTint(
                        ContextCompat.getColor(
                            context,
                            R.color.orange_text
                        )
                    )
                } else if (max3 == recentScansModel.fradulent) {
                    holder.tvSpamCount.text = recentScansModel.fradulent.toString()
                    holder.tvPhishing.text = context.getText(R.string.fraudulent)
                    holder.tvPhishing.background.setTint(
                        ContextCompat.getColor(
                            context,
                            R.color.yellow_text
                        )
                    )
                } else if (max3 == recentScansModel.malware) {
                    holder.tvSpamCount.text = recentScansModel.malware.toString()
                    holder.tvPhishing.text = context.getText(R.string.malware)
                    holder.tvPhishing.background.setTint(
                        ContextCompat.getColor(
                            context,
                            R.color.red
                        )
                    )
                } else if (max3 == recentScansModel.phishing) {
                    holder.tvSpamCount.text = recentScansModel.phishing.toString()
                    holder.tvPhishing.text = context.getText(R.string.phishing)
                    holder.tvPhishing.background.setTint(
                        ContextCompat.getColor(
                            context,
                            R.color.purple
                        )
                    )

                }
            }
        } else {
            holder.tvVerified.visibility = View.VISIBLE
            holder.tvSocialMedia.visibility = View.VISIBLE
            holder.tvSocialMedia.text = recentScansModel.category
            holder.tvPhishing.visibility = View.GONE
        }


        holder.tvSource.text = recentScansModel.source
        Picasso.with(context)
            .load(recentScansModel.sourceIcon)
            .into(holder.ivSource)


        holder.itemView.setOnClickListener {
            val scanDetailsFragment: ScanDetailsFragment = ScanDetailsFragment()
            MainActivity.addFragmentToActivity(scanDetailsFragment, fm)
        }
    }

    override fun getItemCount(): Int {
        return scanList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var civFavicon: ImageView = itemView.findViewById(R.id.civ_favicon)
        var ivSafe: ImageView = itemView.findViewById(R.id.iv_safe)
        var tvDomain: TextView = itemView.findViewById(R.id.tv_domain_name)
        var tvVerified: TextView = itemView.findViewById(R.id.tv_verified)
        var tvTime: TextView = itemView.findViewById(R.id.tv_time)
        var tvUrl: TextView = itemView.findViewById(R.id.tv_url)
        var tvPhishing: TextView = itemView.findViewById(R.id.tv_phishing)
        var tvSocialMedia: TextView = itemView.findViewById(R.id.tv_social_media)
        var tvSpamCount: TextView = itemView.findViewById(R.id.tv_report_count)
        var tvSource: TextView = itemView.findViewById(R.id.tv_source)
        var ivSource: ImageView = itemView.findViewById(R.id.iv_app_icon)


    }

}

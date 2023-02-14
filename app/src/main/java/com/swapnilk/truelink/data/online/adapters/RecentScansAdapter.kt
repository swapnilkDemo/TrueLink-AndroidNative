package com.swapnilk.truelink.data.online.adapters

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.swapnilk.truelink.R
import com.swapnilk.truelink.data.online.model.RecentScansModel

class RecentScansAdapter(private val scanList: ArrayList<RecentScansModel>, val context: Context) :
    RecyclerView.Adapter<RecentScansAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.list_items_recent_scan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recentScansModel = scanList[position]
        holder.civFavicon.setImageDrawable(context.getDrawable(recentScansModel.faviconUrl))
        holder.tvDomain.text = recentScansModel.domainName
        holder.tvTime.text = recentScansModel.time
        holder.tvUrl.text = recentScansModel.actualUrl

        if (recentScansModel.isPhishing) {
            holder.tvPhishing.visibility = View.VISIBLE
            holder.ivSafe.setImageDrawable(context.getDrawable(R.drawable.ic_lock))
        } else {
            holder.tvPhishing.visibility = View.GONE
            holder.ivSafe.setImageDrawable(context.getDrawable(R.drawable.ic_unlock))
        }
        if (recentScansModel.isSocialMedia)
            holder.tvSocialMedia.visibility = View.VISIBLE
        else
            holder.tvSocialMedia.visibility = View.GONE
        if (!TextUtils.isEmpty(recentScansModel.reportCount)) {
            holder.tvSpamCount.text = recentScansModel.reportCount
            holder.tvSpamCount.visibility = View.VISIBLE
        }
        if (recentScansModel.isVerified)
            holder.tvVerified.visibility = View.VISIBLE
        else
            holder.tvVerified.visibility = View.GONE

        holder.tvSource.text = recentScansModel.source
        holder.tvSource.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            recentScansModel.sourceIcon,
            0
        )
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


    }

}

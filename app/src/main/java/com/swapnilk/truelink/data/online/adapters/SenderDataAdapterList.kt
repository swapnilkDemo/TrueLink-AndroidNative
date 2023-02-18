package com.swapnilk.truelink.data.online.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.AppScanHistoryQuery
import com.squareup.picasso.Picasso
import com.swapnilk.truelink.R
import com.swapnilk.truelink.ui.dashboard.DashboardFragment
import com.swapnilk.truelink.utils.CommonFunctions
import de.hdodenhof.circleimageview.CircleImageView

class SenderDataAdapterList(
    val senders: List<AppScanHistoryQuery.Sender?>,
    val context: Context,
    val packageName: String?
) :
    RecyclerView.Adapter<SenderDataAdapterList.ViewHolder>() {
    var commonFunctions: CommonFunctions = CommonFunctions(context)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivSenderIcon: CircleImageView = itemView.findViewById(R.id.iv_sender_icon)
        var tvSenderName: TextView = itemView.findViewById(R.id.tv_sender_name)
        var tvSafeLinks: TextView = itemView.findViewById(R.id.tv_safe_links)
        var tvSuspiciousLinks: TextView = itemView.findViewById(R.id.tv_suspicious_links)
        var tvTotalLinks: TextView = itemView.findViewById(R.id.tv_total_links)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.list_senders_item, parent, false)
        return SenderDataAdapterList.ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var sender: AppScanHistoryQuery.Sender = senders[position]!!
        if (sender.sender != null) {
            holder.tvSenderName.text = sender.sender
            Picasso.with(context)
                .load(sender.image)
                .into(holder.ivSenderIcon)

            holder.tvSuspiciousLinks.text =
                sender.suspiciousLinks.toString() + " " + context.getString(R.string.suspicious_link)
            holder.tvSafeLinks.text =
                sender.linksCount.toString() + " " + context.getString(R.string.safe_links_1)
            holder.tvTotalLinks.text = sender.linksCount.toString()
            holder.itemView.setOnClickListener {
                DashboardFragment.mListener.onSenderSelected(sender, packageName)
            }
        } else {
        }

    }

    override fun getItemCount(): Int {
        return senders.size
    }

}

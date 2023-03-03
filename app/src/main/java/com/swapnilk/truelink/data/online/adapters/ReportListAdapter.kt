package com.swapnilk.truelink.data.online.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ScanLinkMutation
import com.squareup.picasso.Picasso
import com.swapnilk.truelink.R
import com.swapnilk.truelink.utils.CommonFunctions
import de.hdodenhof.circleimageview.CircleImageView

class ReportListAdapter(
    private val reportList: ArrayList<ScanLinkMutation.Report>,
    val context: Context,
    parentFragmentManager: FragmentManager
) : RecyclerView.Adapter<ReportListAdapter.ViewHolder>() {
    var commonFunctions = CommonFunctions(context)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userAvatar: CircleImageView = itemView.findViewById(R.id.user_avatar)
        var userName: TextView = itemView.findViewById(R.id.tv_name)
        var userLocation: TextView = itemView.findViewById(R.id.tv_report_location)
        var flag: TextView = itemView.findViewById(R.id.tv_flag)
        var report: TextView = itemView.findViewById(R.id.tv_report)
        var createdAt: TextView = itemView.findViewById(R.id.tv_created_at)
        var url: TextView = itemView.findViewById(R.id.tv_url)
        var comment: TextView = itemView.findViewById(R.id.tv_comment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.list_link_reports, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val report: ScanLinkMutation.Report = reportList[position]
        if (report.user?.avatar != null)
            Picasso.with(context)
                .load(report.user?.avatar)
                .placeholder(R.drawable.ic_no_photo)
                .into(holder.userAvatar)
        holder.userName.text = report.user?.fullname
        holder.userLocation.text = report.user?.city + ", " + report.user?.country
        holder.report.text = report.flag
        holder.flag.text = report.flag
        holder.url.text = report.link
        holder.comment.text = report.comment!!
        holder.createdAt.text = commonFunctions.convertTimeStamp2Date(report.createdAt.toString()!!)
    }

    override fun getItemCount(): Int {
        return 1
    }

}

package com.swapnilk.truelink.data.online.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.swapnilk.truelink.R
import com.swapnilk.truelink.data.online.model.AppDataModel
import com.swapnilk.truelink.utils.CommonFunctions
import de.hdodenhof.circleimageview.CircleImageView

class AppDataAdapterList(
    val appList: ArrayList<AppDataModel>,
    val context: Context
) :
    RecyclerView.Adapter<AppDataAdapterList.ViewHolder>() {
    var commonFunctions: CommonFunctions = CommonFunctions(context)
    var selectedAppList: ArrayList<AppDataModel> = ArrayList()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivAppIcon: CircleImageView = itemView.findViewById(R.id.iv_sender_icon)
        var tvAppName: TextView = itemView.findViewById(R.id.tv_sender_name)
        var tvSafeLinks: TextView = itemView.findViewById(R.id.tv_safe_links)
        var tvSuspiciousLinks: TextView = itemView.findViewById(R.id.tv_suspicious_links)
        var tvTotalLinks: TextView = itemView.findViewById(R.id.tv_total_links)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.list_senders_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appDataModel = appList[position]
        if (appDataModel != null) {
            holder.tvAppName.text =
                commonFunctions.getAppNameFromPackageName(appDataModel.packageName, context)
            holder.tvSafeLinks.text = appDataModel.safeLinks.toString()
            holder.tvSuspiciousLinks.text = appDataModel.suspiciousLinks.toString()
            holder.tvTotalLinks.text = appDataModel.totalLinks.toString()

        }
    }

    override fun getItemCount(): Int {
        return appList.size
    }


}

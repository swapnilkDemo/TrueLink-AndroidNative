package com.swapnilk.truelink.data.online.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.swapnilk.truelink.R
import com.swapnilk.truelink.data.online.model.AppDataModel
import com.swapnilk.truelink.ui.dashboard.DashboardFragment
import com.swapnilk.truelink.utils.CommonFunctions
import de.hdodenhof.circleimageview.CircleImageView

class AppDataAdapterList(
    val appList: ArrayList<AppDataModel>,
    val context: Context,
    val dialog: BottomSheetDialog
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
        var ivArrowRight: ImageView = itemView.findViewById(R.id.iv_arrow_right)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.list_senders_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appDataModel = appList[position]
        if (appDataModel != null) {
            if (appDataModel.packageName != null) {
                holder.tvAppName.text =
                    commonFunctions.getAppNameFromPackageName(appDataModel.packageName, context)
                holder.ivAppIcon.setImageDrawable(
                    commonFunctions.getAppIconFromPackageName(
                        appDataModel.packageName,
                        context
                    )
                )
            } else {
                holder.tvAppName.text = context.getString(R.string.overall)
                holder.ivAppIcon.setImageDrawable(context.resources.getDrawable(R.drawable.ic_overall))
            }
            holder.tvSafeLinks.text = appDataModel.safeLinks.toString()
            holder.tvSuspiciousLinks.text = appDataModel.suspiciousLinks.toString()
            holder.tvTotalLinks.text = appDataModel.totalLinks.toString()
            holder.ivArrowRight.visibility = View.VISIBLE

            holder.itemView.setOnClickListener {
                DashboardFragment.mListener.onAppSelected(appDataModel)
                dialog.dismiss()
            }
        }
    }

    override fun getItemCount(): Int {
        return appList.size
    }


}

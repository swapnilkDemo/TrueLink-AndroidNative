package com.swapnilk.truelink.data.online.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.AppScanHistoryQuery
import com.google.android.material.chip.Chip
import com.swapnilk.truelink.R
import com.swapnilk.truelink.ui.dashboard.DashboardFragment
import com.swapnilk.truelink.ui.dashboard.DashboardFragment.GlobalProperties.selectedItemsList
import com.swapnilk.truelink.utils.CommonFunctions

class SenderDataAdapterChip(
    val senders: List<AppScanHistoryQuery.Sender?>,
    val context: Context,
    val packageName: String?
) :
    RecyclerView.Adapter<SenderDataAdapterChip.ViewHolder>() {
    var commonFunctions: CommonFunctions = CommonFunctions(context)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var chip: Chip = itemView.findViewById(R.id.user_chip)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.list_senders_chip, parent, false)
        return SenderDataAdapterChip.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var sender: AppScanHistoryQuery.Sender = senders[position]!!
        try {
            if (sender.sender != null) {
                holder.chip.text = sender.sender.toString()
                if (sender.image?.isNotEmpty() == true)
                    holder.chip.chipIcon = commonFunctions.drawableFromUrl(sender.image)

                if (selectedItemsList.contains(sender))
                    holder.chip.chipBackgroundColor =
                        ColorStateList.valueOf(context.getColor(R.color.secondary_color))
                else
                    holder.chip.chipBackgroundColor =
                        ColorStateList.valueOf(context.getColor(R.color.primary_color))

                holder.chip.setOnClickListener {
                    /*var senderList: MutableList<String> = ArrayList()
                    senderList.add(sender.sender.toString())*/
                    if (!DashboardFragment.selectedItemsList.contains(sender)){
                        selectedItemsList.add(sender)
                        holder.chip.chipBackgroundColor =
                            ColorStateList.valueOf(context.getColor(R.color.secondary_color))
                    }else{
                        selectedItemsList.remove(sender)
                        holder.chip.chipBackgroundColor =
                            ColorStateList.valueOf(context.getColor(R.color.primary_color))
                    }

                    DashboardFragment.mListener.onSenderSelected(selectedItemsList, packageName)
                }
            } else {
                holder.chip.visibility = View.GONE
            }
        } catch (ex: Exception) {
            ex.stackTrace
        }

    }

    override fun getItemCount(): Int {
        return senders.size
    }

}

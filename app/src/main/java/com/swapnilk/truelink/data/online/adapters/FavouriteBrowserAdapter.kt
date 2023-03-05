package com.swapnilk.truelink.data.online.adapters

import android.content.Context
import android.content.pm.ResolveInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.swapnilk.truelink.R
import com.swapnilk.truelink.ui.user_profile.UpdateUserProfile.Companion.favouriteBrowser


class FavouriteBrowserAdapter(
    private val allBrowsers: List<ResolveInfo>,
    val context: Context
) : RecyclerView.Adapter<FavouriteBrowserAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvAppName: TextView = itemView.findViewById(R.id.tv_app_name)
        var ivAppIcon: ImageView = itemView.findViewById(R.id.iv_app_icon)
        var radioButton: RadioButton = itemView.findViewById(R.id.rdo)
        val llParent: LinearLayout = itemView.findViewById(R.id.ll_parent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.list_browser_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var resolveInfo = allBrowsers[position]
        var packageManager = context.packageManager
        var icon = resolveInfo.loadIcon(context.packageManager)
        if (icon != null) {
            holder.ivAppIcon.setImageDrawable(icon)
        }
        if (favouriteBrowser.contains(resolveInfo.activityInfo.packageName)) {
            holder.llParent.setBackgroundColor(context.getColor(R.color.secondary_color))
            holder.radioButton.isChecked = true
        } else {
            holder.llParent.setBackgroundColor(context.getColor(R.color.light_background))
            holder.radioButton.isChecked = false
        }

        holder.tvAppName.text = resolveInfo.loadLabel(packageManager)

        holder.radioButton
            .setOnCheckedChangeListener { _, _ ->
                if (!favouriteBrowser.contains(resolveInfo.activityInfo.packageName))
                    favouriteBrowser.add(resolveInfo.activityInfo.packageName)
                else
                    favouriteBrowser.remove(resolveInfo.activityInfo.packageName)

                notifyDataSetChanged()
            }
    }

    override fun getItemCount(): Int {
        return allBrowsers.size
    }

}

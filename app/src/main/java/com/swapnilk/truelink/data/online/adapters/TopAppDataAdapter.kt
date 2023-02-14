package com.swapnilk.truelink.data.online.adapters

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.swapnilk.truelink.R
import com.swapnilk.truelink.data.online.model.AppDataModel


class TopAppDataAdapter(private val appList: ArrayList<AppDataModel>, val context: Context) :
    RecyclerView.Adapter<TopAppDataAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.list_items_circular, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appDataModel = appList[position]
        /* Picasso.with(context)
             .load(appDataModel.clickedLinks)
             .into(holder.ivAppIcon)*/
        holder.ivAppIcon.setImageDrawable(context.getDrawable(appDataModel.appIconUrl))
        if (appDataModel.appLinkCount < 99)
            holder.tvBadge.text = appDataModel.appLinkCount.toString()
        else
            holder.tvBadge.text = "99+"
        setTextViewDrawableColor(holder.tvBadge, appDataModel.bgColor)
        holder.tvAppName.text = appDataModel.appName
    }

    override fun getItemCount(): Int {
        return appList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivAppIcon: ImageView = itemView.findViewById(R.id.iv_app)
        var tvBadge: TextView = itemView.findViewById(R.id.tv_badge)
        var tvAppName: TextView = itemView.findViewById(R.id.tv_app_name)
    }

    private fun setTextViewDrawableColor(textView: TextView, color: Int) {
        var drawable = textView.background
        if (drawable != null) {
            drawable.colorFilter =
                PorterDuffColorFilter(
                    ContextCompat.getColor(textView.context, color),
                    PorterDuff.Mode.SRC_IN
                )
        }

    }
}
package com.swapnilk.truelink.data.online.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.swapnilk.truelink.R
import com.swapnilk.truelink.data.online.model.AppDataModel
import com.swapnilk.truelink.ui.dashboard.DashboardFragment
import com.swapnilk.truelink.utils.CommonFunctions


class TopAppDataAdapter(
    private val appList: ArrayList<AppDataModel>,
    val context: Context,
    var selectedItem: Int
) :
    RecyclerView.Adapter<TopAppDataAdapter.ViewHolder>() {
    private var commonFunctions = CommonFunctions(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.list_items_circular, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appDataModel = appList[position]
        if (!TextUtils.isEmpty(appDataModel.packageName)) {
            if (commonFunctions.getAppNameFromPackageName(
                    appDataModel.packageName,
                    context
                ) != "Unknown"
            ) {
                holder.tvAppName.text =
                    commonFunctions.getAppNameFromPackageName(appDataModel.packageName, context)
                holder.ivAppIcon.setImageDrawable(
                    commonFunctions.getAppIconFromPackageName(
                        appDataModel.packageName!!,
                        context
                    )
                )
            } else {
                holder.tvAppName.text = appDataModel.appName
                Picasso.with(context)
                    .load(appDataModel.appIconUrl)
                    .placeholder(R.drawable.ic_no_photo)
                    .into(holder.ivAppIcon)
            }

        } else {
            if (appDataModel.appName.equals("Overall")) {
                holder.tvAppName.text = context.getString(R.string.overall)
                holder.ivAppIcon.setImageDrawable(context.resources.getDrawable(R.drawable.ic_overall))
            } else {
                holder.tvAppName.text = "Unknown"

            }
        }

        if (appDataModel.totalLinks!! < 99)
            holder.tvBadge.text = appDataModel.totalLinks.toString()
        else
            holder.tvBadge.text = "99+"
        setTextViewDrawableColor(
            holder.tvBadge,
            appDataModel.bgColor!!
        )
        /* commonFunctions.getDominantColor(
                holder.ivAppIcon.drawable,
                context
            )*/

        if (selectedItem == position) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                holder.ivAppIcon.background =
                    context.resources.getDrawable(
                        R.drawable.background_circle_selected
                    )
            else
                holder.ivAppIcon.background =
                    ContextCompat.getDrawable(context, R.drawable.background_circle_selected)

            DashboardFragment.mListener.onAppSelected(appDataModel)
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                holder.ivAppIcon.setBackgroundDrawable(
                    ContextCompat.getDrawable(context, R.drawable.background_circle)
                )
            else
                holder.ivAppIcon.background =
                    ContextCompat.getDrawable(context, R.drawable.background_circle)
        }



        holder.itemView.setOnClickListener {
            val getPosition =
                position as Int // Here we get the position that we have set for the checkbox using setTag.

            for (i in 0 until appList.size) {
                if (getPosition == i) {
                    selectedItem = getPosition
                } else {
                }
            }
            notifyDataSetChanged()
        }
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
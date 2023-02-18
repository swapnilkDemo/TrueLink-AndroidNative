package com.swapnilk.truelink.ui.dashboard

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.swapnilk.truelink.databinding.FragmentBarChartBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BarChartFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentBarChartBinding? = null
    private val binding get() = _binding!!
    private lateinit var barchart: BarChart
    private lateinit var data: BarData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBarChartBinding.inflate(inflater, container, false)
        val root: View = binding.root
        bindViews()
        loadGraph()
        return root
    }

    private fun bindViews() {
        barchart = binding.barchart
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BarChartFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    @SuppressLint("ResourceAsColor")
    private fun loadGraph() {
        val xAxisList: ArrayList<String> = ArrayList()
        val yAxisList: ArrayList<String> = ArrayList()

        xAxisList.add("SocialMedia")
        xAxisList.add("News")
        xAxisList.add("E-Commerce")
        xAxisList.add("Technology")
        xAxisList.add("Suspicious")
        xAxisList.add("Other")

        yAxisList.add("138")
        yAxisList.add("68")
        yAxisList.add("87")
        yAxisList.add("72")
        yAxisList.add("49")
        yAxisList.add("28")

        val entries = java.util.ArrayList<BarEntry>()

        for (i in yAxisList.indices) {
            entries.add(BarEntry(i.toFloat(), yAxisList[i].replace(",".toRegex(), "").toFloat()))
        }
        val xLabel = arrayOfNulls<String>(xAxisList.size)
        for (i in xAxisList.indices) {
            xLabel[i] = xAxisList.get(i)
        }

        val d = BarDataSet(entries, "")
        val colors = ArrayList<Int>()

        colors.add(Color.rgb(43, 167, 0))
        colors.add(Color.rgb(50, 101, 254))
        colors.add(Color.rgb(255, 86, 63))
        colors.add(Color.rgb(255, 214, 0))
        colors.add(Color.rgb(254, 28, 29))
        colors.add(Color.rgb(234, 160, 160))
        d.colors = colors


        val sets = java.util.ArrayList<IBarDataSet>()
        sets.add(d)
        data = BarData(sets)
        data.barWidth = 0.3.toFloat()
        barchart.data = data
        barchart.data
        barchart.data.setValueTextColor(Color.rgb(255, 255, 255))
        barchart.animateXY(2000, 2000)
        barchart.description.isEnabled = false
        barchart.legend.isEnabled = true
        barchart.xAxis.isEnabled = true
        barchart.setDrawMarkers(true)
        barchart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisList)
        barchart.xAxis.labelCount = entries.size
        barchart.xAxis.textColor = Color.WHITE
        barchart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barchart.xAxis.yOffset = 10f
        barchart.xAxis.xOffset = 10f
        barchart.xAxis.granularity = 1.0f
        barchart.xAxis.setDrawGridLines(false)
        barchart.extraBottomOffset = 20f
        barchart.legend.isEnabled = true
        barchart.setFitBars(false)
        barchart.setVisibleXRangeMaximum(7f)
        barchart.axisLeft.valueFormatter = DashboardFragment.yAxisValueFormatter()
        barchart.axisRight.valueFormatter = DashboardFragment.yAxisValueFormatter()
        barchart.axisRight.textColor = Color.WHITE
        barchart.axisLeft.isEnabled = false
        barchart.isHorizontalScrollBarEnabled = true
        barchart.setDrawValueAboveBar(true);
        barchart.invalidate()
    }
}
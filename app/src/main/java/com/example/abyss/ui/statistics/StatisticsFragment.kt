package com.example.abyss.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.abyss.R
import com.example.abyss.databinding.FragmentStatisticsBinding
import com.example.abyss.extensions.onClick
import com.example.abyss.utils.HidingNavigationBar
import com.example.abyss.utils.LineChartXAxisValueFormatter
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kodeinViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import java.text.DateFormat
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*


class StatisticsFragment : Fragment(), KodeinAware {

    override val kodein by kodein()
    private val viewModel: StatisticsViewModel by kodeinViewModel()
    private lateinit var binding: FragmentStatisticsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        binding.statisticsViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        (activity as HidingNavigationBar).hideNavigationBar(true)

        subscription()
        ddd()

        return binding.root
    }

    private fun subscription() {
        binding.backBtn.onClick {
            findNavController().popBackStack()
        }
        binding.buttonSelect.onClick {
            monthPicker()
        }
    }

    private fun monthPicker() {
        val customTitle = "Выбор месяца"
        val dialogFragment = MonthYearPickerDialogFragment
            .getInstance(
                viewModel.monthSelectedInt.value!!,
                viewModel.yearSelectedInt.value!!,
                customTitle
            )
        dialogFragment.show(parentFragmentManager, null)
        dialogFragment.setOnDateSetListener { year, monthOfYear ->
            viewModel.monthSelectedInt.value = monthOfYear
            viewModel.yearSelectedInt.value = year
            viewModel.monthSelectedString.value = DateFormatSymbols().months[monthOfYear]
            viewModel.yearSelectedString.value = year.toString()
        }
    }


    private fun lkj() {
        val xAxis = binding.chart1.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)

        xAxis.valueFormatter = LineChartXAxisValueFormatter()


        val entriesFirst: ArrayList<Entry> = ArrayList()
        entriesFirst.add(Entry(1623233453f, 2f))
        entriesFirst.add(Entry(1623319853f, 2f))
        entriesFirst.add(Entry(1623406253f, 7f))
        entriesFirst.add(Entry(1623492653f, 3f))
        entriesFirst.add(Entry(1623579053f, 5f))
        // На основании массива точек создадим первую линию с названием
        val datasetFirst = LineDataSet(entriesFirst, "Подписавшиеся")
        // График будет заполненным
        datasetFirst.setDrawFilled(true)
        // Цвет
        datasetFirst.color = R.color.purple_500

        // Массив координат точек второй линии
        val entriesSecond: ArrayList<Entry> = ArrayList()
        entriesSecond.add(Entry(0.5f, 0f))
        entriesSecond.add(Entry(2.5f, 2f))
        entriesSecond.add(Entry(3.5f, 1f))
        entriesSecond.add(Entry(3.6f, 2f))
        entriesSecond.add(Entry(4f, 0.5f))
        entriesSecond.add(Entry(5.1f, -0.5f))
        // На основании массива точек создаем вторую линию с названием
        val datasetSecond = LineDataSet(entriesSecond, "Отписавшиеся")
        // Цвет
        datasetSecond.color = Color.BLACK
        // График будет плавным
        datasetSecond.mode = LineDataSet.Mode.CUBIC_BEZIER

        // Линии графиков соберем в один массив
        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        dataSets.add(datasetFirst)
        dataSets.add(datasetSecond)
        // Создадим переменную  данных для графика
        val data = LineData(dataSets)
        // Передадим данные для графика в сам график
        binding.chart1.data = data

        binding.chart1.description.text = "что-то"
        binding.chart1.setNoDataText("Нет данных!")


        binding.chart1.animateXY(600, 600, Easing.EaseInBounce)
    }


    private fun ddd() {
        //Part1
        val entries = ArrayList<Entry>()
        val entries2 = ArrayList<Entry>()


//Part2
        /**В икс засовывай timeStamp число даты
         * (Про timeStamp читай в интернете, в кратце это 4-байтное целое число, равное количеству секунд, прошедших с полуночи 1 января 1970 года)
         * стандартный джавовский класс даты в неё конвертится спокойно
         * */

        val formatter: DateFormat = SimpleDateFormat("dd-MM-yyyy")

        val strDate: String = "1-6-2021"
        val date = formatter.parse(strDate) as Date
        var g = date.time.toFloat()
//        entries.add(Entry(1623233453f, 2f))
//        entries.add(Entry(1623319853f, 2f))
//        entries.add(Entry(1623406253f, 7f))
//        entries.add(Entry(1623492653f, 3f))
//        entries.add(Entry(1623579053f, 5f))
        entries.add(Entry(g, 8f))

        entries2.add(Entry(2f, 20f))
        entries2.add(Entry(3f, 4f))
        entries2.add(Entry(4f, 10f))
        entries2.add(Entry(4f, 20f))
        entries2.add(Entry(5f, 16f))
        entries2.add(Entry(6f, 20f))
        entries2.add(Entry(7f, 10f))
        entries2.add(Entry(8f, 5f))
        entries2.add(Entry(9f, 20f))
        entries2.add(Entry(10f, 16f))

//Part3
        val vl = LineDataSet(entries, "Подписавшиеся")
        val v2 = LineDataSet(entries, "Отписавшиеся")
//Part4
//        vl.setDrawValues(false)
//        vl.setDrawFilled(true)
        vl.lineWidth = 3f
//        vl.fillColor = R.color.purple_500
//        vl.fillAlpha = R.color.purple_500

//        v2.setDrawValues(false)
//        v2.setDrawFilled(true)
        v2.lineWidth = 3f
//        v2.fillColor = R.color.black
//        v2.fillAlpha = R.color.black
        vl.color = R.color.purple_500
        v2.color = R.color.black
        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        dataSets.add(vl)
        dataSets.add(v2)
//Part5
        binding.chart1.xAxis.labelRotationAngle = 0f
        binding.chart1.xAxis.position = XAxis.XAxisPosition.BOTTOM
//Part6
        binding.chart1.data = LineData(vl, v2)




        binding.chart1.xAxis.valueFormatter = LineChartXAxisValueFormatter()


//Part7
        binding.chart1.axisRight.isEnabled = false
//        binding.graph1.xAxis.axisMaximum = j+0.1f

//Part8
        binding.chart1.setTouchEnabled(true)
        binding.chart1.setPinchZoom(true)

//Part9
        binding.chart1.description.text = "Days"
        binding.chart1.setNoDataText("No forex yet!")

//Part10
        binding.chart1.animateXY(600, 600, Easing.EaseInBounce)

//Part11
//        val markerView = CustomMarker(this@ShowForexActivity, R.layout.marker_view)
//        binding.graph1.marker = markerView
    }


//        binding.chart1.description.isEnabled = false

//        val l: Legend = binding.chart1.legend
//        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
//        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
//        l.orientation = Legend.LegendOrientation.HORIZONTAL
//        l.setDrawInside(false)
//        l.form = LegendForm.SQUARE
//        l.formSize = 9f
//        l.textSize = 11f
//        l.xEntrySpace = 4f
//
//        val mv = context?.let { XYMarkerView(it, xAxisFormatter) }
//        mv?.chartView = binding.chart1 // For bounds control
//
//        binding.chart1.marker = mv // Set the marker to the chart
//
//        setData(20, 2f)
//
//    }


//    @SuppressLint("SimpleDateFormat")
//    private fun setData(k: Int, range: Float) {
//        val start = 1f
//        val values = ArrayList<Entry>()
//
//        val xLabel = ArrayList<String>()
//        val calendar = Calendar.getInstance()
//        val dateFormat = SimpleDateFormat("dd-MMM-yyyy")
//
//        for (i in 0..50) {
//            calendar.add(Calendar.DAY_OF_YEAR, i)
//            val date = calendar.time
//            val txtDate = dateFormat.format(date)
//
//            xLabel.add(txtDate)
//        }
//        var x: Float = 0f
//        for (i in 0..50) {
//            val f = (0..10).random()
//
//
//            values.add(Entry(x, f.toFloat())) // add one entry per hour
//            x += 1f
//        }
//
//
//        // create a dataset and give it a type
//        val set1 = LineDataSet(values, "year")
//        set1.axisDependency = AxisDependency.LEFT
//        set1.color = ColorTemplate.getHoloBlue()
//        set1.valueTextColor = ColorTemplate.getHoloBlue()
//        set1.lineWidth = 1.5f
//        set1.setDrawCircles(false)
//        set1.setDrawValues(false)
//        set1.fillAlpha = 65
//        set1.fillColor = ColorTemplate.getHoloBlue()
//        set1.highLightColor = Color.rgb(244, 117, 117)
//        set1.setDrawCircleHole(false)
//
//        // create a data object with the data sets
//
//        // create a data object with the data sets
//        val data = LineData(set1)
//        data.setValueTextColor(Color.WHITE)
//        data.setValueTextSize(9f)
//
//        // set data
//
//        // set data
//        binding.chart1.data = data
//
//    }

//
//    private fun hh() {
//        val xLabel = ArrayList<String>()
//        val calendar = Calendar.getInstance()
//        val dateFormat = SimpleDateFormat("dd-MMM-yyyy")
//
//        for (i in 0..50) {
//            calendar.add(Calendar.DAY_OF_YEAR, i)
//            val date = calendar.time
//            val txtDate = dateFormat.format(date)
//
//            xLabel.add(txtDate)
//        }
//
//        val xAxis = binding.graph1.xAxis
//        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.setDrawGridLines(false)

//        xAxis.valueFormatter = MyCustomFormatter()

//        val datasetFirst = LineDataSet(xLabel, "Подписавшиеся")

//        val entriesFirst: ArrayList<Entry> = ArrayList()
//        entriesFirst.add(Entry(1f, 5f))
//        entriesFirst.add(Entry(2f, 2f))
//        entriesFirst.add(Entry(3f, 1f))
//        entriesFirst.add(Entry(4f, 3f))
//        entriesFirst.add(Entry(5f, 4f))
//        entriesFirst.add(Entry(6f, 1f))
//        // На основании массива точек создадим первую линию с названием
//        val datasetFirst = LineDataSet(entriesFirst, "Подписавшиеся")
//        // График будет заполненным
//        datasetFirst.setDrawFilled(true)
//        // Цвет
//        datasetFirst.color = R.color.purple_500
//
//        // Массив координат точек второй линии
//        val entriesSecond: ArrayList<Entry> = ArrayList()
//        entriesSecond.add(Entry(0.5f, 0f))
//        entriesSecond.add(Entry(2.5f, 2f))
//        entriesSecond.add(Entry(3.5f, 1f))
//        entriesSecond.add(Entry(3.6f, 2f))
//        entriesSecond.add(Entry(4f, 0.5f))
//        entriesSecond.add(Entry(5.1f, -0.5f))
//        // На основании массива точек создаем вторую линию с названием
//        val datasetSecond = LineDataSet(entriesSecond, "Отписавшиеся")
//        // Цвет
//        datasetSecond.color = Color.BLACK
//        // График будет плавным
//        datasetSecond.mode = LineDataSet.Mode.CUBIC_BEZIER
//
//        // Линии графиков соберем в один массив
//        val dataSets: ArrayList<ILineDataSet> = ArrayList()
//        dataSets.add(datasetFirst)
//        dataSets.add(datasetSecond)
//        // Создадим переменную  данных для графика
//        val data = LineData(dataSets)
//        // Передадим данные для графика в сам график
//        binding.graph1.data = data
//
//        binding.graph1.description.text = "что-то"
//        binding.graph1.setNoDataText("Нет данных!")

    // График будет анимироваться 0.5 секунды
//        binding.graph1.animateXY(600, 600, Easing.EaseInBounce)
//    }

//    class MyCustomFormatter : ValueFormatter()
//    {
//        override fun getFormattedValue(value: Float, axis: AxisBase?): String
//        {
//            val dateInMillis = value.toLong()
//            val date = Calendar.getInstance().apply {
//                timeInMillis = dateInMillis
//            }.time
//
//            return SimpleDateFormat("dd MMM", Locale.getDefault()).format(date)
//        }
//    }


}
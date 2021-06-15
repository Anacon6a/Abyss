package com.example.abyss.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.abyss.R
import com.example.abyss.databinding.FragmentStatisticsBinding
import com.example.abyss.extensions.onClick
import com.example.abyss.ui.profile.ProfileFragmentDirections
import com.example.abyss.utils.HidingNavigationBar
import com.example.abyss.utils.ChartValueFormatter
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import kodeinViewModel
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
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

        binding.typeSortSpinner.adapter = ArrayAdapter.createFromResource(activity?.applicationContext!!, R.array.sortingType, android.R.layout.simple_spinner_dropdown_item)

        if (viewModel.eventSubscriptionDataReceived.value == true) {
            setSubscriptionData()
        }
        if (viewModel.eventAudienceReachDataReceived.value == true) {
            setUserActData()
        }
        if (viewModel.typeSortSelectInt.value != null) {
            binding.typeSortSpinner.setSelection(viewModel.typeSortSelectInt.value!!)
        } else {
            binding.typeSortSpinner.setSelection(0)
        }

        subscription()
        setupSubscriptionPieChart()
        setupUserActBarChart()
        setItemSpinner()
        setAdaptersForRecycler()

        return binding.root
    }

    private fun subscription() {
        lifecycleScope.launch {
            binding.backBtn.onClick {
                findNavController().popBackStack()
            }
            binding.buttonSelect.onClick {
                monthPicker()
            }
            viewModel.eventSubscriptionDataReceived.observe(viewLifecycleOwner, { event ->
                setSubscriptionData()
            })
            viewModel.eventAudienceReachDataReceived.observe(viewLifecycleOwner, { event ->
                setUserActData()
            })
            binding.typeSortSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        viewModel.seTypeSort(position)
                    }

                }
            binding.typeSortSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        viewModel.seTypeSort(position)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }
            viewModel.postsPagingAdapter.setOnItemClickListener { post, imageView, postContainer ->
                val action =
                    StatisticsFragmentDirections.actionStatisticsFragmentToPostFragment(post)
                findNavController().navigate(
                    action, FragmentNavigator.Extras.Builder().addSharedElements(
                        mapOf(
                            postContainer to postContainer.transitionName
                        )
                    ).build()
                )
            }
        }
    }

    private val l = arrayListOf("Самые популярные", "По количеству просмотров")
    private fun setItemSpinner() {
        viewModel.typeSortSelectInt.value?.let {
            binding.typeSortSpinner.setSelection(it)
        }
    }

    private fun monthPicker() {
        val customTitle = "Выбор периода"
        val dialogFragment = MonthYearPickerDialogFragment
            .getInstance(
                viewModel.monthSelectedInt.value!!,
                viewModel.yearSelectedInt.value!!,
                customTitle
            )
        dialogFragment.show(parentFragmentManager, null)
        dialogFragment.setOnDateSetListener { year, monthOfYear ->
            viewModel.setDate(monthOfYear, year)
        }
    }

    private fun setupSubscriptionPieChart() {
        lifecycleScope.launch {
            binding.chart1.isDrawHoleEnabled = true
            binding.chart1.setEntryLabelTextSize(14F)
            binding.chart1.setEntryLabelColor(Color.WHITE)
            binding.chart1.centerText = "Пользователи"
            binding.chart1.setCenterTextSize(18F)
            binding.chart1.description.isEnabled = false
            binding.chart1.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            binding.chart1.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            binding.chart1.legend.orientation = Legend.LegendOrientation.VERTICAL
            binding.chart1.legend.setDrawInside(false)
            binding.chart1.legend.isEnabled = true
            binding.chart1.legend.textSize = 16F
            binding.chart1.setDrawSliceText(false)
        }
    }

    private fun setSubscriptionData() {
        lifecycleScope.launch {
            val entries: ArrayList<PieEntry> = ArrayList()
            entries.add(PieEntry(viewModel.numberSubscriptions.value!!.toFloat(), "Подписавшиеся"))
            entries.add(PieEntry(viewModel.numberUnsubscriptions.value!!.toFloat(), "Отписавшиеся"))
            val dataSet = PieDataSet(entries, "")
            dataSet.colors = arrayListOf(R.color.purple_500, Color.BLACK)
            val data = PieData(dataSet)
            data.setValueTextColor(Color.WHITE)
            data.setDrawValues(true)
            data.setValueFormatter(PercentFormatter())
            data.setValueTextSize(12f)
            data.setValueFormatter(ChartValueFormatter())
            binding.chart1.data = data
            binding.chart1.invalidate()
            binding.chart1.animateY(1400, Easing.EaseInOutQuad)
        }
    }

    private fun setupUserActBarChart() {
        lifecycleScope.launch {
            binding.chart2.legend.textSize = 12f
            binding.chart2.legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            binding.chart2.legend.orientation = Legend.LegendOrientation.HORIZONTAL
            binding.chart2.legend.textColor = R.color.black
            binding.chart2.description.isEnabled = false
            binding.chart2.xAxis.isEnabled = false
        }
    }

    private fun setUserActData() {
        lifecycleScope.launch {
            val entries1: ArrayList<BarEntry> =
                arrayListOf(BarEntry(1f, viewModel.numberViews.value!!.toFloat()))
            val barDataSetViews = BarDataSet(entries1, "Просмотры")
            barDataSetViews.color = resources.getColor(R.color.color1)
            barDataSetViews.valueTextSize = 14f
            barDataSetViews.valueFormatter = ChartValueFormatter()
            val entries2: ArrayList<BarEntry> =
                arrayListOf(BarEntry(2f, viewModel.numberLikes.value!!.toFloat()))
            val barDataSetLikes = BarDataSet(entries2, "Лайки")
            barDataSetLikes.valueTextSize = 14f
            barDataSetLikes.color = resources.getColor(R.color.color2)
            barDataSetLikes.valueFormatter = ChartValueFormatter()
            val entries3: ArrayList<BarEntry> =
                arrayListOf(BarEntry(3f, viewModel.numberComments.value!!.toFloat()))
            val barDataSetComments = BarDataSet(entries3, "Комментарии")
            barDataSetComments.valueTextSize = 14f
            barDataSetComments.color = resources.getColor(R.color.color3)
            barDataSetComments.valueFormatter = ChartValueFormatter()
            val entries4: ArrayList<BarEntry> =
                arrayListOf(BarEntry(4f, viewModel.numberSaves.value!!.toFloat()))
            val barDataSetSaves = BarDataSet(entries4, "Сохранения")
            barDataSetSaves.valueTextSize = 14f
            barDataSetSaves.color = resources.getColor(R.color.color4)
            barDataSetSaves.valueFormatter = ChartValueFormatter()

            val b = BarData(barDataSetViews, barDataSetLikes, barDataSetComments, barDataSetSaves)
            binding.chart2.data = b
            binding.chart2.legend.textColor = R.color.black
            binding.chart2.animateY(1200)
        }
    }

    private fun setAdaptersForRecycler() {
        lifecycleScope.launch {
            binding.postsRecyclerView.apply {
                layoutManager = StaggeredGridLayoutManager(1, RecyclerView.HORIZONTAL)
                setHasFixedSize(false)
                itemAnimator = null
                adapter = viewModel.postsPagingAdapter
            }
        }
    }


}
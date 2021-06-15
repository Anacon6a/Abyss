package com.example.abyss.ui.statistics

import android.R.attr.data
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abyss.model.repository.statistics.StatisticsRepository
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class StatisticsViewModel(
    private val externalScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    private val statisticsRepository: StatisticsRepository
) : ViewModel() {

    //    private val calendar: Calendar = Calendar.getInstance()
    private val monthList = listOf<String>(
        "январь", "февраль", "март", "апрель", "май", "июнь",
        "июль", "август", "сентябрь", "октябрь", "ноябрь", "декабрь"
    )

    val yearSelectedInt = MutableLiveData<Int>()
    val yearSelectedString = MutableLiveData<String>()

    val monthSelectedInt = MutableLiveData<Int>()
    val monthSelectedString = MutableLiveData<String>()

    val subscriptionsEntryList = java.util.ArrayList<Entry>()
    val unsubscriptionsEntryList = java.util.ArrayList<Entry>()
    val viewsEntryList = java.util.ArrayList<Entry>()
    val likesEntryList = java.util.ArrayList<Entry>()
    val commentsEntryList = java.util.ArrayList<Entry>()
    val saveEntryList = java.util.ArrayList<Entry>()

    init {
        val calendar: Calendar = Calendar.getInstance()
        setDate(calendar[Calendar.MONTH], calendar[Calendar.YEAR])
    }

    fun setDate(month: Int, year: Int) {
        externalScope.launch(ioDispatcher) {
            monthSelectedInt.postValue(month)
            yearSelectedInt.postValue(year)
            monthSelectedString.postValue(monthList[month])
            yearSelectedString.postValue(year.toString())

            val d = arrayListOf(async {
                getSubscriptions(month, year)
            }, async {
                getUnsubscriptions(month, year)
            }, async {
                getViews(month, year)
            }, async {
                getLikes(month, year)
            }, async {
                getComments(month, year)
            }, async {
                getSaves(month, year)
            })
            d.awaitAll()
//            hhh(month, year)
        }
//        getAllActions(month, year)
    }

    private suspend fun getAllActions(month: Int, year: Int) {
        externalScope.launch(ioDispatcher) {
            statisticsRepository.getAllActions(month, year).collect {
            }
        }.join()
    }

    private suspend fun getSubscriptions(month: Int, year: Int) {
        externalScope.launch(ioDispatcher) {
            statisticsRepository.getSubscriptionsActions(month, year).collect {

            }
        }.join()
    }

    fun hhh(month: Int, year: Int) {


        val calendar: Calendar = GregorianCalendar(year, month, 1)

        val daysInMonth: Int = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in 1..daysInMonth) {


            val strDate: String = "$i-$month-$year"
            val formatter: DateFormat = SimpleDateFormat("dd-MM-yyyy")
            val date = formatter.parse(strDate) as Date
        }
    }

    private suspend fun getUnsubscriptions(month: Int, year: Int) {
        externalScope.launch(ioDispatcher) {
            statisticsRepository.getUnsubscriptionsActions(month, year).collect {

                val calendar: Calendar = GregorianCalendar(year, month, 1)
                val daysInMonth: Int = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                val formatter: DateFormat = SimpleDateFormat("dd-MM")

                var nom = 0
                for (i in 1..daysInMonth) {
                    val strDate: String = "$i-${month+1}"
                    val date = formatter.parse(strDate) as Date
                    var numbers = 0
                    for ( j in nom until it.size) {
                        if (it[j].date!!.day == date.day){
                            numbers += 1
                        } else {
                           nom = j
                            break
                        }
                    }

                   var g = date.time.toFloat()
//                    var kk = date.time.toInt()

                    unsubscriptionsEntryList.add(Entry( g, numbers.toFloat()))
                    val h = 4
                }

            }
        }.join()
    }

    private suspend fun getViews(month: Int, year: Int) {
        externalScope.launch(ioDispatcher) {
            statisticsRepository.getViewsActions(month, year).collect {

            }
        }.join()
    }

    private suspend fun getLikes(month: Int, year: Int) {
        externalScope.launch(ioDispatcher) {
            statisticsRepository.getLikesActions(month, year).collect {

            }
        }.join()
    }

    private suspend fun getComments(month: Int, year: Int) {
        externalScope.launch(ioDispatcher) {
            statisticsRepository.getCommentsActions(month, year).collect {

            }
        }.join()
    }

    private suspend fun getSaves(month: Int, year: Int) {
        externalScope.launch(ioDispatcher) {
            statisticsRepository.getSavesActions(month, year).collect {

            }
        }.join()
    }
}
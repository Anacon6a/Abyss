package com.example.abyss.ui.statistics

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abyss.extensions.secondTimestamp
import com.example.abyss.model.data.entity.NotificationData
import com.example.abyss.model.repository.statistics.StatisticsRepository
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


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

    val subscriptionsEntryList = MutableLiveData<ArrayList<Entry>>()
    val unsubscriptionsEntryList = MutableLiveData<ArrayList<Entry>>()
    val viewsEntryList = MutableLiveData<ArrayList<Entry>>()
    val likesEntryList = MutableLiveData<ArrayList<Entry>>()
    val commentsEntryList = MutableLiveData<ArrayList<Entry>>()
    val saveEntryList = MutableLiveData<ArrayList<Entry>>()

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
//                getSubscriptions(month, year)
//            }, async {
//                getUnsubscriptions(month, year)
//            }, async {
//                getViews(month, year)
//            }, async {
                getLikes(month, year)
//            }, async {
//                getComments(month, year)
//            }, async {
//                getSaves(month, year)
            })
            d.awaitAll()
//            hhh(month, year)
        }

    }


    private suspend fun getSubscriptions(month: Int, year: Int) {
        externalScope.launch(ioDispatcher) {
            statisticsRepository.getSubscriptionsActions(month, year).collect {
                if (it.isNotEmpty()) {
                    val entryList = createEntries(it, month, year)
                    subscriptionsEntryList.value = entryList
                }
            }
        }.join()
    }

    private suspend fun getUnsubscriptions(month: Int, year: Int) {
        externalScope.launch(ioDispatcher) {
            statisticsRepository.getUnsubscriptionsActions(month, year).collect {
                if (it.isNotEmpty()) {
                    val entryList = createEntries(it, month, year)
                    unsubscriptionsEntryList.value = entryList
                }
            }
        }.join()
    }

    private suspend fun getViews(month: Int, year: Int) {
        externalScope.launch(ioDispatcher) {
            statisticsRepository.getViewsActions(month, year).collect {
                if (it.isNotEmpty()) {
                    val entryList = createEntries(it, month, year)
                    viewsEntryList.value = entryList
                }
            }
        }.join()
    }

    private suspend fun getLikes(month: Int, year: Int) {
        externalScope.launch(ioDispatcher) {
            statisticsRepository.getLikesActions(month, year).collect {
                if (it.isNotEmpty()) {
                    val entryList = createEntries(it, month, year)
                    likesEntryList.value = entryList
                }
            }
        }.join()
    }

    private suspend fun getComments(month: Int, year: Int) {
        externalScope.launch(ioDispatcher) {
            statisticsRepository.getCommentsActions(month, year).collect {
                if (it.isNotEmpty()) {
                    val entryList = createEntries(it, month, year)
                    commentsEntryList.value = entryList
                }
            }
        }.join()
    }

    private suspend fun getSaves(month: Int, year: Int) {
        externalScope.launch(ioDispatcher) {
            statisticsRepository.getSavesActions(month, year).collect {
                if (it.isNotEmpty()) {
                    val entryList = createEntries(it, month, year)
                    saveEntryList.value = entryList
                }
            }
        }.join()
    }

    private fun createEntries(list: MutableList<NotificationData>, month: Int, year: Int): ArrayList<Entry> {
        val entryList = arrayListOf<Entry>()
        val calendar: Calendar = GregorianCalendar(year, month, 1)
        val daysInMonth: Int = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val formatter: DateFormat = SimpleDateFormat("dd-MM-yyyy")

        var nom = 0
        for (i in 1..daysInMonth) {
            val strDate: String = "$i-${month + 1}-$year"
            val date = formatter.parse(strDate) as Date

            var numbers = 0
            for (j in nom until list.size) {
                if (list[j].date!!.date == i) {
                    numbers += 1
                } else {
                    nom = j
                    break
                }
            }
            entryList.add(Entry(date.secondTimestamp, numbers.toFloat()))
        }
        return entryList
    }
}
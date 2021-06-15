package com.example.abyss.ui.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abyss.adapters.profile.ProfileMyPostsPagingAdapter
import com.example.abyss.model.repository.post.PostRepository
import com.example.abyss.model.repository.statistics.StatisticsRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.util.*


class StatisticsViewModel(
    private val externalScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    private val statisticsRepository: StatisticsRepository,
    val postsPagingAdapter: ProfileMyPostsPagingAdapter,
    private val postRepository: PostRepository
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

    //    val periodSelectedInt = MutableLiveData<Int>()
    val typeSortSelectInt = MutableLiveData<Int>()

    val numberSubscriptions = MutableLiveData<Int>()
    val numberUnsubscriptions = MutableLiveData<Int>()
    val numberViews = MutableLiveData<Int>()
    val numberLikes = MutableLiveData<Int>()
    val numberComments = MutableLiveData<Int>()
    val numberSaves = MutableLiveData<Int>()

    private val _eventSubscriptionDataReceived = MutableLiveData<Boolean>()
    val eventSubscriptionDataReceived: LiveData<Boolean>
        get() = _eventSubscriptionDataReceived

    private val _eventAudienceReachDataReceived = MutableLiveData<Boolean>()
    val eventAudienceReachDataReceived: LiveData<Boolean>
        get() = _eventAudienceReachDataReceived

    init {
        val calendar: Calendar = Calendar.getInstance()
        setDate(calendar[Calendar.MONTH], calendar[Calendar.YEAR])
        seTypeSort(0)
    }

    fun setDate(month: Int, year: Int) {
        externalScope.launch(ioDispatcher) {
            monthSelectedInt.postValue(month)
            yearSelectedInt.postValue(year)
            monthSelectedString.postValue(monthList[month])
            yearSelectedString.postValue(year.toString())

            val d = arrayListOf(async {
                val d1 = arrayListOf(async {
                    getSubscriptions(month, year)
                }, async {
                    getUnsubscriptions(month, year)
                })
                d1.awaitAll()
                _eventSubscriptionDataReceived.postValue(true)
            }, async {
                val d2 = arrayListOf(async {
                    getViews(month, year)
                }, async {
                    getLikes(month, year)
                }, async {
                    getComments(month, year)
                }, async {
                    getSaves(month, year)
                })
                d2.awaitAll()
                _eventAudienceReachDataReceived.postValue(true)
            })
            d.awaitAll()
        }
    }

    private suspend fun getSubscriptions(month: Int, year: Int) {
        externalScope.launch(ioDispatcher) {
            statisticsRepository.getNumberActions(month, year, "subscribe").collect {
                numberSubscriptions.postValue(it)
            }
        }.join()
    }

    private suspend fun getUnsubscriptions(month: Int, year: Int) {
        externalScope.launch(ioDispatcher) {
            statisticsRepository.getNumberActions(month, year, "unsubscribe").collect {
                numberUnsubscriptions.postValue(it)
            }
        }.join()
    }

    private suspend fun getViews(month: Int, year: Int) {
        externalScope.launch(ioDispatcher) {
            statisticsRepository.getNumberActions(month, year, "view").collect {
                numberViews.postValue(it)
            }
        }.join()
    }

    private suspend fun getLikes(month: Int, year: Int) {
        externalScope.launch(ioDispatcher) {
            statisticsRepository.getNumberActions(month, year, "like").collect {
                numberLikes.postValue(it)
            }
        }.join()
    }

    private suspend fun getComments(month: Int, year: Int) {
        externalScope.launch(ioDispatcher) {
            statisticsRepository.getNumberActions(month, year, "comment").collect {
                numberComments.postValue(it)
            }
        }.join()
    }

    private suspend fun getSaves(month: Int, year: Int) {
        externalScope.launch(ioDispatcher) {
            statisticsRepository.getNumberActions(month, year, "save").collect {
                numberSaves.postValue(it)
            }
        }.join()
    }

    fun seTypeSort(type: Int) {
        viewModelScope.launch {
            typeSortSelectInt.postValue(type)
            getPosts(type)
        }
    }

    private fun getPosts(type: Int) {
        externalScope.launch(ioDispatcher) {
            postRepository.getSortedPosts(1, 2021, type).collect {
                postsPagingAdapter.submitData(it)
            }
        }
    }
}
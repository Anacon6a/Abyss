package com.example.abyss.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.example.abyss.adapters.NotificationsPagingAdapter
import com.example.abyss.adapters.PostNewsFeedPagingAdapter
import com.example.abyss.model.repository.post.PostRepository
import com.example.abyss.model.repository.statistics.StatisticsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val statisticsRepository: StatisticsRepository,
    val newNotificationsPagingAdapter: NotificationsPagingAdapter,
    val viewedNotificationsPagingAdapter: NotificationsPagingAdapter
) : ViewModel() {

    private val _progressBarLoadingNotifications = MutableLiveData<Boolean>()
    val progressBarLoadingNotifications: LiveData<Boolean>
        get() = _progressBarLoadingNotifications

    private val _hideNewNotificationsIfFalse = MutableLiveData<Boolean>()
    val hideNewNotificationsIfFalse: LiveData<Boolean>
        get() = _hideNewNotificationsIfFalse

    private val _hideViewedNotificationsIfFalse = MutableLiveData<Boolean>()
    val hideViewedNotificationsIfFalse: LiveData<Boolean>
        get() = _hideViewedNotificationsIfFalse


    init {
        statusLoading(newNotificationsPagingAdapter, false)
        statusLoading(viewedNotificationsPagingAdapter, true)
        getNewNotificationsForPagingAdapter()
        getViewedNotificationsForPagingAdapter()
    }

    private fun getViewedNotificationsForPagingAdapter() {
        viewModelScope.launch {
            statisticsRepository.getViewedNotification()?.collect {
                viewedNotificationsPagingAdapter.submitData(it)
            }
        }
    }
var f: Int = 4
    private fun getNewNotificationsForPagingAdapter() {
        viewModelScope.launch {
            statisticsRepository.getNewNotification()?.collect {
                if (f == 0){
                    newNotificationsPagingAdapter.submitData(PagingData.empty())
                } else {
                newNotificationsPagingAdapter.submitData(it) }
                f -= 1
            }
        }
    }

    private var lVN: Boolean = true
    private var lNN: Boolean = true
    private fun statusLoading(pagingAdapter: NotificationsPagingAdapter, viewed: Boolean) {
        viewModelScope.launch {
            pagingAdapter.loadStateFlow.collectLatest { loadState ->
                if (viewed) {
                    lVN = loadState.source.refresh is LoadState.Loading
                } else {
                    lNN = loadState.source.refresh is LoadState.Loading
                }
                loadingNotifications(lNN || lVN)
                if (loadState.source.refresh is LoadState.NotLoading) {
                    if (viewed) {
                        _hideViewedNotificationsIfFalse.postValue(pagingAdapter.itemCount > 0)
                    } else {
                        _hideNewNotificationsIfFalse.postValue(pagingAdapter.itemCount > 0)
                    }
                }
            }
        }
    }

    private fun loadingNotifications(boolean: Boolean) {
        _progressBarLoadingNotifications.postValue(boolean)
    }


    fun onRefresh() {
        getViewedNotificationsForPagingAdapter()
        getNewNotificationsForPagingAdapter()
    }
}
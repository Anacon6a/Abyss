package com.example.abyss.ui.home.search

import android.view.View
import android.widget.RadioButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.example.abyss.R
import com.example.abyss.adapters.home.search.SearchPostsPagingAdapter
import com.example.abyss.adapters.home.search.SearchUsersPagingAdapter
import com.example.abyss.model.repository.post.PostRepository
import com.example.abyss.model.repository.user.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*


class SearchViewModel(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val externalScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    val searchPostsPagingAdapter: SearchPostsPagingAdapter,
    val searchUsersPagingAdapter: SearchUsersPagingAdapter
) : ViewModel() {

    private val _progressBarLoading = MutableLiveData<Boolean>()
    val progressBarLoading: LiveData<Boolean>
        get() = _progressBarLoading

    private val _searchText = MutableLiveData<String>()
    val searchText: LiveData<String>
        get() = _searchText

    private val addFirstSearch = MutableLiveData<Boolean>()

    private val orderBySelection = MutableLiveData<Int>().apply { value = 0 }

//    private val periodSelection = MutableLiveData<Int>().apply { value = 0 }

    init {
        statusLoading()
    }

    fun initial(t: String) {
        viewModelScope.launch {
            if (addFirstSearch.value == null) {
                addFirstSearch.postValue(true)
                _searchText.postValue(t)
            }
        }
    }

    private fun statusLoading() {
        externalScope.launch {
            searchPostsPagingAdapter.loadStateFlow.collectLatest { loadState ->
                loading(loadState.source.refresh is LoadState.Loading)
            }
            searchUsersPagingAdapter.loadStateFlow.collectLatest { loadState ->
                loading(loadState.source.refresh is LoadState.Loading)
            }
        }
    }

    private fun loading(boolean: Boolean) {
        _progressBarLoading.postValue(boolean)
    }

    fun getSearchResults(text: String) {
        _searchText.value = text
        getPosts()
        getUsers()
    }

    private fun getUsers() {
        viewModelScope.launch {
                userRepository.getFoundUsers(
                    _searchText.value!!.toLowerCase(Locale.ROOT),
                    orderBySelection.value!!,
                ).collect {
                    searchUsersPagingAdapter.submitData(it)
                }
        }
    }

    private fun getPosts() {
        viewModelScope.launch {
                postRepository.getFoundPosts(
                    _searchText.value!!.toLowerCase(Locale.ROOT),
                    orderBySelection.value!!,
                )?.collect {
                    searchPostsPagingAdapter.submitData(it)
                }
        }
    }

    fun onRefresh() {
        getPosts()
        getUsers()
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked
            when (view.getId()) {
                R.id.by_popularity_radio ->
                    if (checked) {
                        orderBySelection.value = 0
                    }
                R.id.by_date_desc_radio ->
                    if (checked) {
                        orderBySelection.value = 1
                    }
                R.id.by_date_asc_radio ->
                    if (checked) {
                        orderBySelection.value = 2
                    }
//                R.id.all_time_radio ->
//                    if (checked) {
//                        periodSelection.value = 0
//                    }
//                R.id.today_radio ->
//                    if (checked) {
//                        periodSelection.value = 1
//                    }
//                R.id.last_month_radio ->
//                    if (checked) {
//                        periodSelection.value = 2
//                    }
//                R.id.last_year_radio ->
//                    if (checked) {
//                        periodSelection.value = 3
//                    }
            }
        }
    }
}
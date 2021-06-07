package com.example.abyss.ui.home.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.PagingData
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

    private val searchText = MutableLiveData<String>()

    private val _firstSearch = MutableLiveData<String>()
    val firstSearch: LiveData<String>
        get() = _firstSearch


    init {
        statusLoading()
    }

    fun initial(fs: String) {
        _firstSearch.value = fs
        viewModelScope.launch {
            userRepository.getFoundUsers(firstSearch.value).collect {
                searchUsersPagingAdapter.submitData(it)
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

    fun getSearchResults(text: String?) {
        if (text != null) {
            searchText.value = text.toLowerCase(Locale.ROOT)
        }
        getPost()
        getUsers()
    }

    private fun getUsers() {
        viewModelScope.launch {
            if (!searchText.value.isNullOrEmpty()) {
                userRepository.getFoundUsers(searchText.value).collect {
                    searchUsersPagingAdapter.submitData(it)
                }
            } else {
                searchUsersPagingAdapter.submitData(PagingData.empty())
            }
        }
    }

    private fun getPost() {

    }

    fun onRefresh() {
        getPost()
        getUsers()
    }
}
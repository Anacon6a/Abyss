package com.example.abyss.ui.home

import androidx.lifecycle.*
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.abyss.adapters.PostNewsFeedPagingAdapter
import com.example.abyss.model.data.PostData
import com.example.abyss.model.repository.post.PostRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NewsFeedViewModel(
    private val postRepository: PostRepository,
    private val externalScope: CoroutineScope,
    val postNewsFeedPagingAdapter: PostNewsFeedPagingAdapter,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    init {

    }

    private val _progressBarloadingAllPosts = MutableLiveData<Boolean>()
    val progressBarloadingAllPosts: LiveData<Boolean>
        get() = _progressBarloadingAllPosts

    private val _posts = MutableLiveData<PagingData<PostData>>()


    val getPosts = postRepository.GetPostForProfile()?.cachedIn(externalScope)?.asLiveData()


    fun LoadingPosts(boolean: Boolean) {
        _progressBarloadingAllPosts.postValue(boolean)
    }

}
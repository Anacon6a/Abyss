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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NewsFeedViewModel(
    private val postRepository: PostRepository,
    private val externalScope: CoroutineScope,
    val postNewsFeedPagingAdapter: PostNewsFeedPagingAdapter,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    init {
        StatusLoading()
        GetPostsSubscriptions()
    }

    private val _progressBarloadingAllPosts = MutableLiveData<Boolean>()
    val progressBarloadingAllPosts: LiveData<Boolean>
        get() = _progressBarloadingAllPosts

    private val _postsSubscriptions = MutableLiveData<PagingData<PostData>>()
    val postsSubscriptions: LiveData<PagingData<PostData>>
        get() = _postsSubscriptions

    private val _postsTrends = MutableLiveData<PagingData<PostData>>()
    val postsTrends: LiveData<PagingData<PostData>>
        get() = _postsTrends

    private val posts = MutableLiveData<PagingData<PostData>>()

    val getPosts = postRepository.GetPostForProfile()?.cachedIn(externalScope)?.asLiveData()

    fun SetPosition(position: Int) {
        when (position) {
            0 -> {
                GetPostsSubscriptions()
            }
            1 -> {
                GetPostsTrends()
            }
        }
    }

    private fun StatusLoading() {
        externalScope.launch()
        {
            postNewsFeedPagingAdapter.loadStateFlow.collectLatest { loadState ->
                LoadingPosts(loadState.source.refresh is LoadState.Loading)
            }
        }
    }
    private fun LoadingPosts(boolean: Boolean) {
        _progressBarloadingAllPosts.postValue(boolean)
    }

    private fun GetPostsSubscriptions() {
        externalScope.launch(ioDispatcher) {

            postRepository.GetPostsSubscriptionForNewsFeed()?.collect {
                _postsSubscriptions.postValue(it)
                postNewsFeedPagingAdapter.submitData(it)
            }

        }
    }

    private fun GetPostsTrends() {
        externalScope.launch(ioDispatcher) {

            postRepository.GetPostsTrendsForNewsFeed()?.collect {
                _postsTrends.postValue(it)
                postNewsFeedPagingAdapter.submitData(it)
            }

        }
    }


}
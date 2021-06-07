package com.example.abyss.ui.home.newsfeed

import androidx.lifecycle.*
import androidx.paging.LoadState
import com.example.abyss.adapters.home.newsfeed.NewsFeedPostsPagingAdapter
import com.example.abyss.model.repository.post.PostRepository
import com.example.abyss.model.repository.tag.TagRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.DKodein
import org.kodein.di.generic.instance

class NewsFeedViewModel(
    private val postRepository: PostRepository,
    private val externalScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    private val tagRepository: TagRepository,
    private val kodein: DKodein
) : ViewModel() {

    private val _progressBarLoadingAllPosts = MutableLiveData<Boolean>()
    val progressBarLoadingAllPosts: LiveData<Boolean>
        get() = _progressBarLoadingAllPosts

    private val _listPostPagingAdapters =
        MutableLiveData<ArrayList<NewsFeedPostsPagingAdapter>>().apply { value = arrayListOf() }
    val listPostsPagingAdapters: LiveData<ArrayList<NewsFeedPostsPagingAdapter>>
        get() = _listPostPagingAdapters

//    private val pagingDataNotNull =
//        MutableLiveData<ArrayList<Boolean>>().apply { value = arrayListOf() }

    private val _listTitles =
        MutableLiveData<ArrayList<String>>().apply { value = arrayListOf("Подписки", "Тренды") }
    val listTitles: LiveData<ArrayList<String>>
        get() = _listTitles

    private val _eventTagChange = MutableLiveData<Int>().apply { value = 0 }
    val eventTagChange: LiveData<Int>
        get() = _eventTagChange

    private val wasInitialization = MutableLiveData<Boolean>().apply { value = false }

    suspend fun initial() {
        if (!wasInitialization.value!!) {
            addMainAdapters()
            getUsersTags()
        }
    }

    private fun getAdapter(): NewsFeedPostsPagingAdapter {
        return kodein.instance() as NewsFeedPostsPagingAdapter
    }

    private fun statusLoading(postsPagingAdapter: NewsFeedPostsPagingAdapter) {
        externalScope.launch {
            postsPagingAdapter.loadStateFlow.collectLatest { loadState ->
                loadingPosts(loadState.source.refresh is LoadState.Loading)
            }
        }
    }

    private fun loadingPosts(boolean: Boolean) {
        _progressBarLoadingAllPosts.postValue(boolean)
    }

    private suspend fun addMainAdapters() {
        externalScope.launch(ioDispatcher) {
            for (i in 0 until 2) {
                getAdapter().let {
                    _listPostPagingAdapters.value!!.add(it)
                }
                statusLoading(_listPostPagingAdapters.value!![i])
            }
            wasInitialization.postValue(true)
        }.join()
    }

    private fun getUsersTags() {
        externalScope.launch(ioDispatcher) {
            tagRepository.getUserTags().collect { tags ->
                _eventTagChange.postValue(_eventTagChange.value?.plus(1))

                val l: ArrayList<String> = arrayListOf("Подписки", "Тренды")
                tags.forEach { tag ->
                    l.add(tag.tagName!!)
                }

                addTagsAdapters(tags.size)

                _listTitles.postValue(l)
            }
        }
    }

    private fun addTagsAdapters(numberOfTags: Int) {
        if (listPostsPagingAdapters.value?.size!! > 2) {
            _listPostPagingAdapters.value?.subList(2, listPostsPagingAdapters.value?.size!!)
                ?.clear()
        }
        if (numberOfTags > 0) {
            for (i in 2 until 2 + numberOfTags) {
                getAdapter().let {
                    _listPostPagingAdapters.value!!.add(it)
                }
                statusLoading(_listPostPagingAdapters.value!![i])
            }
        }
    }


    fun getPosts(position: Int) {
        when (position) {
            0 -> getPostsSubscriptions()
            1 -> getPostsTrends()
            else -> getPostsByFilter(position)
        }
    }

    private fun getPostsSubscriptions() {
        externalScope.launch(ioDispatcher) {
//            pagingDataNotNull.value!!.elementAtOrNull(0).let { data ->
//                if (_listPostPagingAdapters.value?.get(0)?.itemCount!! < 1)
//                if (data == null) {
            postRepository.getPostsSubscriptionForNewsFeed()?.collect {
//                pagingDataNotNull.value!!.add(true)
                _listPostPagingAdapters.value?.get(0)?.submitData(it)
//                    }
//                } else if (refresh){
//                    postRepository.getPostsSubscriptionForNewsFeed()?.collect {
//                        _listPostPagingAdapters.value?.get(0)?.submitData(it)
//                    }
//                }
            }

        }
    }

    private fun getPostsTrends() {
        externalScope.launch(ioDispatcher) {
//            pagingDataNotNull.value!!.elementAtOrNull(1).let { data ->
//                if (data == null) {
            postRepository.getPostsTrendsForNewsFeed()?.collect {
//                pagingDataNotNull.value!!.add(true)
                _listPostPagingAdapters.value?.get(1)?.submitData(it)
            }
//                } else if (refresh){
//            postRepository.getPostsTrendsForNewsFeed()?.collect {
//                _listPostPagingAdapters.value?.get(1)?.submitData(it)
//            }
//                }
        }
    }

    private fun getPostsByFilter(position: Int) {
        externalScope.launch(ioDispatcher) {
            postRepository.getPostByTag(listTitles.value!![position])?.collect {
                _listPostPagingAdapters.value?.get(position)?.submitData(it)
            }
        }
    }


    fun onRefresh() {
        getPostsSubscriptions()
        getPostsTrends()
        for (i in 2 until _listPostPagingAdapters.value?.size!!) {
            getPostsByFilter(i)
        }
    }

}
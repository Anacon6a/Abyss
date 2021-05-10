package com.example.abyss.ui.home

import androidx.lifecycle.*
import androidx.paging.LoadState
import com.example.abyss.adapters.PostNewsFeedPagingAdapter
import com.example.abyss.model.repository.post.PostRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.DKodein
import org.kodein.di.generic.instance

class NewsFeedViewModel(
    private val postRepository: PostRepository,
    private val externalScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    private val kodein: DKodein
) : ViewModel() {

    private val _progressBarLoadingAllPosts = MutableLiveData<Boolean>()
    val progressBarLoadingAllPosts: LiveData<Boolean>
        get() = _progressBarLoadingAllPosts

    //    private val пользовательские категории

    private val _listPostPagingAdapters =
        MutableLiveData<ArrayList<PostNewsFeedPagingAdapter>>().apply { value = arrayListOf() }
    val listPostPagingAdapters: LiveData<ArrayList<PostNewsFeedPagingAdapter>>
    get() = _listPostPagingAdapters

    private val pagingDataNotNull =
        MutableLiveData<ArrayList<Boolean>>().apply { value = arrayListOf() }

    private val _listTitles = arrayListOf("Подписки", "Тренды")
    val listTitles =
        MutableLiveData<ArrayList<String>>().apply { value = _listTitles }

    private val wasInitialization = MutableLiveData<Boolean>().apply { value = false}

   suspend fun initial() {
       if (!wasInitialization.value!!) {
           addAdapters()
       }
    }

    private suspend fun getNumberAdapters() {
        //сначала получить пользовательские категории
        //добавить в _listTitles
//        _listTitles.add("Маникюр")
//        _listTitles.add("Маникюр")
    }

    private fun getAdapter(): PostNewsFeedPagingAdapter {
        return kodein.instance() as PostNewsFeedPagingAdapter
    }

    //    private fun addAdapters(i: Int) {
//        getAdapter().let {
//            listPostPagingAdapters.value!!.add(it)
//        }
//        statusLoading(listPostPagingAdapters.value!![i])
//    }
    private suspend fun addAdapters() {
       externalScope.launch(ioDispatcher) {
            getNumberAdapters()
            for (i in 0 until listTitles.value!!.size) {

                    getAdapter().let {
                        _listPostPagingAdapters.value!!.add(it)
                    }
                    statusLoading(_listPostPagingAdapters.value!![i])
            }
           wasInitialization.postValue(true)
        }.join()
    }

    private fun statusLoading(pagingAdapter: PostNewsFeedPagingAdapter) {
        externalScope.launch {
            pagingAdapter.loadStateFlow.collectLatest { loadState ->
                loadingPosts(loadState.source.refresh is LoadState.Loading)
            }
        }
    }

    private fun loadingPosts(boolean: Boolean) {
        _progressBarLoadingAllPosts.postValue(boolean)
    }

    fun getPosts(position: Int) {
        when (position) {
            0 -> getPostsSubscriptions(false)
            1 -> getPostsTrends(false)
            else -> getPostsByFilter(position, false)
        }
    }

    private fun getPostsSubscriptions(refresh: Boolean) {
        externalScope.launch(ioDispatcher) {
            pagingDataNotNull.value!!.elementAtOrNull(0).let { data ->
                if (data == null) {
                    postRepository.GetPostsSubscriptionForNewsFeed()?.collect {
                        pagingDataNotNull.value!!.add(true)
                        _listPostPagingAdapters.value?.get(0)?.submitData(it)
                    }
                } else if (refresh){
                    postRepository.GetPostsSubscriptionForNewsFeed()?.collect {
                        _listPostPagingAdapters.value?.get(0)?.submitData(it)
                    }
                }
            }

        }
    }

    private fun getPostsTrends(refresh: Boolean) {
        externalScope.launch(ioDispatcher) {
            pagingDataNotNull.value!!.elementAtOrNull(1).let { data ->
                if (data == null) {
                    postRepository.GetPostsTrendsForNewsFeed()?.collect {
                        pagingDataNotNull.value!!.add(true)
                        _listPostPagingAdapters.value?.get(1)?.submitData(it)
                    }
                } else if (refresh){
                    postRepository.GetPostsTrendsForNewsFeed()?.collect {
                        _listPostPagingAdapters.value?.get(1)?.submitData(it)
                    }
                }
            }
        }
    }

    private fun getPostsByFilter(position: Int, refresh: Boolean) {

    }

    fun onRefresh() {
        getPostsSubscriptions(true)
        getPostsTrends(true)
    }

}
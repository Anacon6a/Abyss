package com.example.abyss.ui.profile.profile

import androidx.lifecycle.*
import androidx.paging.LoadState
import com.example.abyss.adapters.profile.ProfileMyPostsPagingAdapter
import com.example.abyss.model.State
import com.example.abyss.model.repository.post.PostRepository
import com.example.abyss.model.repository.user.UserRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class ProfileViewModel(
    private val ioDispatcher: CoroutineDispatcher,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val externalScope: CoroutineScope,
    val postsPagingAdapter: ProfileMyPostsPagingAdapter,
    val savedPostsPagingAdapter: ProfileMyPostsPagingAdapter,
) : ViewModel() {

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String>
        get() = _userName

    private val _profileImageUrl = MutableLiveData<String>()
    val profileImageUrl: LiveData<String>
        get() = _profileImageUrl

    private val _numbersOfSubscribers = MutableLiveData<Int>()
    val numbersOfSubscribers: LiveData<Int>
        get() = _numbersOfSubscribers

    private val _numbersOfSubscriptions = MutableLiveData<Int>()
    val numbersOfSubscriptions: LiveData<Int>
        get() = _numbersOfSubscriptions

    private val _progressBarloadingAllPosts = MutableLiveData<Boolean>()
    val progressBarloadingAllPosts: LiveData<Boolean>
        get() = _progressBarloadingAllPosts

    init {
        statusLoading()
        getPostsUser()
        getSavedPosts()
        listeningForChangesPosts()
        listeningForChangesSavedPosts()
        getUser()
    }

    private fun statusLoading() {
        externalScope.launch()
        {
            postsPagingAdapter.loadStateFlow.collectLatest { loadState ->
                loadingPosts(loadState.source.refresh is LoadState.Loading)
            }
            savedPostsPagingAdapter.loadStateFlow.collectLatest { loadState ->
                loadingPosts(loadState.source.refresh is LoadState.Loading)
            }
        }
    }

    private fun loadingPosts(boolean: Boolean) {
        _progressBarloadingAllPosts.postValue(boolean)
    }

    private fun getPostsUser() {
        externalScope.launch(ioDispatcher) {
            postRepository.getUsersPosts()?.collect {
                postsPagingAdapter.submitData(it)
            }
        }
    }

    private fun getSavedPosts() {
        externalScope.launch(ioDispatcher) {
            postRepository.getSavedPosts()?.collect {
                savedPostsPagingAdapter.submitData(it)
            }
        }
    }

    private fun getUser() {

        externalScope.launch(ioDispatcher) {

            userRepository.getUserByUid().collect { state ->

                when (state) {
                    is State.Success -> {
                        if (userName.value != state.data?.userName!!) {
                            _userName.postValue(state.data.userName!!)
                        }
                        if (profileImageUrl.value != state.data.profileImageUrl!!) {
                            _profileImageUrl.postValue(state.data.profileImageUrl!!)
                        }
                        if (numbersOfSubscribers.value != state.data.numberOfSubscribers) {
                            _numbersOfSubscribers.postValue(state.data.numberOfSubscribers!!)
                        }
                        if (numbersOfSubscriptions.value != state.data.numberOfSubscriptions!!) {
                            _numbersOfSubscriptions.postValue(state.data.numberOfSubscriptions!!)
                        }
                    }
                }
            }
        }

    }

    private fun listeningForChangesPosts() {
        externalScope.launch(ioDispatcher) {
            postRepository.listeningForChangesPosts().collect {
                if (it) {
                    getPostsUser()
                }
            }
        }
    }

    private fun listeningForChangesSavedPosts() {
        externalScope.launch(ioDispatcher) {
            postRepository.listeningForChangesSavedPosts().collect {
                if (it) {
                    getSavedPosts()
                }
            }
        }
    }

    fun refresh() {
        getPostsUser()
        getSavedPosts()
        getUser()
    }
}



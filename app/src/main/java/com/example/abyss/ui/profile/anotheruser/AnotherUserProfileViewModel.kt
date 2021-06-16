package com.example.abyss.ui.profile.anotheruser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import com.example.abyss.adapters.profile.ProfileMyPostsPagingAdapter
import com.example.abyss.model.State
import com.example.abyss.model.repository.post.PostRepository
import com.example.abyss.model.repository.subscription.SubscriptionRepository
import com.example.abyss.model.repository.user.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AnotherUserProfileViewModel(
    private val ioDispatcher: CoroutineDispatcher,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val externalScope: CoroutineScope,
    val postsPagingAdapter: ProfileMyPostsPagingAdapter,
    private val subscriptionRepository: SubscriptionRepository,
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

    private val _stateSubscribe = MutableLiveData<Boolean>()
    val stateSubscribe: LiveData<Boolean>
        get() = _stateSubscribe

    private val userUid = MutableLiveData<String>()

    init {
        statusLoading()
        listeningForChangesPosts()
    }

    fun ininitialization(uid: String) {
        if (userUid.value == null) {
            userUid.value = uid
            getPostsUser()
            getUser()
            subscribeButtonVisibility()
        }
    }

    private fun statusLoading() {
        externalScope.launch()
        {
            postsPagingAdapter.loadStateFlow.collectLatest { loadState ->
                loadingPosts(loadState.source.refresh is LoadState.Loading)
            }
        }
    }

    private fun loadingPosts(boolean: Boolean) {
        _progressBarloadingAllPosts.postValue(boolean)
    }

    private fun getPostsUser() {
        externalScope.launch(ioDispatcher) {
            postRepository.getAnotherUsersPosts(userUid.value!!)?.collect {
                postsPagingAdapter.submitData(it)
            }
        }
    }

    private fun getUser() {
        externalScope.launch(ioDispatcher) {
            userRepository.getAnotherUserByUid(userUid.value!!).collect { state ->
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

    private fun subscribeButtonVisibility() {
        viewModelScope.launch(ioDispatcher) {
            _stateSubscribe.postValue(subscriptionRepository.GetSubscriptionStatus(userUid.value!!))
        }
    }

    fun subscribeToAccount() {
        externalScope.launch(ioDispatcher) {
            stateSubscribe.value?.let {
                if (stateSubscribe.value!!) {
                    _numbersOfSubscribers.postValue(numbersOfSubscribers.value!!.minus(1))
                } else {
                    _numbersOfSubscribers.postValue(numbersOfSubscribers.value!!.plus(1))
                }
                _stateSubscribe.postValue(!stateSubscribe.value!!)
                subscriptionRepository.addOrDeleteSubscription(
                   userUid.value!!
                )
            }
        }
    }

    fun refresh() {
        getPostsUser()
        getUser()
    }
}



package com.example.abyss.ui.profile

import androidx.lifecycle.*
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.example.abyss.adapters.PostProfilePagingAdapter
import com.example.abyss.model.State
import com.example.abyss.model.data.PostData
import com.example.abyss.model.repository.post.PostRepository
import com.example.abyss.model.repository.user.UserRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber

class ProfileViewModel(
    private val ioDispatcher: CoroutineDispatcher,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val externalScope: CoroutineScope,
    val postProfilePagingAdapter: PostProfilePagingAdapter,
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

    private val postsUser = MutableLiveData<PagingData<PostData>>()


//    val getPosts = postRepository.GetPostForProfile()?.cachedIn(externalScope)?.asLiveData()

    init {
        StatusLoading()
        GetPostsUser()
        listeningForChangesPosts()
        GetUser()
    }

    fun StatusLoading() {
        externalScope.launch()
        {
            postProfilePagingAdapter.loadStateFlow.collectLatest { loadState ->
                LoadingPosts(loadState.source.refresh is LoadState.Loading)
            }
        }
    }

    fun LoadingPosts(boolean: Boolean) {
        _progressBarloadingAllPosts.postValue(boolean)
    }

    private fun GetPostsUser() {
        externalScope.launch(ioDispatcher) {
            postRepository.getPostsForProfile()?.collect {
                postsUser.postValue(it)
                postProfilePagingAdapter.submitData(it)
            }
        }
    }

    private fun GetUser() {

        externalScope.launch(ioDispatcher) {

            userRepository.GetUserByUid().collect { state ->

                when (state) {
                    is State.Loading -> {

                    }
                    is State.Success -> {
                        if (userName.value != state.data?.userName!!) {
                            _userName.postValue(state.data.userName!!)
                        }
                        if (profileImageUrl.value != state.data.profileImageUrl!!) {
                            _profileImageUrl.postValue(state.data.profileImageUrl!!)
                        }
                        if (numbersOfSubscribers.value != state.data.numberOfSubscribers){
                            _numbersOfSubscribers.postValue(state.data.numberOfSubscribers!!)
                        }
                        if (numbersOfSubscriptions.value != state.data.numberOfSubscriptions!!){
                            _numbersOfSubscriptions.postValue(state.data.numberOfSubscriptions!!)
                        }
                    }
                    is State.Failed -> {
                        Timber.e("Ошибка получения пользователя: ${state.message}")
                    }
                }
            }
        }

    }

    private fun listeningForChangesPosts() {
        externalScope.launch(ioDispatcher) {
            postRepository.listeningForChangesPosts().collect {
                if (it) {
                    GetPostsUser()
                }
            }
        }
    }

    fun Refresh() {
        GetPostsUser()
        GetUser()
    }
}



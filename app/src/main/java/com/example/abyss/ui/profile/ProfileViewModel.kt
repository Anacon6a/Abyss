package com.example.abyss.ui.profile

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.example.abyss.model.State
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
) : ViewModel() {

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String>
        get() = _userName

    private val _profileImageUrl = MutableLiveData<String>()
    val profileImageUrl: LiveData<String>
        get() = _profileImageUrl

    private val _loadingAddPost = MutableLiveData<Boolean>()
    val loadingAddPost: LiveData<Boolean>
        get() = _loadingAddPost

    private val _progressBarloadingAllPosts = MutableLiveData<Boolean>()
    val progressBarloadingAllPosts: LiveData<Boolean>
        get() = _progressBarloadingAllPosts

//    val flow = Pager(
//        PagingConfig(
//            initialLoadSize = 40,
//            pageSize = 40,
//            prefetchDistance = 40
//        )
//    ) {
//        postForProfileFirestorePagingSource
//    }.flow.cachedIn(externalScope)

    init {
        GetUser()
    }

    fun LoadingPosts(boolean: Boolean) {
        _progressBarloadingAllPosts.postValue(boolean)
    }

    fun LoadingPost() {

    }

    val getPosts = postRepository.GetPostForProfile()?.cachedIn(viewModelScope)?.asLiveData()


    fun GetUser() {

        externalScope.launch(ioDispatcher) {

            userRepository.GetUserByUid().collect { state ->

                when (state) {
                    is State.Loading -> {

                    }
                    is State.Success -> {
                        _userName.postValue(state.data?.userName!!)
                        _profileImageUrl.postValue(state.data?.profileImageUrl!!)
                    }
                    is State.Failed -> {
                        Timber.e("Ошибка получения пользователя: ${state.message}")
                    }
                }
            }
        }

    }

}



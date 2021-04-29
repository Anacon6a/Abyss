package com.example.abyss.ui.profile

import android.net.sip.SipSession
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.abyss.adapters.PostPagingAdapter
import com.example.abyss.model.State
import com.example.abyss.model.pagingsource.PostForProfileFirestorePagingSource
import com.example.abyss.model.repository.auth.AuthRepository
import com.example.abyss.model.repository.post.PostRepository
import com.example.abyss.model.repository.user.UserRepository
import com.google.firebase.firestore.Query
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.lang.Exception

class ProfileViewModel(
    private val ioDispatcher: CoroutineDispatcher,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val externalScope: CoroutineScope,
) : ViewModel() {

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String>
        get() = _userName

    private val _postImageUrl = MutableLiveData<String>()
    val postImageUrl: LiveData<String>
        get() = _postImageUrl

    private val _avatarImageUrl = MutableLiveData<String>()
    val avatarImageUrl: LiveData<String>
        get() = _avatarImageUrl

    private val _loadingAddPost = MutableLiveData<Boolean>()
    val loadingAddPost: LiveData<Boolean>
        get() = _loadingAddPost

    private val _loadingAllPosts = MutableLiveData<Boolean>()
    val loadingAllPosts: LiveData<Boolean>
        get() = _loadingAllPosts

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
        _loadingAllPosts.postValue(boolean)
    }

    fun LoadingPost() {

    }

    val getPosts = postRepository.GetPostForProfile()?.cachedIn(viewModelScope)?.asLiveData()


    fun GetUser() {

        externalScope.launch(ioDispatcher) {

            userRepository.GetUserById().collect { state ->

                when (state) {
                    is State.Loading -> {

                    }
                    is State.Success -> {
                        _userName.postValue(state.data?.userName!!)
                    }
                    is State.Failed -> {
                        Timber.e("Ошибка получения пользователя: ${state.message}")
                    }
                }
            }
        }

    }

}



package com.example.abyss.ui.profile

import android.net.sip.SipSession
import androidx.lifecycle.*
import com.example.abyss.model.State
import com.example.abyss.model.repository.post.PostRepository
import com.example.abyss.model.repository.user.UserRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.lang.Exception

class ProfileViewModel(
    private val ioDispatcher: CoroutineDispatcher,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
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

    init {
        GetUser()
        GetPost()
    }

    private fun GetPost() {
        viewModelScope.launch(ioDispatcher) {
            var p = postRepository.GetPostForProfile()
        }
    }

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



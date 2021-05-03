package com.example.abyss.ui.posts.post

import android.widget.ImageView
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abyss.model.State
import com.example.abyss.model.data.PostData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import timber.log.Timber

class PostViewModel(
    private val externalScope: CoroutineScope
) : ViewModel() {

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String>
        get() = _userName

    private val _numberOfSubscribers = MutableLiveData<String>()
    val numberOfSubscribers: LiveData<String>
        get() = _numberOfSubscribers

    private val _textSubscribe = MutableLiveData<String>()
    val textSubscribe: LiveData<String>
        get() = _textSubscribe

    private val _visibilityTextPost = MutableLiveData<Boolean>()
    val visibilityTextPost: LiveData<Boolean>
        get() = _visibilityTextPost

    private val _visibilitySubscribe = MutableLiveData<Boolean>()
    val visibilitySubscribe: LiveData<Boolean>
        get() = _visibilitySubscribe

    private val _stateSubscribe = MutableLiveData<Boolean>()
    val stateSubscribe: LiveData<Boolean>
        get() = _stateSubscribe

    private val _stateLike = MutableLiveData<Boolean>()
    val stateLike: LiveData<Boolean>
        get() = _stateLike

    private val _numberOfLikes = MutableLiveData<String>()
    val numberOfLikes: LiveData<String>
        get() = _numberOfLikes


    private val _numberOfViews = MutableLiveData<String>()
    val numberOfViews: LiveData<String>
        get() = _numberOfViews

    private val _postImageUrl = MutableLiveData<String>()
    val postImageUrl: LiveData<String>
        get() = _postImageUrl

    private lateinit var postImageView: ImageView

    var postData = ObservableField<PostData>()

    init {
//        _visibilitySubscribe.value = true
//        _stateSubscribe.value = true
//        _stateLike.value = true
//        _visibilityTextPost.value = true
    }

    fun InsertPost() {

        externalScope.launch {
            val deferreds = listOf(
                async {
//                    if ()
//                    _postImageUrl.postValue(post.imageUrl!!)
                },
                async {

                },
                async {

                }
            )
            deferreds.awaitAll()
        }


//        val deferreds = listOf(
//            async { fetchDoc(1) },
//            async { fetchDoc(2) }
//        )
//        deferreds.awaitAll()
    }



//    fun GetUser() {
//
//        externalScope.launch(ioDispatcher) {
//
//            userRepository.GetUserById().collect { state ->
//
//                when (state) {
//                    is State.Loading -> {
//
//                    }
//                    is State.Success -> {
//                        _userName.postValue(state.data?.userName!!)
//                        _profileImageUrl.postValue(state.data?.profileImageUrl!!)
//                    }
//                    is State.Failed -> {
//                        Timber.e("Ошибка получения пользователя: ${state.message}")
//                    }
//                }
//            }
//        }
//
//    }
    fun goToUserProfile() {

    }

    fun subscribeToAccount() {

    }

    fun savePost() {

    }
}
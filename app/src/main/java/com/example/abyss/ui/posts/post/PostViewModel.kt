package com.example.abyss.ui.posts.post

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abyss.model.data.PostData
import com.example.abyss.model.data.UserData
import com.example.abyss.model.repository.auth.AuthRepository
import com.example.abyss.model.repository.like.LikeRepository
import com.example.abyss.model.repository.post.PostRepository
import com.example.abyss.model.repository.user.UserRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class PostViewModel(
    private val externalScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val likeRepository: LikeRepository
) : ViewModel() {

//    private val _userName = MutableLiveData<String>()
//    val userName: LiveData<String>
//        get() = _userName

//    private val _profileImageUrl = MutableLiveData<String>()
//    val profileImageUrl: LiveData<String>
//        get() = _profileImageUrl

    private val _numberOfSubscribers = MutableLiveData<String>()
    val numberOfSubscribers: LiveData<String>
        get() = _numberOfSubscribers

//    private val _textSubscribe = MutableLiveData<String>()
//    val textSubscribe: LiveData<String>
//        get() = _textSubscribe

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

//    private val _postImageUrl = MutableLiveData<String>()
//    val postImageUrl: LiveData<String>
//        get() = _postImageUrl

    private val _userData = MutableLiveData<UserData>()
    val userData: LiveData<UserData>
        get() = _userData

//    private lateinit var postImageView: ImageView

    var postData = ObservableField<PostData>()

    private var myUid: String? = null

    init {
//        _visibilitySubscribe.value = true
//        _stateSubscribe.value = true
//        _stateLike.value = true
//        _visibilityTextPost.value = true
    }

    // первоначальное получение и подписка на изменения
    fun InsertPost() {

        viewModelScope.launch(ioDispatcher) {
            val deferreds = listOf(
                async {
                    GetUserContentProvider()
                },
                async {
                    subscribeButtonVisibility()
                },
                async {
                    GetNumbersOfLikes()
                },
                async {
                    GetStateLike()
                }
            )
            deferreds.awaitAll()
        }

    }

    private suspend fun GetUserContentProvider() {
        userRepository.GetUserContentProviderByUid(postData.get()?.uid!!).collect {
            _userData.postValue(it)
        }
    }

    private suspend fun subscribeButtonVisibility() {
        if (myUid == null) {
            myUid = authRepository.GetUid()
        }
        _visibilitySubscribe.postValue(myUid != postData.get()?.uid)
        ///////////
    }

    private suspend fun GetNumbersOfLikes() {
        likeRepository.GetNumberOfLikes(postData.get()!!.id!!, postData.get()!!.uid!!)
            .collect { numbers ->
                numbers?.let {
                    if (it != 0) {
                        _numberOfLikes.postValue(numbers.toString())
                    }
                }
            }
    }

    private suspend fun GetStateLike() {
        _stateLike.postValue(likeRepository.GetLikeStatus(postData.get()!!.id!!))
    }

    fun ClickLike() {
        externalScope.launch(ioDispatcher) {
            if (stateLike.value == true) {
                likeRepository.RemoveLike(postData.get()!!.id!!).collect {
                    if (it != 0) {
                        _numberOfLikes.postValue(it.toString())
                    } else {
                        _numberOfLikes.postValue("")
                    }
                    _stateLike.postValue(false)
                }
            } else if(stateLike.value == false){
                likeRepository.AddLike(postData.get()!!.id!!).collect {
                    if (it != 0) {
                        _numberOfLikes.postValue(it.toString())
                    }
                    _stateLike.postValue(true)
                }
            }
        }
    }


    fun goToUserProfile() {

    }

    fun subscribeToAccount() {

    }

    fun savePost() {

    }
}
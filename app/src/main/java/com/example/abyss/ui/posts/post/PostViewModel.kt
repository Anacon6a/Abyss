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
import com.example.abyss.model.repository.views.ViewsRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class PostViewModel(
    private val externalScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val likeRepository: LikeRepository,
    private val viewsRepository: ViewsRepository
) : ViewModel() {

    private val _numberOfSubscribers = MutableLiveData<String>()
    val numberOfSubscribers: LiveData<String>
        get() = _numberOfSubscribers

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

    private val _numberOfLikes = MutableLiveData<Int>()
    val numberOfLikes: LiveData<Int>
        get() = _numberOfLikes


    private val _numberOfViews = MutableLiveData<Int>()
    val numberOfViews: LiveData<Int>
        get() = _numberOfViews

    private val _userData = MutableLiveData<UserData>()
    val userData: LiveData<UserData>
        get() = _userData

    var postData = ObservableField<PostData>()

    private var myUid: String? = null

    fun Insert() {

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
                },
                async {
                    AddAndGetViews()
                },
                async {
                    if (postData.get()!!.text != null){
                        _visibilityTextPost.postValue(true)
                    } else {
                        _visibilityTextPost.postValue(false)
                    }
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
                        _numberOfLikes.postValue(numbers)
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
                        _numberOfLikes.postValue(it)
                    } else {
                        _numberOfLikes.postValue(0)
                    }
                    _stateLike.postValue(false)
                }
            } else if (stateLike.value == false) {
                likeRepository.AddLike(postData.get()!!.id!!).collect {
                    if (it != 0) {
                        _numberOfLikes.postValue(it)
                    }
                    _stateLike.postValue(true)
                }
            }
        }
    }

    private suspend fun AddAndGetViews() {
       _numberOfViews.postValue( viewsRepository.AddViewsAndGetNumberOfViews(postData.get()!!.id!!, postData.get()!!.uid!!))
    }

    fun goToUserProfile() {

    }

    fun subscribeToAccount() {

    }

    fun savePost() {

    }
}
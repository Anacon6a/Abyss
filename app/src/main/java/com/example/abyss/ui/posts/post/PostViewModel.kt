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
            GetUserContentProvider()
            subscribeButtonVisibility()
            GetNumbersOfLikes()
            GetStateLike()
            AddAndGetViews()
            textPostVisibility()
        }

    }

    private fun GetUserContentProvider() {
        externalScope.launch(ioDispatcher) {
            userRepository.GetUserContentProviderByUid(postData.get()?.uid!!).collect {
                _userData.postValue(it)
            }
        }
    }

    private fun subscribeButtonVisibility() {
        externalScope.launch(ioDispatcher) {
            if (myUid == null) {
                myUid = authRepository.GetUid()
            }
            _visibilitySubscribe.postValue(myUid != postData.get()?.uid)
        }
        ///////////
    }

    private fun GetNumbersOfLikes() {
        externalScope.launch(ioDispatcher) {
            likeRepository.GetNumberOfLikes(postData.get()!!.id!!, postData.get()!!.uid!!)
                .collect { numbers ->
                    numbers?.let {
                        if (it != 0) {
                            _numberOfLikes.postValue(numbers)
                        }
                    }
                }
        }
    }

    private fun GetStateLike() {
        externalScope.launch(ioDispatcher) {
            _stateLike.postValue(
                likeRepository.GetLikeStatus(
                    postData.get()!!.id!!,
                    postData.get()!!.uid!!
                )
            )
        }
    }

    fun ClickLike() {
        externalScope.launch(ioDispatcher) {
            stateLike.value?.let { state ->
                likeRepository.AddViewsAndGetNumberOfLikesAndStatus(
                    postData.get()!!.id!!, postData.get()!!.uid!!, state
                ).collect {
                    _numberOfLikes.postValue(it.first)
                    _stateLike.postValue(it.second)
                }

            }
        }
    }

    private fun AddAndGetViews() {
        externalScope.launch(ioDispatcher) {

            viewsRepository.AddViewsAndGetNumberOfLikesAndStatus(
                postData.get()!!.id!!,
                postData.get()!!.uid!!
            ).collect {
                _numberOfViews.postValue(it)
            }
        }
    }

    private fun textPostVisibility() {
        externalScope.launch(ioDispatcher) {
            if (postData.get()!!.text != null) {
                _visibilityTextPost.postValue(true)
            } else {
                _visibilityTextPost.postValue(false)
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

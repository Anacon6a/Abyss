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
import com.example.abyss.model.repository.subscription.SubscriptionRepository
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
    private val viewsRepository: ViewsRepository,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _numberOfSubscribers = MutableLiveData<Int?>()
    val numberOfSubscribers: LiveData<Int?>
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

        GetUserContentProvider()
        subscribeButtonVisibility()
        GetNumbersOfLikes()
        GetStateLike()
        AddAndGetViews()
        textPostVisibility()
    }
//пользователь, выложевший пост
    private fun GetUserContentProvider() {
        viewModelScope.launch(ioDispatcher) {
            userRepository.GetUserContentProviderByUid(postData.get()?.uid!!).collect {
                _userData.postValue(it)
                _numberOfSubscribers.postValue(it!!.numberOfSubscribers)
            }
        }
    }
// отображение кнопки подписки
    private fun subscribeButtonVisibility() {
        viewModelScope.launch(ioDispatcher) {
            if (myUid == null) {
                myUid = authRepository.GetUid()
            }
            _stateSubscribe.postValue(subscriptionRepository.GetSubscriptionStatus(postData.get()?.uid!!))
            _visibilitySubscribe.postValue(myUid != postData.get()?.uid)
        }
    }
// получение количества лайков. ИЗМЕНИТЬ!!
    private fun GetNumbersOfLikes() {
        viewModelScope.launch(ioDispatcher) {
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
// поставлен ли лайк у пользователя
    private fun GetStateLike() {
        viewModelScope.launch(ioDispatcher) {
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
                _stateLike.postValue(!stateLike.value!!)
                likeRepository.AddLikeAndGetNumberOfLikesAndStatus(
                    postData.get()!!.id!!, postData.get()!!.uid!!, state
                ).collect {
                    _numberOfLikes.postValue(it.first)
                }

            }
        }
    }
// добавление просмотра, если пользователь не просматривал
    private fun AddAndGetViews() {
        externalScope.launch(ioDispatcher) {

            viewsRepository.AddViewsAndGetNumber(
                postData.get()!!.id!!,
                postData.get()!!.uid!!
            ).collect {
                _numberOfViews.postValue(it)
            }
        }
    }

    private fun textPostVisibility() {
        viewModelScope.launch(ioDispatcher) {
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
        externalScope.launch(ioDispatcher) {
            stateSubscribe.value?.let {
                _stateSubscribe.postValue(!stateSubscribe.value!!)
                subscriptionRepository.AddSubscriptionAndGetNumberOfSubscribersAndStatus(
                    postData.get()!!.uid!!,
                    stateSubscribe.value!!
                )
                    .collect {
                        _numberOfSubscribers.postValue(it.first)
                    }
            }
        }
    }

    fun savePost() {

    }
}

package com.example.abyss.ui.posts.post

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

    private val _profileImage = MutableLiveData<String>()
    val profileImage: LiveData<String>
        get() = _profileImage

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String>
        get() = _userName

    private val _numberOfSubscribers = MutableLiveData<Int?>()
    val numberOfSubscribers: LiveData<Int?>
        get() = _numberOfSubscribers

    private val _postImage = MutableLiveData<String>()
    val postImage: LiveData<String>
        get() = _postImage

    private val _postText = MutableLiveData<String?>()
    val postText: LiveData<String?>
        get() = _postText

    private val _numberOfViews = MutableLiveData<Int>()
    val numberOfViews: LiveData<Int>
        get() = _numberOfViews

    private val _numberOfLikes = MutableLiveData<Int>()
    val numberOfLikes: LiveData<Int>
        get() = _numberOfLikes

    private val _visibilitySubscribe = MutableLiveData<Boolean>()
    val visibilitySubscribe: LiveData<Boolean>
        get() = _visibilitySubscribe

    private val _visibilityMoreBtn = MutableLiveData<Boolean>()
    val visibilityMoreBtn: LiveData<Boolean>
        get() = _visibilityMoreBtn

    private val _stateSubscribe = MutableLiveData<Boolean>()
    val stateSubscribe: LiveData<Boolean>
        get() = _stateSubscribe

    private val _stateLike = MutableLiveData<Boolean>()
    val stateLike: LiveData<Boolean>
        get() = _stateLike

    private val _userData = MutableLiveData<UserData>()
    val userData: LiveData<UserData>
        get() = _userData

    val postData = MutableLiveData<PostData>()

    private var myUid: String? = null

   suspend fun insertPost(post: PostData) {
        if (postData.value == null) {
            postData.value = post
            _postImage.value = post.imageUrl!!
            postRepository.getPostById(post.id!!, post.uid!!).collect {
                if (it != null) {
                    if (postImage.value != it.imageUrl)
                    {
                        _postImage.postValue(it.imageUrl!!)
                    }
                    _postText.value = it.text
                    _numberOfLikes.value = it.numberOfLikes!!
                    _numberOfViews.value = it.numberOfViews!!
                }
            }
        }
    }

    fun ininitialization() {
        if (userData.value == null) {
            getUserContentProvider()
            subscribeButtonVisibility()
            getStateLike()
            addAndGetViews()
        }
    }

    //пользователь, выложивший пост
    private fun getUserContentProvider() {
        viewModelScope.launch(ioDispatcher) {
            userRepository.GetUserContentProviderByUid(postData.value!!.uid!!).collect {
                _userData.postValue(it)
                _userName.postValue(it!!.userName!!)
                _profileImage.postValue(it.profileImageUrl!!)
                _numberOfSubscribers.postValue(it.numberOfSubscribers)
            }
        }
    }

    // отображение кнопки подписки
    private fun subscribeButtonVisibility() {
        viewModelScope.launch(ioDispatcher) {
            if (myUid == null) {
                myUid = authRepository.GetUid()
            }
            _stateSubscribe.postValue(subscriptionRepository.GetSubscriptionStatus(postData.value?.uid!!))
            _visibilitySubscribe.postValue(myUid != postData.value!!.uid)
            _visibilityMoreBtn.postValue(myUid == postData.value!!.uid)
        }
    }

    // поставлен ли лайк у пользователя
    private fun getStateLike() {
        viewModelScope.launch(ioDispatcher) {
            _stateLike.postValue(
                likeRepository.GetLikeStatus(
                    postData.value!!.id!!,
                    postData.value!!.uid!!
                )
            )
        }
    }

    fun ClickLike() {
        externalScope.launch(ioDispatcher) {
            stateLike.value?.let { state ->
                if (stateLike.value!!) {
                    _numberOfLikes.postValue(numberOfLikes.value!!.minus(1))
                } else {
                    _numberOfLikes.postValue(numberOfLikes.value!!.plus(1))
                }
                _stateLike.postValue(!stateLike.value!!)

                likeRepository.AddLikeAndGetNumberOfLikesAndStatus(
                    postData.value!!.id!!, postData.value!!.uid!!
                )
            }
        }
    }

    // добавление просмотра, если пользователь не просматривал
    private fun addAndGetViews() {
        externalScope.launch(ioDispatcher) {

            viewsRepository.AddViewsAndGetNumber(
                postData.value!!.id!!,
                postData.value!!.uid!!
            ).collect {
                _numberOfViews.postValue(it)
            }
        }
    }

    fun goToUserProfile() {

    }

    fun subscribeToAccount() {
        externalScope.launch(ioDispatcher) {
            userData.value?.let {
                stateSubscribe.value?.let {
                    if (stateSubscribe.value!!) {
                        _numberOfSubscribers.postValue(numberOfSubscribers.value!!.minus(1))
                    } else {
                        _numberOfSubscribers.postValue(numberOfSubscribers.value!!.plus(1))
                    }
                    _stateSubscribe.postValue(!stateSubscribe.value!!)
                    subscriptionRepository.AddSubscriptionAndGetNumberOfSubscribersAndStatus(
                        postData.value!!.uid!!
                    )
                }
            }
        }
    }

    fun savePost() {

    }
}

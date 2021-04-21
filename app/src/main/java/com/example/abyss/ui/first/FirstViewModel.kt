package com.example.abyss.ui.first

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abyss.model.repository.auth.AuthRepository
import com.example.abyss.model.repository.user.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class FirstViewModel(
    private var authRepository: AuthRepository,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _eventGoToAuth = MutableLiveData<Boolean>()
    val eventGoToAuth: LiveData<Boolean>
        get() = _eventGoToAuth

    private val _eventGoToHome = MutableLiveData<Boolean>()
    val eventGoToHome: LiveData<Boolean>
        get() = _eventGoToHome

    init {
        viewModelScope.launch(ioDispatcher) {

            if (authRepository.currentUser() != null) {
                _eventGoToHome.postValue(true)
            } else {
                _eventGoToAuth.postValue(true)
            }
        }
    }

}
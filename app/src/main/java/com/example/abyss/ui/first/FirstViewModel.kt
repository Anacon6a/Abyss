package com.example.abyss.ui.first

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abyss.model.repository.UserRepository

class FirstViewModel(
    private var repository: UserRepository
): ViewModel() {

    private val _eventGoToAuth = MutableLiveData<Boolean>()
    val eventGoToAuth: LiveData<Boolean>
    get() = _eventGoToAuth

    private val _eventGoToHome = MutableLiveData<Boolean>()
    val eventGoToHome: LiveData<Boolean>
        get() = _eventGoToHome

    init {

//        _eventGoToAuth.value = true
        if (repository.currentUser() != null)
        {
            _eventGoToHome.value = true
        }
        else
        {
            _eventGoToAuth.value = true
        }

    }

}
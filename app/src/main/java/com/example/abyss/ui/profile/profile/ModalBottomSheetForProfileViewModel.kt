package com.example.abyss.ui.profile.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abyss.model.repository.auth.AuthRepository
import kotlinx.coroutines.launch

class ModalBottomSheetForProfileViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _eventLogout = MutableLiveData<Boolean>()
    val eventLogout: LiveData<Boolean>
        get() = _eventLogout

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _eventLogout.postValue(true)
        }
    }
}
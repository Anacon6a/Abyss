package com.example.abyss.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abyss.model.repository.auth.AuthRepository
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val ioDispatcher: CoroutineDispatcher,

    ) : ViewModel() {

    var email: String? = null
        set(value) {
            field = value?.trim()
            validateInput()
        }

    var password: String? = null
        set(value) {
            field = value?.trim()
            validateInput()
        }

    private val _errorString = MutableLiveData<String>()
    val errorString: LiveData<String>
        get() = _errorString

    private val _progressBar = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _progressBar

    private val _buttonEnabled = MutableLiveData<Boolean>()
    val buttonEnabled: LiveData<Boolean>
        get() = _buttonEnabled

    private val _viewEnabled = MutableLiveData<Boolean>()
    val viewEnabled: LiveData<Boolean>
        get() = _viewEnabled

    private val _eventLoginCompleted = MutableLiveData<Boolean>()
    val eventLoginCompleted: LiveData<Boolean>
        get() = _eventLoginCompleted

    private val _eventGoToRegistration = MutableLiveData<Boolean>()
    val eventGoToRegistration: LiveData<Boolean>
        get() = _eventGoToRegistration

    private fun validateInput() {
        _buttonEnabled.value = !(email.isNullOrEmpty() || password.isNullOrEmpty())
    }


    init {
        _viewEnabled.value = true
    }

    fun onGoToRegistration() {
        _eventGoToRegistration.value = true
    }

    fun onLogin() {

        viewModelScope.launch(ioDispatcher) {
            loading(true)

            var request = authRepository.login(email!!, password!!)
            if (request != "") {
                onFailureLogin(request)
            } else {
                onSuccessLogin()
            }
            loading(false)
        }

    }

    private fun onSuccessLogin() {
        _eventLoginCompleted.postValue(true)
    }

    private fun onFailureLogin(message: String) {
        _errorString.postValue(message)
    }

    private fun loading(boolean: Boolean) {
        _progressBar.postValue(boolean)
        _buttonEnabled.postValue(!boolean)
        _viewEnabled.postValue(!boolean)
    }


}


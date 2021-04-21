package com.example.abyss.ui.auth.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abyss.model.data.entity.UserData
import com.example.abyss.model.repository.auth.AuthRepository
import com.example.abyss.model.repository.user.UserRepository
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    var username: String? = null
        set(value) {
            field = value?.trim()
            validateInput()
        }

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

    private val _eventRegistrationCompleted = MutableLiveData<Boolean>()
    val eventRegistrationCompleted: LiveData<Boolean>
        get() = _eventRegistrationCompleted

    private val _eventGoToLogin = MutableLiveData<Boolean>()
    val eventGoToLogin: LiveData<Boolean>
        get() = _eventGoToLogin

    private fun validateInput() {
        _buttonEnabled.value =
            !(username.isNullOrEmpty() || email.isNullOrEmpty() || password.isNullOrEmpty())
    }

    private val disposables = CompositeDisposable()

    init {
        _eventRegistrationCompleted.value = false
        _eventGoToLogin.value = false
        _viewEnabled.value = true
    }

    fun onGoToLogin() {
        _eventGoToLogin.value = true
    }

    fun onRegistration() {

       loading(true)

        viewModelScope.launch(ioDispatcher) {

            var request = authRepository.register(email!!, password!!)

            if (request != "") {

                onFailureRegistration(request)
            } else {

                val user = UserData(username!!, email!!, "")
               userRepository.CreateUser(user)
                onSuccessRegistration()
            }
        }

        loading(false)
    }

    private fun onSuccessRegistration() {

        _eventRegistrationCompleted.postValue(true)
    }

    private fun onFailureRegistration(message: String) {

        _errorString.postValue(message)
    }

    private fun loading(boolean: Boolean) {
        _progressBar.value = boolean
        _buttonEnabled.value = !boolean
        _viewEnabled.value = !boolean
    }

}
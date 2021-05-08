package com.example.abyss.ui.auth.registration

import android.net.Uri
import androidx.lifecycle.*
import com.example.abyss.model.data.UserData
import com.example.abyss.model.repository.auth.AuthRepository
import com.example.abyss.model.repository.user.UserRepository
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

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

    private val _userImageUrl = MutableLiveData<Uri>()
    val userImageUrl: LiveData<Uri>
        get() = _userImageUrl

    private val _eventImageSelection = MutableLiveData<Boolean>()
    val eventImageSelection: LiveData<Boolean>
        get() = _eventImageSelection


    private fun validateInput() {
        _buttonEnabled.value =
            !(username.isNullOrEmpty() || email.isNullOrEmpty() || password.isNullOrEmpty())
    }

    init {
        _viewEnabled.value = true
    }

    fun imageSelection() {
        _eventImageSelection.value = true
    }

    fun endEventImageSelection() {
        _eventImageSelection.value = false
    }

    fun onGoToLogin() {
        _eventGoToLogin.value = true
    }

    fun onRegistration() {

        viewModelScope.launch(ioDispatcher) {

            loading(true)

            val request = authRepository.register(email!!, password!!)

            if (request != "") {
                onFailureRegistration(request)
            } else {

                if (userImageUrl.value != null) {
                   userRepository.AddProfileImageInStorage(userImageUrl.value!!).collect {
                      AddUser(it)
                  }
                } else {
                    AddUser("")
                }
                Timber.i("Сюда не придем")
            }
        }
    }

    private fun AddUser(url: String){
        viewModelScope.launch(ioDispatcher) {
            val user = UserData(username!!, email!!, url)
            userRepository.CreateUser(user)
            onSuccessRegistration()
            loading(false)
        }
    }

    private fun onSuccessRegistration() {
        _eventRegistrationCompleted.postValue(true)
    }

    private fun onFailureRegistration(message: String) {
        _errorString.postValue(message)
        loading(false)
    }

    private fun loading(boolean: Boolean) {
        _progressBar.postValue(boolean)
        _buttonEnabled.postValue(!boolean)
        _viewEnabled.postValue(!boolean)
    }

    fun onActivityResult(requestCode: Int, uri: Uri?) {
        if (uri != null) {
            viewModelScope.launch {
                _userImageUrl.postValue(uri!!)
            }
        }
    }
}
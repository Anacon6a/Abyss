package com.example.abyss.ui.auth.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abyss.data.repositories.AuthRepository
import com.example.abyss.data.repositories.UserRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class RegistrationViewModel(
    private val repository: AuthRepository
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

    private val  _viewEnabled = MutableLiveData<Boolean>()
    val  viewEnabled: LiveData<Boolean>
        get() = _viewEnabled

    private val _eventRegistrationCompleted = MutableLiveData<Boolean>()
    val eventRegistrationCompleted: LiveData<Boolean>
        get() = _eventRegistrationCompleted

    private val _eventGoToLogin = MutableLiveData<Boolean>()
    val eventGoToLogin: LiveData<Boolean>
        get() = _eventGoToLogin

    private fun validateInput() {
        _buttonEnabled.value = !(username.isNullOrEmpty() || email.isNullOrEmpty() || password.isNullOrEmpty())
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

         onStartLoading()

         val disposable = repository.register(email!!, password!!)
             .subscribeOn(Schedulers.io())
             .observeOn( AndroidSchedulers.mainThread())
             .subscribe({

                 onSuccessLoading()
             }, {

                 onFailureLoading(it.message!!)
             })
         disposables.add(disposable)
    }

    private fun onStartLoading() {
        loading(true)
    }

    private fun onSuccessLoading() {
        loading(false)
        _eventRegistrationCompleted.value = true
    }

    private fun onFailureLoading(message: String) {
        loading(false)
        _errorString.value = message
    }

    private fun loading(boolean: Boolean) {
        _progressBar.value = boolean
        _buttonEnabled.value = !boolean
        _viewEnabled.value = !boolean
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }


}
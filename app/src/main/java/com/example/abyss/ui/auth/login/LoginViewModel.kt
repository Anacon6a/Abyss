package com.example.abyss.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abyss.data.repositories.AuthRepository
import com.example.abyss.data.repositories.UserRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class LoginViewModel(
    private val repository: AuthRepository
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

    private val  _viewEnabled = MutableLiveData<Boolean>()
    val  viewEnabled: LiveData<Boolean>
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

    private val disposables = CompositeDisposable()

//    val user by lazy {
//        repository.currentUser()
//    }

    init {
        _eventLoginCompleted.value = false
        _eventGoToRegistration.value = false
        _viewEnabled.value = true

    }

     fun onGoToRegistration() {
        _eventGoToRegistration.value = true
    }

     fun onLogin() {

         onStartLoading()

          val disposable = repository.login(email!!, password!!)
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
        _eventLoginCompleted.value = true
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


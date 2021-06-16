package com.example.abyss.ui.profile.editprofile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abyss.model.State
import com.example.abyss.model.data.UserData
import com.example.abyss.model.repository.auth.AuthRepository
import com.example.abyss.model.repository.user.UserRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _userImageUri = MutableLiveData<Uri?>()
    val userImageUri: LiveData<Uri?>
        get() = _userImageUri

    private val _userImageUrl = MutableLiveData<String>()
    val userImageUrl: LiveData<String>
        get() = _userImageUrl

    val userName = MutableLiveData<String>()

    val userEmail = MutableLiveData<String>()

    var userPassword: String? = null
        set(value) {
            field = value?.trim()
            checkingIfItIsEmpty()
        }
    var newUserPassword: String? = null
        set(value) {
            field = value?.trim()
            checkingIfItIsEmpty()
        }

    private fun checkingIfItIsEmpty() {
        _buttonPasswordEnabled.value =
            !(userPassword.isNullOrEmpty() || newUserPassword.isNullOrEmpty())
    }

    private val _viewEnabled = MutableLiveData<Boolean>()
    val viewEnabled: LiveData<Boolean>
        get() = _viewEnabled

    private val _buttonPasswordEnabled = MutableLiveData<Boolean>()
    val buttonPasswordEnabled: LiveData<Boolean>
        get() = _buttonPasswordEnabled

    private val _buttonNameEnabled = MutableLiveData<Boolean>()
    val buttonNameEnabled: LiveData<Boolean>
        get() = _buttonNameEnabled

    private val _buttonEmailEnabled = MutableLiveData<Boolean>()
    val buttonEmailEnabled: LiveData<Boolean>
        get() = _buttonEmailEnabled

    private val _loadingEdit = MutableLiveData<Boolean>()
    val loadingEdit: LiveData<Boolean>
        get() = _loadingEdit

    private val _emailErrorString = MutableLiveData<String>()
    val emailErrorString: LiveData<String>
        get() = _emailErrorString

    private val _passwordErrorString = MutableLiveData<String>()
    val passwordErrorString: LiveData<String>
        get() = _passwordErrorString

    private val userData = MutableLiveData<UserData>()

    init {
        _viewEnabled.value = true
        _buttonPasswordEnabled.value = false
        getUser()
    }

    fun getUser() {
        viewModelScope.launch {
            userRepository.getUserByUid().collect { state ->
                when (state) {
                    is State.Success -> {
                        userData.postValue(state.data!!)

                        if (userData.value != state.data) {
                            userData.postValue(state.data!!)
                        }
                        if (userName.value != state.data?.userName!!) {
                            userName.postValue(state.data.userName!!)
                        }
                        if (userImageUrl.value != state.data.profileImageUrl!!) {
                            state.data.profileImageUrl?.let {
                                _userImageUrl.postValue(state.data.profileImageUrl!!)
                            }
                        }
                        if (userEmail.value != state.data.email!!) {
                            userEmail.postValue(state.data.email!!)
                        }
                    }
                }
            }
        }
    }

    fun onActivityResult(requestCode: Int, uri: Uri?) {
        if (uri != null) {
            viewModelScope.launch {
                _userImageUri.postValue(uri!!)
            }
        }
    }

    fun onImageSave() {
        viewModelScope.launch {
            loading(true)
            if (userImageUri.value != null) {
                userRepository.updateProfileImage(userImageUri.value!!)
                _userImageUri.postValue(null)
            }
            loading(false)
        }
    }

    fun onNameUpdate() {
        viewModelScope.launch {
            loading(true)
            if (userName.value != userData.value?.userName) {
                userRepository.updateUserName(userName.value!!)
            }
            loading(false)
        }
    }

    fun onEmailUpdate() {
        viewModelScope.launch {
            loading(true)
            if (userEmail.value != userData.value?.email) {
                _emailErrorString.postValue(authRepository.updateEmail(userEmail.value!!))
                if (_emailErrorString.value.isNullOrEmpty()) {
                    userRepository.updateUserEmail(userEmail.value!!)
                }
            }
            loading(false)
        }
    }

    fun onPasswordUpdate() {
        viewModelScope.launch {
            loading(true)
            _passwordErrorString.postValue(authRepository.updatePassword(userPassword!!, newUserPassword!!))
            userPassword = ""
            newUserPassword = ""
            loading(false)
        }
    }

    private fun loading(boolean: Boolean) {
        _viewEnabled.postValue(!boolean)
        _loadingEdit.postValue(boolean)
        if (true) {
            _buttonNameEnabled.postValue(!boolean)
            _buttonEmailEnabled.postValue(!boolean)
            _buttonPasswordEnabled.postValue(!boolean)
        } else {
            _buttonNameEnabled.postValue(!userName.value.isNullOrEmpty())
            _buttonEmailEnabled.postValue(!userEmail.value.isNullOrEmpty())
            checkingIfItIsEmpty()
        }
    }
}
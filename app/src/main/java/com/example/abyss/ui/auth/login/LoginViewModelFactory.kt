package com.example.abyss.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.abyss.model.repository.auth.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher

@Suppress("UNCHECKED_CAST")
class LoginViewModelFactory(
    private val authRepository: AuthRepository,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel(authRepository, ioDispatcher) as T
    }
}
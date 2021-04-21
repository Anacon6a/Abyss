package com.example.abyss.ui.auth.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.abyss.model.repository.UserRepository
import com.example.abyss.model.repository.auth.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher

@Suppress("UNCHECKED_CAST")
class RegistrationViewModelFactory(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RegistrationViewModel(authRepository, userRepository, ioDispatcher) as T
    }
}
package com.example.abyss.ui.auth.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.abyss.data.repositories.UserRepository
import com.example.abyss.ui.auth.login.LoginViewModel

@Suppress("UNCHECKED_CAST")
class RegistrationViewModelFactory(
    private val repository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RegistrationViewModel(repository) as T
    }
}
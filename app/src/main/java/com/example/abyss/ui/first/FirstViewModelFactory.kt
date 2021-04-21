package com.example.abyss.ui.first

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.abyss.model.repository.auth.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher


class FirstViewModelFactory(
    private val authRepository: AuthRepository,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FirstViewModel(authRepository, ioDispatcher) as T
    }
}
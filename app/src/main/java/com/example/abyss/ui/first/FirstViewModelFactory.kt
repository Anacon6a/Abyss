package com.example.abyss.ui.first

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.abyss.model.repository.UserRepository


class FirstViewModelFactory (
    private val repository: UserRepository
    ) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FirstViewModel(repository) as T
    }
}
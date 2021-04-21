package com.example.abyss.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.abyss.ui.auth.login.LoginViewModel
import com.example.abyss.ui.posts.AddPostViewModel
import kotlinx.coroutines.CoroutineDispatcher
import org.kodein.di.DKodein
import org.kodein.di.TT
import org.kodein.di.generic.instanceOrNull
import org.kodein.di.generic.provider
import timber.log.Timber

//class ProfileViewModelFactory(
//    private val ioDispatcher: CoroutineDispatcher
//) : ViewModelProvider.NewInstanceFactory() {
//
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        return LoginViewModel(authRepository, ioDispatcher) as T
//    }
//}
class ProfileViewModelFactory(
    private val kodein: DKodein,

    ) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Timber.i("В фабрике")
        return kodein.instanceOrNull<ViewModel>(tag = ProfileViewModel::class.java) as T?
            ?: modelClass.newInstance()
    }

}

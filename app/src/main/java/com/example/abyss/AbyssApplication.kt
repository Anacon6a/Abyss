package com.example.abyss

import android.app.Application
import com.example.abyss.model.firebase.FirebaseSource
import com.example.abyss.model.repository.AuthRepository2
import com.example.abyss.model.repository.UserRepository
import com.example.abyss.model.repository.auth.AuthRepositoryFirebase
import com.example.abyss.model.repository.auth.AuthRepository
import com.example.abyss.ui.first.FirstViewModelFactory
import com.example.abyss.ui.auth.login.LoginViewModelFactory
import com.example.abyss.ui.auth.registration.RegistrationViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.*
import timber.log.Timber

class AbyssApplication : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@AbyssApplication))
// firebase
        bind<FirebaseAuth>() with provider { FirebaseAuth.getInstance()}

// репозитории, зависимости будет переданы единожды
        bind() from singleton { FirebaseSource() }
        bind() from singleton { UserRepository(instance()) }
        bind() from singleton { AuthRepository2(instance()) }
        bind<AuthRepository>() with singleton { AuthRepositoryFirebase(instance(), instance(), instance()) }
// фабрики, зависимость будет запрашиваться каждый раз при обращении
        bind() from provider { FirstViewModelFactory(instance()) }
        bind() from provider { LoginViewModelFactory(instance(), instance()) }
        bind() from provider { RegistrationViewModelFactory(instance(), instance(), instance()) }
// корутины
        bind<CoroutineScope>() with singleton { MainScope() }
        bind<CoroutineDispatcher>() with singleton { Dispatchers.IO }
        bind<CoroutineDispatcher>(tag = "default") with singleton { Dispatchers.Default }
    }

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}
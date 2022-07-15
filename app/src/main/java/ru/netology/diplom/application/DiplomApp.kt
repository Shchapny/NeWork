package ru.netology.diplom.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.diplom.authorization.AppAuth
import javax.inject.Inject

@HiltAndroidApp
class DiplomApp : Application() {

    private val appScore = CoroutineScope(Dispatchers.Default)

    @Inject
    lateinit var appAuth: AppAuth

    override fun onCreate() {
        super.onCreate()
        setupAuth()
    }

    private fun setupAuth() {
        appScore.launch {
            appAuth.sendPushToken()
        }
    }
}
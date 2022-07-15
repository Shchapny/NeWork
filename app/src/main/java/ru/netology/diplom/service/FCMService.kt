package ru.netology.diplom.service

import com.google.firebase.messaging.FirebaseMessagingService
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diplom.authorization.AppAuth
import javax.inject.Inject

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {

    @Inject
    lateinit var appAuth: AppAuth

    override fun onNewToken(token: String) {
        appAuth.sendPushToken(token)
    }
}
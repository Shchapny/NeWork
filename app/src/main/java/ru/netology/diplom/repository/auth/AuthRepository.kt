package ru.netology.diplom.repository.auth

import kotlinx.coroutines.flow.Flow
import ru.netology.diplom.data.dto.User
import ru.netology.diplom.data.dto.media.MediaUpload
import ru.netology.diplom.model.state.AuthState

interface AuthRepository {

    val dataAuth: Flow<AuthState>
    val authenticated: Boolean
    suspend fun getUserById(id: Long): User
    suspend fun authentication(login: String, password: String)
    suspend fun registerUser(login: String, password: String, name: String)
    suspend fun registerWithPhoto(
        login: String,
        password: String,
        name: String,
        mediaUpload: MediaUpload
    )
}
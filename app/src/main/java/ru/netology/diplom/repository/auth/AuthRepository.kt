package ru.netology.diplom.repository.auth

import ru.netology.diplom.data.dto.Token
import ru.netology.diplom.data.dto.User
import ru.netology.diplom.data.dto.media.MediaUpload

interface AuthRepository {

    suspend fun getUserById(id: Long): User
    suspend fun authentication(login: String, password: String): Token
    suspend fun registerUser(login: String, password: String, name: String): Token
    suspend fun registerWithPhoto(
        login: String,
        password: String,
        name: String,
        mediaUpload: MediaUpload
    ): Token
}
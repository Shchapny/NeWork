package ru.netology.diplom.repository.auth

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.diplom.api.AuthApiService
import ru.netology.diplom.data.dto.Token
import ru.netology.diplom.data.dto.User
import ru.netology.diplom.data.dto.media.MediaUpload
import ru.netology.diplom.error.ApiError
import ru.netology.diplom.error.NetworkError
import ru.netology.diplom.error.ServerError
import ru.netology.diplom.error.UnknownError
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService
) : AuthRepository {

    override suspend fun getUserById(id: Long): User {
        try {
            val response = authApiService.getUserById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: SocketTimeoutException) {
            throw ServerError
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun authentication(login: String, password: String): Token {
        try {
            val response = authApiService.authentication(login, password)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: SocketTimeoutException) {
            throw ServerError
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun registerUser(login: String, password: String, name: String): Token {
        try {
            val response = authApiService.registerUser(login, password, name)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: SocketTimeoutException) {
            throw ServerError
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun registerWithPhoto(
        login: String,
        password: String,
        name: String,
        mediaUpload: MediaUpload
    ): Token {
        try {
            val loginRequest = login.toRequestBody("text/plain".toMediaType())
            val passRequest = password.toRequestBody("text/plain".toMediaType())
            val nameRequest = name.toRequestBody("text/plain".toMediaType())
            val media = MultipartBody.Part.createFormData(
                "file",
                mediaUpload.file.name,
                mediaUpload.file.asRequestBody()
            )
            val response =
                authApiService.registerWithPhoto(loginRequest, passRequest, nameRequest, media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: SocketTimeoutException) {
            throw ServerError
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}
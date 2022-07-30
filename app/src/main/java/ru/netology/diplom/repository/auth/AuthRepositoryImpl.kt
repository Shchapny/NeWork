package ru.netology.diplom.repository.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.diplom.api.AuthApiService
import ru.netology.diplom.data.dao.UserDao
import ru.netology.diplom.data.dto.Token
import ru.netology.diplom.data.dto.User
import ru.netology.diplom.data.dto.media.MediaUpload
import ru.netology.diplom.data.entity.toDto
import ru.netology.diplom.data.entity.toUserEntity
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
    private val authApiService: AuthApiService,
    private val userDao: UserDao
) : AuthRepository {

    override val data: Flow<List<User>> = userDao.getAllUsers()
        .map { users -> users.toDto() }

    override suspend fun getAllUsers() {
        try {
            val response = authApiService.getAllUsers()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            userDao.insertUsers(body.toUserEntity())
        } catch (e: SocketTimeoutException) {
            throw ServerError
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

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
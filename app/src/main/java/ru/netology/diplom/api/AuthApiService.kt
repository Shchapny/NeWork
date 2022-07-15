package ru.netology.diplom.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.diplom.data.dto.User
import ru.netology.diplom.data.dto.token.PushToken
import ru.netology.diplom.model.state.AuthState

interface AuthApiService {

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<User>

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun authentication(
        @Field("login") login: String,
        @Field("pass") password: String
    ): Response<AuthState>

    @FormUrlEncoded
    @POST("users/registration")
    suspend fun registerUser(
        @Field("login") login: String,
        @Field("pass") password: String,
        @Field("name") name: String
    ): Response<AuthState>

    @Multipart
    @POST("users/registration")
    suspend fun registerWithPhoto(
        @Part("login") login: RequestBody,
        @Part("pass") password: RequestBody,
        @Part("name") name: RequestBody,
        @Part file: MultipartBody.Part?
    ): Response<AuthState>

    @POST("users/push-tokens")
    suspend fun saveToken(@Body pushToken: PushToken): Response<Unit>
}
package ru.netology.diplom.api

import retrofit2.Response
import retrofit2.http.*
import ru.netology.diplom.data.dto.entity.Job

interface JobApiService {

    @GET("{id}/jobs")
    suspend fun getByUserId(@Path("id") id: Long): Response<List<Job>>

    @POST("my/jobs")
    suspend fun save(@Body job: Job): Response<Job>

    @DELETE("my/jobs/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>
}
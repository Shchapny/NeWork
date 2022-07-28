package ru.netology.diplom.repository.job

import kotlinx.coroutines.flow.Flow
import ru.netology.diplom.data.dto.entity.Job

interface JobRepository {
    fun data(userId: Long): Flow<List<Job>>
    suspend fun save(job: Job, userId: Long): Long
    suspend fun getByUserId(id: Long)
    suspend fun removeById(id: Long)
}
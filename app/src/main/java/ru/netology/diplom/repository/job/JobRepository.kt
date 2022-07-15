package ru.netology.diplom.repository.job

import kotlinx.coroutines.flow.Flow
import ru.netology.diplom.data.dto.entity.Job

interface JobRepository {
    val data: Flow<List<Job>>
    suspend fun save(job: Job)
    suspend fun getAll()
    suspend fun removeById(id: Long)
}
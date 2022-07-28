package ru.netology.diplom.repository.job

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.diplom.api.JobApiService
import ru.netology.diplom.data.dao.JobDao
import ru.netology.diplom.data.dto.entity.Job
import ru.netology.diplom.data.entity.JobEntity
import ru.netology.diplom.data.entity.toJobEntity
import ru.netology.diplom.error.*
import java.io.IOException
import java.net.SocketTimeoutException
import java.sql.SQLException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobRepositoryImpl @Inject constructor(
    private val jobApiService: JobApiService,
    private val jobDao: JobDao
) : JobRepository {

    override fun data(userId: Long): Flow<List<Job>> {
        return jobDao.getByUserId(userId).map { jobs ->
            jobs.map { job -> job.toDto() }
        }
    }

    override suspend fun save(job: Job, userId: Long): Long {
        try {
            val jobId = jobDao.saveJob(JobEntity.fromDto(job, userId))

            val response = jobApiService.save(job)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            jobDao.insertJob(JobEntity.fromDto(body, userId))

            return jobId
        } catch (e: ApiError) {
            throw e
        } catch (e: SocketTimeoutException) {
            throw ServerError
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getByUserId(id: Long) {
        try {
            jobDao.removeAll()
            val response = jobApiService.getByUserId(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            jobDao.insertJobs(body.toJobEntity(id))
        } catch (e: ApiError) {
            throw e
        } catch (e: SocketTimeoutException) {
            throw ServerError
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            val response = jobApiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            jobDao.removeById(id)
        } catch (e: ApiError) {
            throw e
        } catch (e: SocketTimeoutException) {
            throw ServerError
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}
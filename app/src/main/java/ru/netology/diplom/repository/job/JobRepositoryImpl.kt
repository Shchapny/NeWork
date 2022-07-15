package ru.netology.diplom.repository.job

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import ru.netology.diplom.api.JobApiService
import ru.netology.diplom.authorization.AppAuth
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

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class JobRepositoryImpl @Inject constructor(
    private val jobApiService: JobApiService,
    private val jobDao: JobDao,
    appAuth: AppAuth
) : JobRepository {

    override val data: Flow<List<Job>> = appAuth.authStateFlow
        .flatMapLatest {
            jobDao.get().map { jobs ->
                jobs.map(JobEntity::toDto)
            }
        }

    override suspend fun save(job: Job) {
        try {
            jobDao.saveJob(JobEntity.fromDto(job))

            val response = jobApiService.save(job)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            jobDao.insert(JobEntity.fromDto(body))
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

    override suspend fun getAll() {
        try {
            val response = jobApiService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            jobDao.insert(body.toJobEntity())
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
package ru.netology.diplom.repository.event

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.diplom.api.EventApiService
import ru.netology.diplom.api.MediaApiService
import ru.netology.diplom.data.dao.EventDao
import ru.netology.diplom.data.dao.EventRemoteKeyDao
import ru.netology.diplom.data.db.AppDb
import ru.netology.diplom.data.dto.Attachment
import ru.netology.diplom.data.dto.entity.Event
import ru.netology.diplom.data.dto.media.Media
import ru.netology.diplom.data.dto.media.MediaUpload
import ru.netology.diplom.data.entity.EventEntity
import ru.netology.diplom.data.entity.toEntity
import ru.netology.diplom.enumeration.AttachmentType
import ru.netology.diplom.error.*
import ru.netology.diplom.mediator.EventRemoteMediator
import java.io.IOException
import java.net.SocketTimeoutException
import java.sql.SQLException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OptIn(ExperimentalPagingApi::class)
class EventRepositoryImpl @Inject constructor(
    private val eventApiService: EventApiService,
    private val mediaApiService: MediaApiService,
    private val eventDao: EventDao,
    eventRemoteKeyDao: EventRemoteKeyDao,
    appDb: AppDb
) : EventRepository {

    override val data: Flow<PagingData<Event>> = Pager(
        remoteMediator = EventRemoteMediator(eventApiService, eventDao, eventRemoteKeyDao, appDb),
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = eventDao::getPagingSource
    ).flow.map {
        it.map(EventEntity::toDto)
    }

    override suspend fun save(event: Event, upload: MediaUpload?) {
        try {
            val media = upload?.let { uploadMedia(it) }
            val eventWithAttachment =
                media?.let { event.copy(attachment = Attachment(it.url, AttachmentType.IMAGE)) }

            val response = eventApiService.save(eventWithAttachment ?: event)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
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

    override suspend fun getLatest() {
        try {
            val response = eventApiService.getLatest(10)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(body.toEntity())
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

    override suspend fun getById(id: Long): Event {
        return try {
            eventDao.getById(id).toDto()
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            val response = eventApiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            eventDao.removeById(id)
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

    override suspend fun likeById(id: Long) {
        try {
            eventDao.likeByMe(id)
            val response = eventApiService.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
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

    override suspend fun dislikeById(id: Long) {
        try {
            eventDao.likeByMe(id)
            val response = eventApiService.dislikeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
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

    override suspend fun uploadMedia(upload: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file",
                upload.file.name,
                upload.file.asRequestBody()
            )

            val response = mediaApiService.uploadMedia(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: ApiError) {
            throw e
        } catch (e: SocketTimeoutException) {
            throw ServerError
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun participateById(id: Long) {
        try {
            eventDao.participateByMe(id)
            val response = eventApiService.participateById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
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

    override suspend fun refuseById(id: Long) {
        try {
            eventDao.participateByMe(id)
            val response = eventApiService.refuseById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
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
package ru.netology.diplom.repository.post

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.diplom.api.MediaApiService
import ru.netology.diplom.api.PostApiService
import ru.netology.diplom.data.dao.PostDao
import ru.netology.diplom.data.dao.PostRemoteKeyDao
import ru.netology.diplom.data.db.AppDb
import ru.netology.diplom.data.dto.Attachment
import ru.netology.diplom.data.dto.entity.Post
import ru.netology.diplom.data.dto.media.Media
import ru.netology.diplom.data.dto.media.MediaUpload
import ru.netology.diplom.data.entity.PostEntity
import ru.netology.diplom.data.entity.toEntity
import ru.netology.diplom.enumeration.AttachmentType
import ru.netology.diplom.error.ApiError
import ru.netology.diplom.error.DbError
import ru.netology.diplom.error.NetworkError
import ru.netology.diplom.error.ServerError
import ru.netology.diplom.error.UnknownError
import ru.netology.diplom.mediator.PostRemoteMediator
import java.io.IOException
import java.net.SocketTimeoutException
import java.sql.SQLException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OptIn(ExperimentalPagingApi::class)
class PostRepositoryImpl @Inject constructor(
    private val postApiService: PostApiService,
    private val mediaApiService: MediaApiService,
    private val postDao: PostDao,
    postRemoteKeyDao: PostRemoteKeyDao,
    appDb: AppDb
) : PostRepository {

    override val data: Flow<PagingData<Post>> = Pager(
        remoteMediator = PostRemoteMediator(postApiService, postDao, postRemoteKeyDao, appDb),
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = postDao::getPagingSource
    ).flow.map {
        it.map(PostEntity::toDto)
    }

    override suspend fun save(post: Post, upload: MediaUpload?) {
        try {
            val media = upload?.let { uploadMedia(it) }
            val postWithAttachment =
                media?.let { post.copy(attachment = Attachment(it.url, AttachmentType.IMAGE)) }

            val response = postApiService.save(postWithAttachment ?: post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
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
            val response = postApiService.getLatest(10)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(body.toEntity())
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

    override suspend fun getById(id: Long): Post {
        return try {
            postDao.getById(id).toDto()
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            val response = postApiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            postDao.removeById(id)
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
            postDao.likeByMe(id)
            val response = postApiService.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
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
            postDao.likeByMe(id)
            val response = postApiService.dislikeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
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
}
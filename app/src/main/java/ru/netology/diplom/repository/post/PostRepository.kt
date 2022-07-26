package ru.netology.diplom.repository.post

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.diplom.data.dto.entity.Post
import ru.netology.diplom.data.dto.media.Media
import ru.netology.diplom.data.dto.media.MediaUpload

interface PostRepository {
    val data: Flow<PagingData<Post>>
    suspend fun save(post: Post, upload: MediaUpload?)
    suspend fun getLatest()
    suspend fun getById(id: Long): Post
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun dislikeById(id: Long)
    suspend fun uploadMedia(upload: MediaUpload): Media
}
package ru.netology.diplom.repository.event

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.diplom.data.dto.entity.Event
import ru.netology.diplom.data.dto.media.Media
import ru.netology.diplom.data.dto.media.MediaUpload

interface EventRepository {
    val data: Flow<PagingData<Event>>
    suspend fun save(event: Event, upload: MediaUpload?): Long
    suspend fun getById(id: Long): Event
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun dislikeById(id: Long)
    suspend fun uploadMedia(upload: MediaUpload): Media
    suspend fun participateById(id: Long)
    suspend fun refuseById(id: Long)
}
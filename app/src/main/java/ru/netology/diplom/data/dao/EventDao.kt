package ru.netology.diplom.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.diplom.data.entity.EventEntity

@Dao
interface EventDao {

    @Query("SELECT * FROM EventEntity ORDER BY id DESC")
    fun getPagingSource(): PagingSource<Int, EventEntity>

    @Query("SELECT * FROM EventEntity ORDER BY id DESC")
    fun getLatest(): Flow<List<EventEntity>>

    @Query("SELECT * FROM EventEntity WHERE id = :id")
    suspend fun getById(id: Long): EventEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Query(
        """
        UPDATE EventEntity SET
        content = :content, datetime = :datetime, link = :link
        WHERE id = :eventId 
        """
    )
    suspend fun updateEventById(eventId: Long, content: String, datetime: String, link: String)

    @Query(
        """
        UPDATE EventEntity SET
        likeOwnerIds  = likeOwnerIds + CASE WHEN likedByMe THEN -1 ELSE 1 END, 
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id =:id;
        """
    )
    suspend fun likeByMe(id: Long)

    @Query(
        """
        UPDATE EventEntity SET
        participantsIds = participantsIds + CASE WHEN participatedByMe THEN -1 ELSE 1 END,
        participatedByMe = CASE WHEN participatedByMe THEN 0 ELSE 1 END
        WHERE id =:id; 
        """
    )
    suspend fun participateByMe(id: Long)

    @Query("DELETE FROM EventEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("DELETE FROM EventEntity")
    suspend fun removeAll()

    suspend fun saveEvent(event: EventEntity) =
        if (event.id == 0L) {
            insertEvent(event)
        } else {
            updateEventById(event.id, event.content, event.datetime, event.link)
            0L
        }
}
package ru.netology.diplom.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.diplom.data.entity.PostEntity

@Dao
interface PostDao {

    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getPagingSource(): PagingSource<Int, PostEntity>

    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getLatest(): Flow<List<PostEntity>>

    @Query("SELECT * FROM PostEntity WHERE id = :id")
    suspend fun getById(id: Long): PostEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Query("UPDATE PostEntity SET content = :content WHERE id = :postId")
    suspend fun updateContentById(postId: Long, content: String)

    @Query(
        """
        UPDATE PostEntity SET
        likeOwnerIds = likeOwnerIds + CASE WHEN likedByMe THEN -1 ELSE 1 END, 
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id;
        """
    )
    suspend fun likeByMe(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("DELETE FROM PostEntity")
    suspend fun removeAll()

    suspend fun savePost(post: PostEntity): Long =
        if (post.id == 0L) {
            insertPost(post)
        } else {
            updateContentById(post.id, post.content)
            0L
        }
}
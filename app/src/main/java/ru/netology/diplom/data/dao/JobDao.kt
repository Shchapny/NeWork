package ru.netology.diplom.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.diplom.data.entity.JobEntity

@Dao
interface JobDao {

    @Query("SELECT * FROM JobEntity WHERE userId = :userId ORDER BY start DESC")
    fun getByUserId(userId: Long): Flow<List<JobEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: JobEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobs(jobs: List<JobEntity>)

    @Query(
        """
        UPDATE JobEntity SET 
        name = :name, position = :position, start = :start, finish = :finish, link = :link
        WHERE id = :jobId
        """
    )
    suspend fun updatePositionById(
        jobId: Long,
        name: String,
        position: String,
        start: String,
        finish: String?,
        link: String?
    )

    @Query("DELETE FROM JobEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("DELETE FROM JobEntity")
    suspend fun removeAll()

    suspend fun saveJob(job: JobEntity) =
        if (job.id == 0L) {
            insertJob(job)
        } else {
            updatePositionById(job.id, job.name, job.position, job.start, job.finish, job.link)
            0L
        }
}
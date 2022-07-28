package ru.netology.diplom.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.diplom.data.dto.entity.Job

@Entity
data class JobEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val position: String,
    val start: String,
    val finish: String? = null,
    val link: String? = null,
    val ownedByMe: Boolean = false,
    val userId: Long? = null
) {

    companion object {
        fun fromDto(dto: Job, userId: Long): JobEntity = with(dto) {
            JobEntity(
                id = id,
                name = name,
                position = position,
                start = start,
                finish = finish,
                link = link,
                ownedByMe = ownedByMe,
                userId = userId
            )
        }
    }

    fun toDto() = with(this) {
        Job(
            id = id,
            name = name,
            position = position,
            start = start,
            finish = finish,
            link = link,
            ownedByMe = ownedByMe
        )
    }
}

fun List<Job>.toJobEntity(userId: Long): List<JobEntity> = map {JobEntity.fromDto(it, userId)}

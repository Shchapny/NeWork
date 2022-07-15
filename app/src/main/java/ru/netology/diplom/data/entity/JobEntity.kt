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
    val start: Long,
    val finish: Long? = null,
    val link: String? = null,
    val ownedByMe: Boolean = false
) {

    companion object {
        fun fromDto(dto: Job): JobEntity = with(dto) {
            JobEntity(
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

fun List<Job>.toJobEntity(): List<JobEntity> = map(JobEntity::fromDto)

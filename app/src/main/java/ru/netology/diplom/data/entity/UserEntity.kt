package ru.netology.diplom.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.diplom.data.dto.User

@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String? = null
) {

    companion object {
        fun fromDto(dto: User): UserEntity = with(dto) {
            UserEntity(
                id = id,
                login = login,
                name = name,
                avatar = avatar
            )
        }
    }

    fun toDto() = with(this) {
        User(
            id = id,
            login = login,
            name = name,
            avatar = avatar
        )
    }
}

fun List<UserEntity>.toDto() = map(UserEntity::toDto)
fun List<User>.toUserEntity() = map(UserEntity::fromDto)

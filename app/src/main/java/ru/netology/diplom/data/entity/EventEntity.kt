package ru.netology.diplom.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.diplom.data.dto.entity.Event
import ru.netology.diplom.data.entity.embeddable.AttachmentEmbeddable
import ru.netology.diplom.data.entity.embeddable.EventTypeEmbeddable

@Entity
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val published: String,
    val datetime: String,
    @Embedded
    val type: EventTypeEmbeddable,
    val link: String,
    val speakerIds: Set<Long> = emptySet(),
    @Embedded
    val attachment: AttachmentEmbeddable? = null,
    val ownedByMe: Boolean = false,
    val likedByMe: Boolean = false,
    val likeOwnerIds: Set<Long> = emptySet(),
    val participatedByMe: Boolean = false,
    val participantsIds: Set<Long> = emptySet()
) {

    companion object {
        fun fromDto(dto: Event): EventEntity = with(dto) {
            EventEntity(
                id = id,
                authorId = authorId,
                author = author,
                authorAvatar = authorAvatar,
                content = content,
                published = published,
                datetime = datetime,
                type = EventTypeEmbeddable.fromDto(dto.type),
                link = link,
                speakerIds = speakerIds,
                attachment = AttachmentEmbeddable.fromDto(dto.attachment),
                ownedByMe = ownedByMe,
                likedByMe = likedByMe,
                likeOwnerIds = likeOwnerIds,
                participatedByMe = participatedByMe,
                participantsIds = participantsIds
            )
        }
    }

    fun toDto() = with(this) {
        Event(
            id = id,
            authorId = authorId,
            author = author,
            authorAvatar = authorAvatar,
            content = content,
            published = published,
            datetime = datetime,
            type = type.toDto(),
            link = link,
            speakerIds = speakerIds,
            attachment = attachment?.toDto(),
            ownedByMe = ownedByMe,
            likedByMe = likedByMe,
            likeOwnerIds = likeOwnerIds,
            participatedByMe = participatedByMe,
            participantsIds = participantsIds
        )
    }
}

fun List<Event>.toEntity(): List<EventEntity> = map(EventEntity::fromDto)

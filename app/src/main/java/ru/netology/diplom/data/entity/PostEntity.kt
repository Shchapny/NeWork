package ru.netology.diplom.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.diplom.data.dto.entity.Post
import ru.netology.diplom.data.entity.embeddable.AttachmentEmbeddable

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val published: String,
    val link: String? = null,
    @Embedded
    val attachment: AttachmentEmbeddable? = null,
    val ownedByMe: Boolean = false,
    val likedByMe: Boolean = false,
    val likeOwnerIds: Set<Long> = emptySet(),
    val mentionedMe: Boolean = false,
    val mentionIds: Set<Long> = emptySet()
) {

    companion object {
        fun fromDto(dto: Post): PostEntity = with(dto) {
            PostEntity(
                id = id,
                authorId = authorId,
                author = author,
                authorAvatar = authorAvatar,
                content = content,
                published = published,
                link = link,
                attachment = AttachmentEmbeddable.fromDto(dto.attachment),
                ownedByMe = ownedByMe,
                likedByMe = likedByMe,
                likeOwnerIds = likeOwnerIds,
                mentionedMe = mentionedMe,
                mentionIds = mentionIds
            )
        }
    }

    fun toDto() = with(this) {
        Post(
            id = id,
            authorId = authorId,
            author = author,
            authorAvatar = authorAvatar,
            content = content,
            published = published,
            link = link,
            attachment = attachment?.toDto(),
            ownedByMe = ownedByMe,
            likedByMe = likedByMe,
            likeOwnerIds = likeOwnerIds,
            mentionedMe = mentionedMe,
            mentionIds = mentionIds
        )
    }
}

fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)
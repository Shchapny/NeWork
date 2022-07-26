package ru.netology.diplom.data.dto.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.netology.diplom.data.dto.Attachment

@Parcelize
data class Post(
    val id: Long = 0,
    val authorId: Long = 0,
    val author: String = "",
    val authorAvatar: String? = null,
    val content: String = "",
    val published: String = "2022-07-13T07:58:57.835201Z",
    val link: String? = null,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
    val likedByMe: Boolean = false,
    val likeOwnerIds: Set<Long> = emptySet(),
    val mentionedMe: Boolean = false,
    val mentionIds: Set<Long> = emptySet()
) : Parcelable
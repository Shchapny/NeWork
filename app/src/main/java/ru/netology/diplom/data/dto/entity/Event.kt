package ru.netology.diplom.data.dto.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.netology.diplom.data.dto.Attachment
import ru.netology.diplom.enumeration.EventStatusType

@Parcelize
data class Event(
    val id: Long = 0,
    val authorId: Long = 0,
    val author: String = "",
    val authorAvatar: String? = null,
    val content: String = "",
    val published: String = "22 июн 2022",
    val datetime: String = "",
    val type: EventStatusType = EventStatusType.ONLINE,
    val link: String? = null,
    val speakerIds: Set<Long> = emptySet(),
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
    val likedByMe: Boolean = false,
    val likeOwnerIds: Set<Long> = emptySet(),
    val participatedByMe: Boolean = false,
    val participantsIds: Set<Long> = emptySet()
): Parcelable
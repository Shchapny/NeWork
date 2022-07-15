package ru.netology.diplom.data.dto.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Job(
    val id: Long = 0,
    val name: String = "",
    val position: String = "",
    val start: Long = 0,
    val finish: Long? = null,
    val link: String? = null,
    val ownedByMe: Boolean = false
) : Parcelable
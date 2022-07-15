package ru.netology.diplom.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.netology.diplom.enumeration.AttachmentType

@Parcelize
data class Attachment(
    val url: String,
    val type: AttachmentType
) : Parcelable

package ru.netology.diplom.data.entity.embeddable

import ru.netology.diplom.data.dto.Attachment
import ru.netology.diplom.enumeration.AttachmentType

data class AttachmentEmbeddable(
    val url: String,
    val type: AttachmentType
) {

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let { attachment ->
            AttachmentEmbeddable(attachment.url, attachment.type)
        }
    }

    fun toDto() = Attachment(url, type)
}

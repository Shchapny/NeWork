package ru.netology.diplom.data.entity.embeddable

import ru.netology.diplom.enumeration.EventStatusType

data class EventTypeEmbeddable(
    val eventType: String
) {

    companion object {
        fun fromDto(dto: EventStatusType) = EventTypeEmbeddable(dto.name)
    }

    fun toDto() = EventStatusType.valueOf(eventType)
}


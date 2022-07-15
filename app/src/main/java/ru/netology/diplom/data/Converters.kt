package ru.netology.diplom.data

import androidx.room.TypeConverter
import ru.netology.diplom.enumeration.AttachmentType

class Converters {

    @TypeConverter
    fun fromAttachmentType(value: AttachmentType) = value.name

    @TypeConverter
    fun toAttachmentType(value: String) = enumValueOf<AttachmentType>(value)

    @TypeConverter
    fun fromSet(value: Set<Long>) = value.joinToString(",")

    @TypeConverter
    fun toSet(data: String) =
        if (data.isBlank()) emptySet() else data.split(",").map { it.toLong() }.toSet()
}
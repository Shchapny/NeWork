package ru.netology.diplom.model

data class FeedModel<T>(
    val entities: List<T> = emptyList(),
    val empty: Boolean = false
)

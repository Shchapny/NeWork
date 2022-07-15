package ru.netology.diplom.model.state

data class FeedModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val refreshing: Boolean = false,
    val empty: Boolean = false,
    val server: Boolean = false,
    val authState: Boolean = false,
    val network: Boolean = false
)

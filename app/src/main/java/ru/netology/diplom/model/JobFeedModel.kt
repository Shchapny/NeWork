package ru.netology.diplom.model

import ru.netology.diplom.data.dto.entity.Job

data class JobFeedModel(
    val list: List<Job> = emptyList(),
    val empty: Boolean = false
)
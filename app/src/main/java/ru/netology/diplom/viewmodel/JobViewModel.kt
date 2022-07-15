package ru.netology.diplom.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.diplom.data.dto.entity.Job
import ru.netology.diplom.model.FeedModel
import ru.netology.diplom.model.state.FeedModelState
import ru.netology.diplom.repository.job.JobRepository
import ru.netology.diplom.util.SingleLiveEvent
import javax.inject.Inject

private val emptyJob = Job()

@HiltViewModel
class JobViewModel @Inject constructor(
    private val jobRepository: JobRepository
) : ViewModel() {

    val data = jobRepository.data
        .map { jobs ->
            FeedModel(
                jobs.map { it },
                jobs.isEmpty()
            )
        }
        .catch { e -> e.printStackTrace() }

    private val _jobCreated = SingleLiveEvent<Unit>()
    val jobCreated: LiveData<Unit> = _jobCreated

    private val _dataState = MutableLiveData(FeedModelState())
    val dataState: LiveData<FeedModelState> = _dataState

    private val edited = MutableLiveData(emptyJob)

    init {
        loadJobs()
    }

    private fun loadJobs() = viewModelScope.launch {
        _dataState.value = FeedModelState(loading = true)
        try {
            jobRepository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun refresh() = viewModelScope.launch {
        _dataState.value = FeedModelState(refreshing = true)
        try {
            jobRepository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun save() {
        edited.value?.let { job ->
            _jobCreated.value = Unit
            viewModelScope.launch {
                _dataState.value = FeedModelState(loading = true)
                try {
                    jobRepository.save(job)
                    _dataState.value = FeedModelState()
                    _jobCreated.value = Unit
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = emptyJob
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            jobRepository.removeById(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun edit(job: Job) {
        edited.value = job
    }

    fun change(
        company: String,
        position: String,
        startDate: Long,
        finishDate: Long? = null,
        link: String? = null
    ) {
        edited.value = edited.value?.copy(
            name = company,
            position = position,
            start = startDate,
            finish = finishDate,
            link = link
        )
    }
}
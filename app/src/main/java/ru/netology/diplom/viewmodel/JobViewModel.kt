package ru.netology.diplom.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.diplom.authorization.AppAuth
import ru.netology.diplom.data.dto.entity.Job
import ru.netology.diplom.model.JobFeedModel
import ru.netology.diplom.model.state.FeedModelState
import ru.netology.diplom.repository.job.JobRepository
import ru.netology.diplom.util.SingleLiveEvent
import javax.inject.Inject

private val emptyJob = Job()

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class JobViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val appAuth: AppAuth
) : ViewModel() {

    val data = appAuth.authStateFlow
        .flatMapLatest { (myId, _) ->
            jobRepository.data(myId)
                .map { jobs ->
                    JobFeedModel(
                        jobs.map { job ->
                            job.copy(ownedByMe = userId.value == myId)
                        },
                        jobs.isEmpty()
                    )
                }
        }
        .catch { e -> e.printStackTrace() }
        .asLiveData(Dispatchers.Default)

    private val _jobCreated = SingleLiveEvent<Unit>()
    val jobCreated: LiveData<Unit> = _jobCreated

    private val _dataState = MutableLiveData(FeedModelState())
    val dataState: LiveData<FeedModelState> = _dataState

    private val edited = MutableLiveData(emptyJob)
    private val userId = MutableLiveData<Long>()

    fun loadJobs(id: Long) = viewModelScope.launch {
        _dataState.value = FeedModelState(loading = true)
        try {
            userId.value = id
            jobRepository.getByUserId(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun save() {
        edited.value?.let { job ->
            viewModelScope.launch {
                try {
                    jobRepository.save(job, appAuth.authStateFlow.value.id)
                    _jobCreated.postValue(Unit)
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
        startDate: String,
        finishDate: String? = null,
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
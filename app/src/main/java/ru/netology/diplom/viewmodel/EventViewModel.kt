package ru.netology.diplom.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.diplom.authorization.AppAuth
import ru.netology.diplom.data.dto.User
import ru.netology.diplom.data.dto.entity.Event
import ru.netology.diplom.data.dto.media.MediaUpload
import ru.netology.diplom.data.entity.embeddable.EventTypeEmbeddable
import ru.netology.diplom.model.media.PhotoModel
import ru.netology.diplom.model.state.FeedModelState
import ru.netology.diplom.repository.event.EventRepository
import ru.netology.diplom.util.SingleLiveEvent
import java.io.File
import javax.inject.Inject

private val emptyEvent = Event()
private val noPhoto = PhotoModel()

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    appAuth: AppAuth
) : ViewModel() {

    private val cached = eventRepository.data.cachedIn(viewModelScope)

    val data = appAuth.authStateFlow
        .flatMapLatest { (myId, _) ->
            cached.map { pagingData ->
                pagingData.map { event ->
                    event.copy(
                        ownedByMe = event.authorId == myId,
                        likedByMe = event.likeOwnerIds.contains(myId),
                        participatedByMe = event.participantsIds.contains(myId)
                    )
                }
            }
        }

    private val _eventCreated = SingleLiveEvent<Unit>()
    val eventCreated: LiveData<Unit> = _eventCreated

    private val _dataState = MutableLiveData(FeedModelState())
    val dataState: LiveData<FeedModelState> = _dataState

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel> = _photo

    private val _listSpeakers = MutableLiveData(mutableMapOf<Long, String>())
    val listSpeakers: LiveData<MutableMap<Long, String>> = _listSpeakers

    private val edited = MutableLiveData(emptyEvent)
    private val list = mutableMapOf<Long, String>()

    fun save() {
        edited.value?.let { event ->
            viewModelScope.launch {
                try {
                    eventRepository.save(
                        event,
                        _photo.value?.file?.let { file -> MediaUpload(file) })
                    _eventCreated.value = Unit
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = emptyEvent
        _photo.value = noPhoto
        list.clear()
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            eventRepository.removeById(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            eventRepository.likeById(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun dislikeById(id: Long) = viewModelScope.launch {
        try {
            eventRepository.dislikeById(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun participateById(id: Long) = viewModelScope.launch {
        try {
            eventRepository.participateById(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun refuseById(id: Long) = viewModelScope.launch {
        try {
            eventRepository.refuseById(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun edit(event: Event) {
        edited.value = event
    }

    fun changeContent(content: String, datetime: String, type: String, link: String) {
        val format = EventTypeEmbeddable(type.trim().uppercase()).toDto()
        if (content == edited.value?.content && datetime == edited.value?.datetime && format == edited.value?.type && link == edited.value?.link) {
            return
        }
        edited.value =
            edited.value?.copy(content = content, datetime = datetime, type = format, link = link)
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

    fun selectSpeaker(user: User) {
        edited.value =
            edited.value?.speakerIds?.plus(user.id)?.let { userId ->
                edited.value?.copy(speakerIds = userId)
            }

        if (!list.keys.contains(user.id)) {
            list[user.id] = user.name
            _listSpeakers.value = list
        }
    }
}
package ru.netology.diplom.viewmodel.auth

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.diplom.data.dto.media.MediaUpload
import ru.netology.diplom.error.NetworkError
import ru.netology.diplom.error.ServerError
import ru.netology.diplom.error.UnknownError
import ru.netology.diplom.model.media.PhotoModel
import ru.netology.diplom.model.state.FeedModelState
import ru.netology.diplom.repository.auth.AuthRepository
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _dataState = MutableLiveData(FeedModelState())
    val dataState: LiveData<FeedModelState> = _dataState

    private val _photo = MutableLiveData(PhotoModel())
    val photo: LiveData<PhotoModel> = _photo

    fun registration(login: String, password: String, name: String, avatar: File? = null) =
        viewModelScope.launch {
            try {
                if (avatar == null) {
                    repository.registerUser(login, password, name)
                } else {
                    repository.registerWithPhoto(login, password, name, MediaUpload(avatar))
                }
                _dataState.value = FeedModelState(authState = true)
            } catch (e: ServerError) {
                _dataState.value = FeedModelState(server = true)
            } catch (e: NetworkError) {
                _dataState.value = FeedModelState(network = true)
            } catch (e: UnknownError) {
                _dataState.value = FeedModelState(error = true)
            }
        }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

    fun reset() {
        _dataState.value = FeedModelState()
    }
}
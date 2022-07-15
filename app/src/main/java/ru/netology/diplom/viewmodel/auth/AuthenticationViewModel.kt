package ru.netology.diplom.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.diplom.error.NetworkError
import ru.netology.diplom.error.ServerError
import ru.netology.diplom.error.UnknownError
import ru.netology.diplom.model.state.FeedModelState
import ru.netology.diplom.repository.auth.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _dataState = MutableLiveData(FeedModelState())
    val dataState: LiveData<FeedModelState> = _dataState

    fun authentication(login: String, password: String) = viewModelScope.launch {
        try {
            repository.authentication(login, password)
            _dataState.value = FeedModelState(authState = true)
        } catch (e: ServerError) {
            _dataState.value = FeedModelState(server = true)
        } catch (e: NetworkError) {
            _dataState.value = FeedModelState(network = true)
        } catch (e: UnknownError) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun reset() {
        _dataState.value = FeedModelState()
    }
}
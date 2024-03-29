package ru.netology.diplom.viewmodel.auth

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.diplom.authorization.AppAuth
import ru.netology.diplom.data.dto.User
import ru.netology.diplom.model.state.AuthState
import ru.netology.diplom.model.state.FeedModelState
import ru.netology.diplom.repository.auth.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val appAuth: AppAuth
) : ViewModel() {

    val data = repository.data.asLiveData(Dispatchers.Default)

    val dataAuth: LiveData<AuthState> = appAuth.authStateFlow.asLiveData(Dispatchers.Default)
    val authenticated: Boolean get() = appAuth.authStateFlow.value.id != 0L

    private val _dataState = MutableLiveData(FeedModelState())
    val dataState: LiveData<FeedModelState> = _dataState

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _userIds = MutableLiveData<Set<Long>>()
    val userIds: LiveData<Set<Long>> = _userIds

    init {
        loadUsers()
    }

    fun loadUser(id: Long) = viewModelScope.launch {
        if (id == 0L) {
            _user.value = null
        } else {
            try {
                _user.value = repository.getUserById(id)
            } catch (e: Exception) {
                _user.value = null
            }
        }
    }

    private fun loadUsers() = viewModelScope.launch {
        try {
            repository.getAllUsers()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun getUsersIds(ids: Set<Long>) = viewModelScope.launch {
        _userIds.value = ids
    }
}
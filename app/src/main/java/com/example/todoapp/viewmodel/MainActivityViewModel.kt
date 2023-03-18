package com.example.todoapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.models.Task
import com.example.todoapp.models.User
import com.example.todoapp.repository.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivityViewModel(private val repository: TodoRepository) : ViewModel() {

    private val _mutableLiveDataLoggedInUser = MutableLiveData<User>()
    val liveDataLoggedInUser: LiveData<User>
        get() = _mutableLiveDataLoggedInUser

    private val _mutableLiveDataIsUserLoggedIn = MutableLiveData<Boolean>()
    val liveDataIsUserLoggedIn: LiveData<Boolean>
        get() = _mutableLiveDataIsUserLoggedIn


    private val _mutableLiveDataTasksList = MutableLiveData<List<Task>>()
    val liveDataTasksList: LiveData<List<Task>>
        get() = _mutableLiveDataTasksList


    fun getLoggedInUser() {

        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.getLoggedInUser()

            _mutableLiveDataLoggedInUser.postValue(user)
        }

    }

    fun logOutUser() {
        viewModelScope.launch(Dispatchers.IO) {

            delay(1000)
            val logoutStatus = repository.logout()
            if (logoutStatus) {
                _mutableLiveDataIsUserLoggedIn.postValue(false)
            }
        }
    }

    fun fetchTasksOfUser(userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {

            delay(1000)
            val tasksList = repository.fetchTasksOfUser(userId)
            _mutableLiveDataTasksList.postValue(tasksList)

        }

    }
}
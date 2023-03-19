package com.example.todoapp.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.models.Category
import com.example.todoapp.models.Task
import com.example.todoapp.models.User
import com.example.todoapp.repository.TodoRepository
import com.example.todoapp.sealedClasses.TaskOperation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class MainActivityViewModel(private val repository: TodoRepository) : ViewModel() {

    private val _mutableLiveDataLoggedInUser = MutableLiveData<User>()
    val liveDataLoggedInUser: LiveData<User>
        get() = _mutableLiveDataLoggedInUser

    private val _mutableLiveDataTaskOperation = MutableLiveData<TaskOperation>()
    val liveDataTaskOperation: LiveData<TaskOperation>
        get() = _mutableLiveDataTaskOperation

    private val _mutableLiveDataShowProgressBar = MutableLiveData<Boolean>()
    val liveDataShowProcessBar: LiveData<Boolean>
        get() = _mutableLiveDataShowProgressBar

    private val _mutableLiveDataIsUserLoggedIn = MutableLiveData<Boolean>()
    val liveDataIsUserLoggedIn: LiveData<Boolean>
        get() = _mutableLiveDataIsUserLoggedIn


    private val _mutableLiveDataTasksList = MutableLiveData<List<Task>>()
    val liveDataTasksList: LiveData<List<Task>>
        get() = _mutableLiveDataTasksList

    private val _mutableLiveDataTemp = MutableLiveData<List<Task>>()
    val liveDataTemp: LiveData<List<Task>>
        get() = _mutableLiveDataTemp

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

//            delay(1000)
            val tasksList = repository.fetchTasksOfUser(userId)
//            _mutableLiveDataTasksList.postValue(tasksList)
            _mutableLiveDataTaskOperation.postValue(TaskOperation.onSuccessFetchAllTasks(tasksList))

            delay(1000)
            _mutableLiveDataTaskOperation.postValue(TaskOperation.onNullOperation("done"))
        }

    }

    fun updateTask(task: Task,position: Int)
    {
        viewModelScope.launch(Dispatchers.IO) {

            val rowsUpdated = repository.updateTask(task)

            if(rowsUpdated==1)
            {
                _mutableLiveDataTaskOperation.postValue(TaskOperation.onSuccessUpdateTask(task,position))
            }
            else
            {
                _mutableLiveDataTaskOperation.postValue((TaskOperation.onErrorUpdateTask("some error has occurred")))
            }

            delay(1000)
            _mutableLiveDataTaskOperation.postValue(TaskOperation.onNullOperation("null"))
        }

    }
    fun deleteTask(task: Task, position:Int) {

        viewModelScope.launch(Dispatchers.IO) {

            val rowsDeleted = repository.deleteTask(task.id)

            if(rowsDeleted==1)
            {
                _mutableLiveDataTaskOperation.postValue(TaskOperation.onSuccessDeleteTask(task,position))

            }
            else
            {
                _mutableLiveDataTaskOperation.postValue(TaskOperation.onErrorDeleteTask("delete failed"))
            }


            delay(1000)
            _mutableLiveDataTaskOperation.postValue(TaskOperation.onNullOperation("done"))
        }

    }

    fun createTask(
        title: String,
        description: String,
        listOfCategory: List<Category>,
        priority: String,
        remindTime: Date?,
        status: String,
        taskImage: Bitmap?
    ) {

        if(title == "")
        {
            _mutableLiveDataTaskOperation.postValue(TaskOperation.onErrorAddTask("Task Title is required"))
            return

        }

        _mutableLiveDataShowProgressBar.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {


            delay(1000)

            val taskId=repository.createTask(
                title,
                description,
                listOfCategory,
                priority,
                remindTime,
                status,
                taskImage
            )
            if(taskId==-1L)
            {
                _mutableLiveDataTaskOperation.postValue(TaskOperation.onErrorAddTask("Failed To add Task"))
            }
            else
            {
                val task=repository.getTask(taskId)
                _mutableLiveDataShowProgressBar.postValue(false)
                if(task!=null)
                {

                    _mutableLiveDataTaskOperation.postValue(TaskOperation.onSuccessAddTask(task))
                    delay(1000)
                    _mutableLiveDataTaskOperation.postValue(TaskOperation.onNullOperation("done"))
                    Log.e("MyTag", "createTask:mutable create task posted ", )

                }
            }

        }
    }
}
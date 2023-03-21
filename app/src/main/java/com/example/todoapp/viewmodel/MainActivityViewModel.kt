package com.example.todoapp.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.CustomApplication
import com.example.todoapp.models.Category
import com.example.todoapp.models.Task
import com.example.todoapp.models.User
import com.example.todoapp.repository.TodoRepository
import com.example.todoapp.sealedClasses.CategoryOperation
import com.example.todoapp.sealedClasses.TaskOperation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class MainActivityViewModel(private val repository: TodoRepository) : ViewModel() {

    private val TAG = "MyTag"

    private val _mutableLiveDataLoggedInUser = MutableLiveData<User>()
    val liveDataLoggedInUser: LiveData<User>
        get() = _mutableLiveDataLoggedInUser

    private val _mutableLiveDataTaskOperation = MutableLiveData<TaskOperation>()
    val liveDataTaskOperation: LiveData<TaskOperation>
        get() = _mutableLiveDataTaskOperation

    private val _mutableLiveDataCategoryOperation = MutableLiveData<CategoryOperation>()
    val liveDataCategoryOperation: LiveData<CategoryOperation>
        get() = _mutableLiveDataCategoryOperation
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

    private var tasksList = ArrayList<Task>()
    private var categoriesList = ArrayList<Category>()

    init {

        if (CustomApplication.loggedInUser != null) {
            fetchTasksOfUser(CustomApplication.loggedInUser!!.id)
//            fetchAllCategoriesOfUser(CustomApplication.loggedInUser!!.id)
        }

    }

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

    private fun fetchTasksOfUser(userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.e(TAG, "fetchTasksOfUser: ")
            tasksList = repository.fetchTasksOfUser(userId)
            _mutableLiveDataTaskOperation.postValue(TaskOperation.OnSuccessFetchAllTasks(tasksList))
        }

    }

    private fun fetchAllCategoriesOfUser(userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.e(TAG, "fetchCategoriesOfUser: ")
            categoriesList = repository.fetchAllCategoriesOfUser(userId)
            _mutableLiveDataCategoryOperation.postValue(
                CategoryOperation.OnSuccessFetchAllCategories(
                    categoriesList
                )
            )
        }
    }

    fun fetchAllCategoriesOfUser() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.e(TAG, "fetchCategoriesOfUser: ")
            categoriesList = repository.fetchAllCategoriesOfUser()
            _mutableLiveDataCategoryOperation.postValue(
                CategoryOperation.OnSuccessFetchAllCategories(
                    categoriesList
                )
            )


        }
    }

    fun updateTask(task: Task, position: Int) {
        viewModelScope.launch(Dispatchers.IO) {

            val rowsUpdated = repository.updateTask(task)

            if (rowsUpdated == 1) {
                _mutableLiveDataTaskOperation.postValue(
                    TaskOperation.OnSuccessUpdateTask(
                        task,
                        position
                    )
                )
            } else {
                _mutableLiveDataTaskOperation.postValue((TaskOperation.OnErrorUpdateTask("some error has occurred")))
            }

            delay(1000)
            _mutableLiveDataTaskOperation.postValue(TaskOperation.OnSuccessFetchAllTasks(tasksList))
        }

    }

    fun deleteTask(task: Task, position: Int) {

        viewModelScope.launch(Dispatchers.IO) {

            val rowsDeleted = repository.deleteTask(task.id)

            if (rowsDeleted == 1) {
                _mutableLiveDataTaskOperation.postValue(
                    TaskOperation.OnSuccessDeleteTask(
                        task,
                        position
                    )
                )

            } else {
                _mutableLiveDataTaskOperation.postValue(TaskOperation.OnErrorDeleteTask("delete failed"))
            }


            delay(1000)
            _mutableLiveDataTaskOperation.postValue(TaskOperation.OnSuccessFetchAllTasks(tasksList))
        }

    }

    fun createTask(
        title: String,
        description: String,
        listOfCategory: List<Category>,
        priority: String,
        isReminderSet: Boolean,
        remindTime: Calendar?,
        status: String,
        taskImage: Bitmap?
    ) {

        if (title == "") {
            _mutableLiveDataTaskOperation.postValue(TaskOperation.OnErrorAddTask("Task Title is required"))
            viewModelScope.launch {
                delay(1000)
                _mutableLiveDataTaskOperation.postValue(
                    TaskOperation.OnSuccessFetchAllTasks(
                        tasksList
                    )
                )
            }
            return
        }

        _mutableLiveDataShowProgressBar.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {


            delay(1000)

            val taskId = repository.createTask(
                title,
                description,
                listOfCategory,
                priority,
                isReminderSet,
                remindTime,
                status,
                taskImage
            )
            if (taskId == -1L) {
                _mutableLiveDataTaskOperation.postValue(TaskOperation.OnErrorAddTask("Failed To add Task"))
            } else {
                val task = repository.getTask(taskId)
                _mutableLiveDataShowProgressBar.postValue(false)
                if (task != null) {

                    _mutableLiveDataTaskOperation.postValue(TaskOperation.OnSuccessAddTask(task))
                    delay(1000)
                    _mutableLiveDataTaskOperation.postValue(
                        TaskOperation.OnSuccessFetchAllTasks(
                            tasksList
                        )
                    )
                    Log.e("MyTag", "createTask:mutable create task posted ")

                }
            }
        }
    }

    fun createCategory(name: String, description: String, imageBitmap: Bitmap?) {
        if (name == "") {
            _mutableLiveDataCategoryOperation.postValue(CategoryOperation.OnErrorAddCategory("Category Name is required"))
            viewModelScope.launch {
                delay(1000)
                _mutableLiveDataCategoryOperation.postValue(
                    CategoryOperation.OnSuccessFetchAllCategories(
                        categoriesList
                    )
                )
            }
            return
        }


        viewModelScope.launch(Dispatchers.IO) {


            val categoryId = repository.createCategory(
                name,
                description,
                imageBitmap,
            )
            if (categoryId == -1L) {
                _mutableLiveDataCategoryOperation.postValue(CategoryOperation.OnErrorAddCategory("Failed To add Category"))
            } else {
                val category = repository.getCategory(categoryId)
                if (category != null) {

                    _mutableLiveDataCategoryOperation.postValue(
                        CategoryOperation.OnSuccessAddCategory(
                            category
                        )
                    )
                    delay(1000)
                    categoriesList.add(category)
                    _mutableLiveDataCategoryOperation.postValue(
                        CategoryOperation.OnSuccessFetchAllCategories(
                            categoriesList
                        )
                    )
                    Log.e("MyTag", "createTask:mutable create task posted ")

                }
            }
        }

    }
}
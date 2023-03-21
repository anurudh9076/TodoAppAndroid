package com.example.todoapp.sealedClasses

import com.example.todoapp.models.Category
import com.example.todoapp.models.Task

sealed class TaskOperation {

    data class OnSuccessFetchAllTasks(val list: ArrayList<Task>) : TaskOperation()
    data class OnSuccessUpdateTask(val task: Task, val position: Int) : TaskOperation()
    data class OnSuccessDeleteTask(val task: Task, val position: Int) : TaskOperation()
    data class OnSuccessAddTask(val task: Task) : TaskOperation()


    data class OnErrorFetchAllCTask(val error: String) : TaskOperation()
    data class OnErrorUpdateTask(val error: String) : TaskOperation()
    data class OnErrorDeleteTask(val error: String) : TaskOperation()
    data class OnErrorAddTask(val error: String) : TaskOperation()

    data class OnNullOperation(val error: String) : TaskOperation()

}
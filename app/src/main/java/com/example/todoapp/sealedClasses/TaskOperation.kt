package com.example.todoapp.sealedClasses

import com.example.todoapp.models.Task

sealed class TaskOperation {

    data class onSuccessFetchAllTasks(val list: ArrayList<Task>) : TaskOperation()
    data class onSuccessUpdateTask(val task: Task, val position: Int) : TaskOperation()
    data class onSuccessDeleteTask(val task: Task, val position: Int) : TaskOperation()
    data class onSuccessAddTask(val task: Task) : TaskOperation()

    data class onErrorFetchAllCTask(val error: String) : TaskOperation()
    data class onErrorUpdateTask(val error: String) : TaskOperation()
    data class onErrorDeleteTask(val error: String) : TaskOperation()
    data class onErrorAddTask(val error: String) : TaskOperation()

    data class onNullOperation(val error: String) : TaskOperation()

}
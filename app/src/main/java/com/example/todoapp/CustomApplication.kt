package com.example.todoapp

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import com.example.todoapp.DbHelper.TodoDBHelper
import com.example.todoapp.constants.Constants
import com.example.todoapp.repository.TodoRepository

class CustomApplication :Application(){
    companion object{
        lateinit var  sharedPreferences:SharedPreferences
        lateinit var  todoRepository :TodoRepository
        lateinit var dbHelper:TodoDBHelper

    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MyTag", "onCreate:application ")
        sharedPreferences=getSharedPreferences(Constants.USER_LOGIN_STATE_PREFERENCE, MODE_PRIVATE)

        dbHelper = TodoDBHelper.getInstance(this)
         todoRepository = TodoRepository(dbHelper)

//       val taskId= dbHelper.createTask("do some stuff","You have to complete this",Constants.Priority.TASK_PRIORITY_LOW,null,Constants.Status.NOT_STARTED,null,2)
//        dbHelper.createTask("do some stuff1","You have to complete this",Constants.Priority.TASK_PRIORITY_LOW,null,Constants.Status.NOT_STARTED,null,2)
//        dbHelper.createTask("do some stuff2","You have to complete this",Constants.Priority.TASK_PRIORITY_LOW,null,Constants.Status.NOT_STARTED,null,2)
//        dbHelper.createTask("do some stuff3","You have to complete this",Constants.Priority.TASK_PRIORITY_LOW.priority,null,Constants.Status.NOT_STARTED.value,null,2)
//        val task= dbHelper.getTask(taskId)
//        Log.d("MyTag", "onCreate:task->  "+task.toString())

        val tasksList= dbHelper.fetchAllTasksOfUser(2)

        for(task in tasksList)
        {
            Log.e("MyTag", "Task:-> $task")
        }

//        dbHelper.deleteTask(4)
//
//
//        val tasksList1= dbHelper.fetchAllTasksOfUser(2)
//
//        for(task in tasksList1)
//        {
//            Log.e("MyTag", "Task:-> $task")
//        }


    }


}
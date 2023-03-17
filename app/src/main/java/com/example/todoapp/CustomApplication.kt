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

    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MyTag", "onCreate:application ")
        sharedPreferences=getSharedPreferences(Constants.USER_LOGIN_STATE_PREFERENCE, MODE_PRIVATE)

        val dbHelper = TodoDBHelper.getInstance(this)
         todoRepository = TodoRepository(dbHelper)


    }


}
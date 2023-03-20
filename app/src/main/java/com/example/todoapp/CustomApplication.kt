package com.example.todoapp

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.DbHelper.TodoDBHelper
import com.example.todoapp.constants.Constants
import com.example.todoapp.models.User
import com.example.todoapp.repository.TodoRepository
import com.example.todoapp.viewmodel.MainActivityViewModel
import com.example.todoapp.viewmodel.MainActivityViewModelFactory

class CustomApplication :Application(){
    companion object{
        lateinit var  sharedPreferences:SharedPreferences
        lateinit var  todoRepository :TodoRepository
        lateinit var mainActivityViewModel: MainActivityViewModel
        lateinit var dbHelper:TodoDBHelper
        var loggedInUser: User?=null

    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MyTag", "onCreate:application ")
        sharedPreferences=getSharedPreferences(Constants.USER_LOGIN_STATE_PREFERENCE, MODE_PRIVATE)

        dbHelper = TodoDBHelper.getInstance(this)
         todoRepository = TodoRepository(dbHelper)

        loggedInUser= todoRepository.getLoggedInUser()


    }


}
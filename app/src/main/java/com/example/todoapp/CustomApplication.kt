package com.example.todoapp

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.todoapp.constants.Constants

class CustomApplication :Application(){
    companion object{
        lateinit var  sharedPreferences:SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MyTag", "onCreate:application ")
        sharedPreferences=getSharedPreferences(Constants.USER_LOGIN_STATE_PREFERENCE, AppCompatActivity.MODE_PRIVATE)
    }
}
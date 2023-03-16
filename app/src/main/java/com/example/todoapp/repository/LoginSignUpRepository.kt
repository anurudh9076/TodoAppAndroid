package com.example.todoapp.repository

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import com.example.todoapp.CustomApplication
import com.example.todoapp.DbHelper.TodoDBHelper
import com.example.todoapp.constants.Constants

class LoginSignUpRepository(private val dbHelper: TodoDBHelper) {

    fun singUp(name:String,email:String,password:String,image_bitmap: Bitmap?):Long
    {
       return dbHelper.createUser(name,email,password,image_bitmap)
    }


    fun login(email:String,password:String):Long
    {


        val userId=dbHelper.login(email,password)
        if(userId!=-1L)
        {
            val pref = CustomApplication.sharedPreferences
            val editor = pref.edit()
            editor.putBoolean(Constants.USER_LOGGED_IN, true)
            editor.putLong(Constants.USER_ID,userId)
            editor.apply()
        }

            return userId
    }

}
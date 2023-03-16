package com.example.todoapp.repository

import android.graphics.Bitmap
import com.example.todoapp.CustomApplication
import com.example.todoapp.DbHelper.TodoDBHelper
import com.example.todoapp.constants.Constants
import com.example.todoapp.models.User

class TodoRepository(private val dbHelper: TodoDBHelper) {

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
            editor.putBoolean(Constants.IS_USER_LOGGED_IN, true)
            editor.putLong(Constants.USER_ID,userId)
            editor.apply()
        }

            return userId
    }

    fun logout():Boolean
    {

            val pref = CustomApplication.sharedPreferences
            val editor = pref.edit()
            editor.putBoolean(Constants.IS_USER_LOGGED_IN, false)
            editor.putLong(Constants.USER_ID,-1L)
            editor.apply()
            return true


    }
    fun getLoggedInUser():User?
    {
        val pref = CustomApplication.sharedPreferences

        val userId=pref.getLong(Constants.USER_ID,-1L)
        if(userId==-1L)
        {
            return null
        }

        return dbHelper.getUser(userId)

    }

}
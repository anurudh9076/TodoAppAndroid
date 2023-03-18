package com.example.todoapp.repository

import android.graphics.Bitmap
import com.example.todoapp.CustomApplication
import com.example.todoapp.DbHelper.TodoDBHelper
import com.example.todoapp.constants.Constants
import com.example.todoapp.models.Task
import com.example.todoapp.models.User

class TodoRepository(private val dbHelper: TodoDBHelper) {

    fun singUp(name:String,email:String,password:String,image_bitmap: Bitmap?):Long
    {
        val userId=dbHelper.createUser(name,email,password,image_bitmap)

        if(userId!=-1L)
        {
            createDefaultCategories(userId)
        }
        return  userId
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

    fun createCategory( name:String,description:String,imageBitmap:Bitmap?, userId:Long): Long
    {
        return dbHelper.createCategory(name,description,imageBitmap,userId)
    }

//    fun createTask( title:String,description:String,priority: Constants.Priority,reminderTime:Date, userId:Long): Long
//    {
//        return dbHelper.createTask(name,description,imageBitmap,userId)
//    }


    private fun createDefaultCategories(userId:Long)
    {
            dbHelper.createCategory("android","android related category",null,userId)
            dbHelper.createCategory("tech","tech related category",null,userId)
            dbHelper.createCategory("desktop","desktop related category",null,userId)
            dbHelper.createCategory("backend","backend related category",null,userId)


    }

    fun fetchTasksOfUser(userId: Long):List<Task> {

        return dbHelper.fetchAllTasksOfUser(userId)
    }


}
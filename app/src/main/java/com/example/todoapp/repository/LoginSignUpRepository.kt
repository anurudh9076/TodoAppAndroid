package com.example.todoapp.repository

import android.graphics.Bitmap
import com.example.todoapp.DbHelper.TodoDBHelper

class LoginSignUpRepository(private val dbHelper: TodoDBHelper) {

    fun singUp(name:String,email:String,password:String,image_bitmap: Bitmap?):Long
    {
       return dbHelper.createUser(name,email,password,image_bitmap)
    }


    fun login(email:String,password:String):Long
    {
            return dbHelper.login(email,password)
    }

}
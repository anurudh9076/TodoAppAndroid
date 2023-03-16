package com.example.todoapp.repository

import android.graphics.Bitmap
import com.example.todoapp.DbHelper.TodoDBHelper
import com.example.todoapp.models.User

class SignUpRepository(private val dbHelper: TodoDBHelper) {

    fun singUp(name:String,email:String,password:String,image_bitmap: Bitmap?):Long
    {
       return dbHelper.createUser(name,email,password,image_bitmap)
    }

}
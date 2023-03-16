package com.example.todoapp.repository

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import com.example.todoapp.DbHelper.TodoDBHelper

class LoginSignUpRepository(private val dbHelper: TodoDBHelper,private val context: Context) {

    fun singUp(name:String,email:String,password:String,image_bitmap: Bitmap?):Long
    {
       return dbHelper.createUser(name,email,password,image_bitmap)
    }


    fun login(email:String,password:String):Long
    {


        val userId=dbHelper.login(email,password)
        if(userId!=-1L)
        {
            val pref = context.getSharedPreferences("login", AppCompatActivity.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putBoolean("user_logged_in", true)
            editor.putLong("user_id",userId)
            editor.apply()
        }

            return userId
    }

}
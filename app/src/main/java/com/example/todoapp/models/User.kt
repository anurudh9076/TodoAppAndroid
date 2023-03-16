package com.example.todoapp.models

import android.graphics.Bitmap

data class User(
    var id:Long,
    var name:String,
    var email: String,
    var password: String,
    var image_id:Int,
    var image_bitmap:Bitmap?)
{

}
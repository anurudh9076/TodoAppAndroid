package com.example.todoapp.models

import android.graphics.Bitmap

data class User(
    var id:Long,
    var name:String,
    var email: String,
    var image_id:Long,
    var image_bitmap:Bitmap?)
{

}
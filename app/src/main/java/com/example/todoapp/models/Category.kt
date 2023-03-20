package com.example.todoapp.models

import android.graphics.Bitmap
import java.util.*

data class Category(
    val id:Long,
    val name:String,
    val description:String,
    val iconId:Long,
    val iconBitmap: Bitmap?,
    val userId:Long
) {
}


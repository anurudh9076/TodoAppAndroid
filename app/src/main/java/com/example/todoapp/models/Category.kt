package com.example.todoapp.models

import android.graphics.Bitmap
import java.util.*

data class Category(
    var id:Long,
    var name:String,
    var description:String,
    var iconId:Long,
    var iconBitmap: Bitmap?,
    var userId:Long
) :java.io.Serializable{
}


package com.example.todoapp.models

import android.graphics.Bitmap
import com.example.todoapp.constants.Constants
import java.util.Date

data class Task(
    var id:Long,
    var title:String,
    var description:String,
    var priority:Constants.Priority,
    var reminderTime:Date?,
    var status:Constants.Status,
    var imageId:Long,
    var imageBitmap: Bitmap?,
    var userId:Long

) {

}
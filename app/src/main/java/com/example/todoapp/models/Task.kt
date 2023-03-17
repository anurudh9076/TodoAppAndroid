package com.example.todoapp.models

import com.example.todoapp.constants.Constants
import java.util.Date

data class Task(
    val id:Long,
    val title:String,
    val description:String,
    val priority:Constants.Priority,
    val reminderTime:Date,
    val status:Constants.Status,
    val userId:Long

) {

}
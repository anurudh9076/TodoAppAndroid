package com.example.todoapp.models

import java.util.*

data class Category(
    val id:Long,
    val name:String,
    val description:String,
    val iconId:Long,
    val userId:Long
) {
}


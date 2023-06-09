package com.example.todoapp.viewmodel

import android.app.ActivityManager.TaskDescription
import android.graphics.Bitmap
import android.icu.text.CaseMap.Title
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.CustomApplication
import com.example.todoapp.constants.Constants
import com.example.todoapp.models.Category
import com.example.todoapp.repository.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date

class CreateTaskViewModel: ViewModel() {
    val repository:TodoRepository=CustomApplication.todoRepository
    private val _mutableLiveDataShowProgressBar= MutableLiveData<Boolean>()
    val liveDataShowProcessBar: LiveData<Boolean>
        get()=_mutableLiveDataShowProgressBar

//    fun createTask(title: String, description: String,listOfCategory:List<Category>,priority: String, remindTime: Date?,
//                   status:String,taskImage: Bitmap? )
//    {
//
//        _mutableLiveDataShowProgressBar.postValue(true)
//
//        viewModelScope.launch(Dispatchers.IO) {
//
//
//            delay(1000)
//
//            repository.createTask(title,description,listOfCategory,priority,remindTime, status, taskImage)
//            _mutableLiveDataShowProgressBar.postValue(false)
//        }
//
//
//    }
}
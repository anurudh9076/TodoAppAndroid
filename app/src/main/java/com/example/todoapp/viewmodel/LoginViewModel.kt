package com.example.todoapp.viewmodel

import android.graphics.Bitmap
import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todoapp.models.User
import kotlin.math.log

class LoginViewModel : ViewModel() {
    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String>
        get() = _errorLiveData

    private val _progressBarLiveData = MutableLiveData<Boolean>()
    val progressBarLiveData: LiveData<Boolean>
        get() = _progressBarLiveData



    fun loginUser(email: String, password: String) {
        //input validation
        if (email.isEmpty() || password.isEmpty()) {
            //failure
            _errorLiveData.value = "Please enter email and password."
            return
        }




//        _progressBarLiveData.value = true
//        //use coroutines
//        {
//            repo.login(email, pass)
//        }

    }

    fun signUpForTodo(name: String, email: String, password: String, image: Bitmap) {

    }

}
package com.example.todoapp.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.repository.SignUpRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class SignUpViewModel(private val repository: SignUpRepository) :ViewModel() {

    private val _mutableLiveDataIsSigningUp=MutableLiveData<Boolean>()
    val liveDataIsSigningUp:LiveData<Boolean>
    get()=_mutableLiveDataIsSigningUp


    private val _mutableLiveDataSignUpStatus=MutableLiveData<Boolean>()
    val liveDataSignUpStatus:LiveData<Boolean>
        get()=_mutableLiveDataSignUpStatus

    fun signUp(name:String,email:String,password:String,image_bitmap: Bitmap?)
    {
        _mutableLiveDataIsSigningUp.value=true

        viewModelScope.launch(Dispatchers.IO) {

            val insertedRowId=repository.singUp(name,email,password,image_bitmap)
            if(insertedRowId==-1L)
                _mutableLiveDataSignUpStatus.postValue(true)
            else
                _mutableLiveDataSignUpStatus.postValue(false)

            _mutableLiveDataIsSigningUp.postValue(false)

        }

    }

}
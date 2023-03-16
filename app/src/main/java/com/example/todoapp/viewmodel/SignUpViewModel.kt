package com.example.todoapp.viewmodel

import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.repository.LoginSignUpRepository
import kotlinx.coroutines.*

class SignUpViewModel(private val repository: LoginSignUpRepository) :ViewModel() {

    private val _mutableLiveDataIsSigningUp=MutableLiveData<Boolean>()
    val liveDataIsSigningUp:LiveData<Boolean>
    get()=_mutableLiveDataIsSigningUp


    private val _mutableLiveDataSignUpStatus=MutableLiveData<String>()
    val liveDataSignUpStatus:LiveData<String>
        get()=_mutableLiveDataSignUpStatus

    fun signUp(name:String,email:String,password:String,image_bitmap: Bitmap?)
    {
        if(name.isEmpty()||email.isEmpty()||password.isEmpty())
        {
           _mutableLiveDataSignUpStatus.value="All field are required"
            return
        }
        else if(!isValidEmail(email))
        {
            _mutableLiveDataSignUpStatus.value="please enter a valid email"
            return
        }
        else if(password.length<6)
        {
            _mutableLiveDataSignUpStatus.value="password should contain min 6 char"
            return
        }

        else
        {
            _mutableLiveDataIsSigningUp.value=true

            viewModelScope.launch(Dispatchers.IO) {

                delay(1000)
                val insertedRowId=repository.singUp(name,email,password,image_bitmap)

                if(insertedRowId!=-1L)
                    _mutableLiveDataSignUpStatus.postValue("success")
                else
                    _mutableLiveDataSignUpStatus.postValue("failed")

                _mutableLiveDataIsSigningUp.postValue(false)

            }


        }


    }

    private fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }


}
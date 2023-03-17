package com.example.todoapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.repository.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: TodoRepository) : ViewModel() {


    private val _mutableLiveDataIsLoggingIn=MutableLiveData<Boolean>()
    val liveDataIsLoggingIn:LiveData<Boolean>
        get()=_mutableLiveDataIsLoggingIn


    private val _mutableLiveDataLoginStatus=MutableLiveData<String>()
    val liveDataLoginStatus:LiveData<String>
        get()=_mutableLiveDataLoginStatus



    fun loginUser(email: String, password: String) {

        if(email.isEmpty()||password.isEmpty())
        {
            _mutableLiveDataLoginStatus.value="All field are required"
            return
        }


        else
        {
            _mutableLiveDataIsLoggingIn.value=true

            viewModelScope.launch(Dispatchers.IO) {

                delay(1000)

                val userId=repository.login(email,password)

                if(userId!=-1L)
                {
                    _mutableLiveDataLoginStatus.postValue("success")
                }
                else
                    _mutableLiveDataLoginStatus.postValue("failed")
                _mutableLiveDataIsLoggingIn.postValue(false)

            }


        }

    }


}
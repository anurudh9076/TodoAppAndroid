package com.example.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.repository.LoginSignUpRepository

class SignUpViewModelFactory(private val signupRepositoryLogin: LoginSignUpRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return SignUpViewModel(signupRepositoryLogin) as T
    }
}
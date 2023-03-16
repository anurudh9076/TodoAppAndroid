package com.example.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.repository.LoginSignUpRepository


class LoginViewModelFactory(private val signupRepositoryLogin: LoginSignUpRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return LoginViewModel(signupRepositoryLogin) as T
    }
}
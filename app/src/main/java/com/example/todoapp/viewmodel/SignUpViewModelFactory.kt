package com.example.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.repository.TodoRepository

class SignUpViewModelFactory(private val repository: TodoRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return SignUpViewModel(repository) as T
    }
}
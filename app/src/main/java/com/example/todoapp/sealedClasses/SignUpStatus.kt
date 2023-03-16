package com.example.todoapp.sealedClasses

sealed class SignUpStatus(val status:String)
{

    companion object
    {
        class Success: SignUpStatus("success")
        class Failed: SignUpStatus("failed")
        class WrongPassword:SignUpStatus("password should contain min 6 char")
        class InvalidEmail:SignUpStatus("please enter a valid email")
        class EmptyField:SignUpStatus("All field are required")

    }


}

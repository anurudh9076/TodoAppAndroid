package com.example.todoapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todoapp.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnSignUp.setOnClickListener{
            val intent=Intent(this,SignupActivity::class.java)
            startActivity(intent)

        }


        binding.btnLoginPasswordVisibility.setOnClickListener {

            if(binding.edtLoginPassword.inputType==InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                binding.edtLoginPassword.inputType=InputType.TYPE_TEXT_VARIATION_PASSWORD.or(InputType.TYPE_CLASS_TEXT)
            else
            binding.edtLoginPassword.inputType=InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.edtLoginPassword.setSelection(binding.edtLoginPassword.text.length);
        }

        binding.btnLogin.setOnClickListener {
            //get the data email and password
            //send to viewmodel
        }

    }
}
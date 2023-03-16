package com.example.todoapp.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.DbHelper.TodoDBHelper
import com.example.todoapp.MainActivity
import com.example.todoapp.databinding.ActivityLoginBinding
import com.example.todoapp.repository.LoginSignUpRepository
import com.example.todoapp.viewmodel.LoginViewModel
import com.example.todoapp.viewmodel.LoginViewModelFactory


class LoginActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding

    lateinit var loginViewModel: LoginViewModel
    private var imageBitmap: Bitmap? = null
    private lateinit var progressBar: ProgressBar
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = ProgressBar(this)
        val dbHelper = TodoDBHelper.getInstance(this)
        val loginSignUpRepository = LoginSignUpRepository(dbHelper)
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory(loginSignUpRepository))[LoginViewModel::class.java]


        setOnClickListeners()
        setObservers()


    }


    private fun setOnClickListeners()
    {


        binding.btnSignUp.setOnClickListener{
            val intent=Intent(this, SignupActivity::class.java)
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

            val email = binding.edtLoginEmail.text.toString()
            val password = binding.edtLoginPassword.text.toString()



            loginViewModel.loginUser(email,password)
        }

    }

    private fun setObservers() {
        loginViewModel.liveDataIsLoggingIn.observe(this) {

            if (it == true)
                binding.progressBarLogin.visibility = View.VISIBLE
            else
                binding.progressBarLogin.visibility = View.GONE

        }
        loginViewModel.liveDataLoginStatus.observe(this)
        {
            when (it) {
                "failed" -> Toast.makeText(
                    this,
                    "Login failed \nplease try again",
                    Toast.LENGTH_LONG
                ).show()
                "success" -> {
                    Toast.makeText(this, "successfully logged In", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                }

                else -> {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}
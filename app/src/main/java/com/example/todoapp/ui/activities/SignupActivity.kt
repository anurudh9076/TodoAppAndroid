package com.example.todoapp.ui.activities

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.DbHelper.TodoDBHelper
import com.example.todoapp.databinding.ActivitySignupBinding
import com.example.todoapp.repository.LoginSignUpRepository
import com.example.todoapp.viewmodel.SignUpViewModel
import com.example.todoapp.viewmodel.SignUpViewModelFactory


class SignupActivity : AppCompatActivity() {
    private val TAG = "MyTag"
    private lateinit var binding: ActivitySignupBinding
    private lateinit var signUpViewModel: SignUpViewModel
    private var imageBitmap: Bitmap? = null
    private lateinit var progressBar: ProgressBar


    private val chooseImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.data?.let {
                val imageUri: Uri = it
                var bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                Log.d(TAG, ":hello ")
                imageBitmap = bitmap
                binding.imgViewSignup.setImageBitmap(bitmap)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = ProgressBar(this)
        val dbHelper = TodoDBHelper.getInstance(this)
        val loginSignUpRepository = LoginSignUpRepository(dbHelper)
        signUpViewModel = ViewModelProvider(
            this,
            SignUpViewModelFactory(loginSignUpRepository)
        )[SignUpViewModel::class.java]


        setOnClickListeners()
        setObservers()

//        progressBar.visibility=View.GONE


    }

    private fun setOnClickListeners() {
        binding.btnSignupPasswordVisibility.setOnClickListener {

            if (binding.edtSignupPassword.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                binding.edtSignupPassword.inputType =
                    InputType.TYPE_TEXT_VARIATION_PASSWORD.or(InputType.TYPE_CLASS_TEXT)
            else
                binding.edtSignupPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.edtSignupPassword.setSelection(binding.edtSignupPassword.text.length);
        }

        binding.btnSignIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.imgViewSignup.setOnClickListener {
            chooseImageFromGallery()
        }
        binding.btnCreate.setOnClickListener {

            val image = imageBitmap
            val name = binding.edtSignupName.text.toString()
            val email = binding.edtSignupEmail.text.toString()
            val password = binding.edtSignupPassword.text.toString()
            val confirmPassword = binding.edtSignupConfirmPassword.text.toString()



            signUpViewModel.signUp(name, email, password, confirmPassword, image)


        }

        binding.arrowBack.setOnClickListener {
            finish()
        }
    }

    private fun setObservers() {
        signUpViewModel.liveDataIsSigningUp.observe(this) {

            if (it == true)
                binding.progressBarSignUp.visibility = View.VISIBLE
            else
                binding.progressBarSignUp.visibility = View.GONE

        }
        signUpViewModel.liveDataSignUpStatus.observe(this)
        {
            when (it) {
                "failed" -> Toast.makeText(
                    this,
                    "User creation failed\nplease try again",
                    Toast.LENGTH_LONG
                ).show()

                "success" -> {
                    Toast.makeText(this, "User successfully created", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }

                else -> {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun chooseImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        chooseImageLauncher.launch(intent)
    }


}
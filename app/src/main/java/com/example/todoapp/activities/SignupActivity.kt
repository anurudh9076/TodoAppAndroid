package com.example.todoapp.activities

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
import com.example.todoapp.repository.SignUpRepository
import com.example.todoapp.viewmodel.SignUpViewModel
import com.example.todoapp.viewmodel.SignUpViewModelFactory


class SignupActivity : AppCompatActivity() {
    private val TAG = "MyTag"
    private lateinit var binding:ActivitySignupBinding
    lateinit var signUpViewModel:SignUpViewModel
    private var imageBitmap:Bitmap?=null
    private val progressBar=ProgressBar(this)


    private val chooseImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        it.data?.data?.let {
            val imageUri: Uri = it
            var bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            Log.d(TAG, ":hello ")
            imageBitmap=bitmap
            binding.imgViewSignup.setImageBitmap(bitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setOnClickListeners()
        setObservers()
//        progressBar.visibility=View.GONE

        val dbHelper=TodoDBHelper.getInstance(this)
        val signUpRepository=SignUpRepository(dbHelper)
        signUpViewModel= ViewModelProvider(this,SignUpViewModelFactory(signUpRepository))[SignUpViewModel::class.java]



    }

    private fun setOnClickListeners()
    {
        binding.btnSignupPasswordVisibility.setOnClickListener {

            if(binding.edtSignupPassword.inputType== InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                binding.edtSignupPassword.inputType=
                    InputType.TYPE_TEXT_VARIATION_PASSWORD.or(InputType.TYPE_CLASS_TEXT)
            else
                binding.edtSignupPassword.inputType= InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.edtSignupPassword.setSelection(binding.edtSignupPassword.text.length);
        }

        binding.btnSignIn.setOnClickListener {
            val intent= Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        binding.imgViewSignup.setOnClickListener {
                chooseImageFromGallery()
        }
        binding.btnCreate.setOnClickListener {

            val image=imageBitmap
            val name=binding.edtSignupName.text.toString()
            val email=binding.edtSignupEmail.text.toString()
            val password=binding.edtSignupPassword.text.toString()


            signUpViewModel.signUp(name,email,password,image)

        }
    }

    private fun setObservers()
    {
        signUpViewModel.liveDataIsSigningUp.observe(this) {

            if(it==true)
                progressBar.visibility=View.VISIBLE
            else
                progressBar.visibility=View.GONE

        }
        signUpViewModel.liveDataSignUpStatus.observe(this)
        {
            if(it==false)
            {

            }
        }
    }

    private fun chooseImageFromGallery() {
        val intent=Intent()
        intent.type = "image/*"

        intent.action = Intent.ACTION_GET_CONTENT
        chooseImageLauncher.launch(intent)
    }


}
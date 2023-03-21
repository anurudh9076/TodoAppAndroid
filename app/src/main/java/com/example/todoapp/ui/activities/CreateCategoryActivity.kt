package com.example.todoapp.ui.activities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.CustomApplication
import com.example.todoapp.R
import com.example.todoapp.databinding.ActivityCreateCategoryBinding
import com.example.todoapp.sealedClasses.CategoryOperation
import com.example.todoapp.viewmodel.MainActivityViewModel
import com.example.todoapp.viewmodel.MainActivityViewModelFactory

class CreateCategoryActivity : AppCompatActivity() {
    private val TAG = "MyTag"
    private lateinit var _binding:ActivityCreateCategoryBinding
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private var categoryIconBitmap: Bitmap?=null



    private val registerForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.data?.let {
                val imageUri: Uri = it
                var bitmap: Bitmap
                if (Build.VERSION.SDK_INT < 28) {
                    bitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                    categoryIconBitmap = bitmap
                } else {
                    val source = ImageDecoder.createSource(this.contentResolver, imageUri)
                    bitmap = ImageDecoder.decodeBitmap(source)
                }

                categoryIconBitmap = bitmap
                _binding.ivCreateCategory.setImageBitmap(bitmap)
            }
        }
    companion object {
        private var contextMainActivity: Context? = null
        fun setContextForViewModel(context: FragmentActivity) {
            contextMainActivity = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityCreateCategoryBinding.inflate(layoutInflater)
        setContentView(_binding.root)


        val todoRepository = CustomApplication.todoRepository
        mainActivityViewModel = ViewModelProvider(
            contextMainActivity as AppCompatActivity,
            MainActivityViewModelFactory(todoRepository)
        )[MainActivityViewModel::class.java]


        setListeners()
        setObservers()

    }



    private fun setListeners()
    {
        _binding.arrowBack.setOnClickListener {
          finish()
        }
        _binding.ivCreateCategory.setOnClickListener {

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            registerForResult.launch(intent)
        }
        _binding.btnCreateCategory.setOnClickListener {
            val categoryName=_binding.edtCategoryName.text.toString()
            val categoryDescription=_binding.edtCategoryName.text.toString()
            mainActivityViewModel.createCategory(name = categoryName, description =categoryDescription , imageBitmap =categoryIconBitmap)
        }

    }

    private fun setObservers()
    {
        mainActivityViewModel.liveDataCategoryOperation.observe(this){
            when(it)
            {
                is CategoryOperation.OnSuccessAddCategory ->{
                    Toast.makeText(this,"Category Added Successfully",Toast.LENGTH_SHORT).show()
                    finish()
                }
                is CategoryOperation.OnErrorAddCategory -> {

                    Toast.makeText(this,"${it.error}",Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

}
package com.example.todoapp.ui.activities

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.todoapp.R
import com.example.todoapp.databinding.ActivityCreateCategoryBinding
import com.example.todoapp.viewmodel.MainActivityViewModel

class CreateCategoryActivity : AppCompatActivity() {
    private val TAG = "MyTag"
    private lateinit var _binding:ActivityCreateCategoryBinding
    private lateinit var mainActivityViewModel: MainActivityViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityCreateCategoryBinding.inflate(layoutInflater)
        setContentView(_binding.root)
    }



    private fun setListeners()
    {
        var categoryBitmap: Bitmap?=null
        _binding.arrowBack.setOnClickListener {
          finish()
        }

//        bindingCreatedCategory.ivCreateCategory.setOnClickListener {
//            val registerForResult =
//                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                    it.data?.data?.let {
//                        val imageUri: Uri = it
//                        var bitmap: Bitmap
//                        if (Build.VERSION.SDK_INT < 28) {
//                            bitmap =
//                                MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
//                            categoryBitmap = bitmap
//                        } else {
//                            val source = ImageDecoder.createSource(this.contentResolver, imageUri)
//                            bitmap = ImageDecoder.decodeBitmap(source)
//                        }
//
//                        categoryBitmap = bitmap
//                        bindingCreatedCategory.ivCreateCategory.setImageBitmap(bitmap)
//                    }
//                }
//            val intent = Intent()
//            intent.type = "image/*"
//            intent.action = Intent.ACTION_GET_CONTENT
//            registerForResult.launch(intent)
//        }
        _binding.btnCreateCategory.setOnClickListener {
            val categoryName=_binding.edtCategoryName.text.toString()
            val categoryDescription=_binding.edtCategoryName.text.toString()
            Log.e(TAG, "clicked: " )

        }

    }

}
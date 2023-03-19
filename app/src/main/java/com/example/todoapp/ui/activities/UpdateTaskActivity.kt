package com.example.todoapp.ui.activities

import android.R
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.drawToBitmap
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.CustomApplication
import com.example.todoapp.constants.Constants
import com.example.todoapp.databinding.ActivityCreateTaskBinding
import com.example.todoapp.models.Category
import com.example.todoapp.models.Task
import com.example.todoapp.sealedClasses.TaskOperation
import com.example.todoapp.viewmodel.MainActivityViewModel
import com.example.todoapp.viewmodel.MainActivityViewModelFactory
import javax.net.ssl.SSLEngineResult.Status

class UpdateTaskActivity : AppCompatActivity() {

    private val TAG = "MyTag"

    companion object {
        private var contextMainActivity: Context? = null
        fun setContext(context: FragmentActivity) {
            contextMainActivity = context
        }

    }

    private val arrayListTaskPriority = ArrayList<String>()
    private val arrayListTaskStatus = ArrayList<String>()
    private lateinit var taskPrioritySpinner: Spinner
    private lateinit var taskStatusSpinner: Spinner
    private lateinit var binding: ActivityCreateTaskBinding
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private var imageBitmap: Bitmap? = null
    private var task: Task? = null
    private var adapterPosition = -1

    private val chooseImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.data?.let {
                val imageUri: Uri = it
                var bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                imageBitmap = bitmap
                binding.ivCreateTask.setImageBitmap(bitmap)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTaskStatusSpinner()
        setTaskPrioritySpinner()

        task = intent.getSerializableExtra(Constants.TASK) as Task
        adapterPosition = intent.getIntExtra(Constants.ADAPTER_POSITION, -1)
        val byteArray = intent.getByteArrayExtra(Constants.IMAGE_BYTE_ARRAY)
        if (byteArray != null) {
            imageBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)

        }


        initView() //initialize all the views with given task values

        val todoRepository = CustomApplication.todoRepository
        mainActivityViewModel = ViewModelProvider(
            contextMainActivity as AppCompatActivity,
            MainActivityViewModelFactory(todoRepository)
        )[MainActivityViewModel::class.java]

        setOnClickListeners()
        setObservers()

    }

    private fun initView() {
        binding.btnDeleteTaskActivity.visibility = View.VISIBLE
        binding.spinnerTaskStatus.visibility = View.VISIBLE
        binding.tvTaskStatus.visibility = View.VISIBLE

        binding.btnCreateTask.text = "Update"
        binding.tvCreateTask.text = "Update Task"


        binding.ivCreateTask.setImageBitmap(imageBitmap)
        binding.edtTaskTitle.setText(task?.title)
        binding.edtTaskDescription.setText(task?.description)
        binding.spinnerTaskPriority.setSelection(
            getSpinnerIndex(
                taskPrioritySpinner,
                task?.priority!!.priority
            )
        )
        binding.spinnerTaskStatus.setSelection(
            getSpinnerIndex(
                taskStatusSpinner,
                task?.status!!.value
            )
        )
    }

    private fun setTaskPrioritySpinner() {
        taskPrioritySpinner = binding.spinnerTaskPriority

        arrayListTaskPriority.add(Constants.Priority.TASK_PRIORITY_LOW.priority)
        arrayListTaskPriority.add(Constants.Priority.TASK_PRIORITY_MEDIUM.priority)
        arrayListTaskPriority.add(Constants.Priority.TASK_PRIORITY_HIGH.priority)
        arrayListTaskPriority.add(Constants.Priority.TASK_PRIORITY_CRITICAL.priority)

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                this,
                R.layout.simple_spinner_dropdown_item,
                arrayListTaskPriority
            )
        taskPrioritySpinner.adapter = adapter
    }

    private fun setTaskStatusSpinner() {

        taskStatusSpinner = binding.spinnerTaskStatus
        arrayListTaskStatus.add(Constants.Status.NOT_STARTED.value)
        arrayListTaskStatus.add(Constants.Status.STARTED.value)
        arrayListTaskStatus.add(Constants.Status.CANCELLED.value)
        arrayListTaskStatus.add(Constants.Status.COMPLETED.value)

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                this,
                R.layout.simple_spinner_dropdown_item,
                arrayListTaskStatus
            )
        taskStatusSpinner.adapter = adapter
    }


    private fun setOnClickListeners() {
        binding.ivCreateTask.setOnClickListener {
            chooseImageFromGallery()
        }
        binding.arrowBack.setOnClickListener {
            finish()
        }
        binding.btnCreateTask.setOnClickListener {
            val priorityList = ArrayList<Category>()

            val taskTitle = binding.edtTaskTitle.text.toString()
            val taskDescription = binding.edtTaskDescription.text.toString()
            val taskPriority =
                Constants.Priority from binding.spinnerTaskPriority.selectedItem.toString()
            val taskStatus = Constants.Status from binding.spinnerTaskStatus.selectedItem.toString()
            val taskImageId = task?.imageId

            val taskPriorityEnum = Constants.Priority.values()
            val newTask = Task(
                task!!.id,
                taskTitle,
                taskDescription,
                taskPriority!!,
                task!!.reminderTime,
                taskStatus!!,
                taskImageId!!,
                imageBitmap,
                task!!.userId
            )

            mainActivityViewModel.updateTask(newTask, adapterPosition)


        }


    }

    private fun chooseImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        chooseImageLauncher.launch(intent)
    }

    private fun setObservers() {
        mainActivityViewModel.liveDataTaskOperation.observe(this)
        {
            if (it != null) {
                when (it) {

                    is TaskOperation.onSuccessUpdateTask -> {
                        Log.e(TAG, "update Observer update before finish: ")

                        finish()
                        Log.e(TAG, "update Observer update before after: ")

                    }
                    is TaskOperation.onSuccessDeleteTask -> {
                        finish()
                    }

                    is TaskOperation.onErrorUpdateTask -> {
                        Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }

        }
    }

    private fun getSpinnerIndex(spinner: Spinner, myString: String): Int {

        for (i in 0..spinner.count) {
            if (spinner.getItemAtPosition(i).toString().equals(myString, true)) {
                return i;
            }
        }

        return 0;
    }


}
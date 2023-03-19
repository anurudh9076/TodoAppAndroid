package com.example.todoapp.ui.activities

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.CustomApplication
import com.example.todoapp.R
import com.example.todoapp.constants.Constants
import com.example.todoapp.databinding.ActivityCreateTaskBinding
import com.example.todoapp.models.Category
import com.example.todoapp.sealedClasses.TaskOperation
import com.example.todoapp.viewmodel.MainActivityViewModel
import com.example.todoapp.viewmodel.MainActivityViewModelFactory
import java.util.*


class CreateTaskActivity : AppCompatActivity() {

    private val TAG = "MyTag"

    companion object {
        private var contextMainActivity: Context? = null
        fun setContext(context: FragmentActivity) {
            contextMainActivity = context
        }

    }

    private val arrayListTaskPriority = ArrayList<String>()
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var taskPrioritySpinner: Spinner
    private lateinit var binding: ActivityCreateTaskBinding
    private var imageBitmap: Bitmap? = null
    private var isReminderSet: Boolean = false




    private val c = Calendar.getInstance()
    private val myReminderDateTime = Calendar.getInstance()
    private var selectedYear = c[Calendar.YEAR]
    private var selectedMonth = c[Calendar.MONTH]
    private var selectedDay = c[Calendar.DAY_OF_MONTH]
    private var selectedHour = c[Calendar.HOUR_OF_DAY]
    private var selectedMinute = c[Calendar.MINUTE]

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

        val todoRepository = CustomApplication.todoRepository
        mainActivityViewModel = ViewModelProvider(
            contextMainActivity as AppCompatActivity,
            MainActivityViewModelFactory(todoRepository)
        )[MainActivityViewModel::class.java]
        setTaskPrioritySpinner()
        setOnClickListeners()
        setObservers()
        Log.d(TAG, "instance in create activity: ${mainActivityViewModel.hashCode()}")


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
            val taskPriority = binding.spinnerTaskPriority.selectedItem.toString()

            if(isReminderSet)
            {
                mainActivityViewModel.createTask(
                    taskTitle, taskDescription, priorityList, taskPriority, isReminderSet,myReminderDateTime,
                    Constants.Status.NOT_STARTED.value, imageBitmap
                )
                Log.e(TAG, "isremider set: $myReminderDateTime" )
            }
            else
            {
                mainActivityViewModel.createTask(
                    taskTitle, taskDescription, priorityList, taskPriority, false,null,
                    Constants.Status.NOT_STARTED.value, imageBitmap
                )

                Log.e(TAG, "isremider not set: $myReminderDateTime" )
            }

//            finish()

        }
        binding.switchSetReminder.setOnCheckedChangeListener { buttonView, isChecked ->
            val switchSetReminder = buttonView as SwitchCompat
            if (isChecked) {
                isReminderSet = true
                binding.tvTaskRemindTime.visibility = View.VISIBLE

                val c = Calendar.getInstance(TimeZone.getTimeZone("IST"))

                 selectedYear = c[Calendar.YEAR]
                 selectedMonth = c[Calendar.MONTH]
                 selectedDay = c[Calendar.DAY_OF_MONTH]
                 selectedHour = c[Calendar.HOUR_OF_DAY]
                 selectedMinute = c[Calendar.MINUTE]

                binding.tvTaskRemindTime.text="$selectedDay-${selectedMonth+1}-$selectedYear, $selectedHour:${selectedMinute+1}"
                myReminderDateTime.set(selectedYear,selectedMonth,selectedDay,selectedHour,selectedMinute)

            } else {
                isReminderSet = false
                binding.tvTaskRemindTime.visibility = View.INVISIBLE
            }
        }
        binding.tvTaskRemindTime.setOnClickListener {
            chooseDateTime()
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
            when (it) {


                is TaskOperation.onSuccessAddTask -> {
                    Log.e(TAG, "create Observer add: ")
                    finish()
                }

                is TaskOperation.onErrorAddTask -> {
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()

                }

                else -> {}
            }
        }

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
                android.R.layout.simple_spinner_dropdown_item,
                arrayListTaskPriority
            )
        taskPrioritySpinner.adapter = adapter
    }

   private fun chooseDateTime() {
        val selectDateTimeDialog = Dialog(this)
        selectDateTimeDialog.setContentView(R.layout.item_date_time_input)

        val btnSelectDate = selectDateTimeDialog.findViewById<Button>(R.id.btn_date_picker)
        val btnSelectTime = selectDateTimeDialog.findViewById<TextView>(R.id.btn_time_picker)
        val tvDate = selectDateTimeDialog.findViewById<TextView>(R.id.tv_date)
        val tvTime = selectDateTimeDialog.findViewById<TextView>(R.id.tv_time)
        val btnOk = selectDateTimeDialog.findViewById<Button>(R.id.btn_select_data_time_ok)

        tvDate.text="$selectedDay-${selectedMonth+1}-$selectedYear"
        tvTime.text="$selectedHour:$selectedMinute"


        btnSelectDate.setOnClickListener {

            // Get Current Date

            val datePickerDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->

                    tvDate.text = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                    selectedDay = dayOfMonth
                    selectedYear = year
                    selectedMonth = monthOfYear
                }, selectedYear, selectedMonth, selectedDay
            )
            datePickerDialog.show()

        }

        btnSelectTime.setOnClickListener {

            val timePickerDialog = TimePickerDialog(
                this,
                { view, hourOfDay, minute ->
                    tvTime.text = "$hourOfDay:$minute"
                    selectedMinute = minute
                    selectedHour = hourOfDay
                },selectedHour, selectedMinute, true
            )
            timePickerDialog.show()

        }
        btnOk.setOnClickListener {

            myReminderDateTime.set(selectedYear,selectedMonth,selectedDay,selectedHour,selectedMinute)
            binding.tvTaskRemindTime.text="$selectedDay-${selectedMonth+1}-$selectedYear, $selectedHour:$selectedMinute"
            Log.e(TAG, "choosenDateTime: $myReminderDateTime" )
            selectDateTimeDialog.dismiss()
        }

        selectDateTimeDialog.show()

    }

}



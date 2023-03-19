package com.example.todoapp.ui.activities

import android.R
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
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
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
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
import java.util.*
import javax.net.ssl.SSLEngineResult.Status
import kotlin.collections.ArrayList

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
    private var isReminderSet: Boolean = false

    private var task: Task? = null
    private var adapterPosition = -1


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
        setTaskStatusSpinner()
        setTaskPrioritySpinner()

        task = intent.getSerializableExtra(Constants.TASK) as Task
        adapterPosition = intent.getIntExtra(Constants.ADAPTER_POSITION, -1)
        val byteArray = intent.getByteArrayExtra(Constants.IMAGE_BYTE_ARRAY)
        if (byteArray != null) {
            imageBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)

        }
        isReminderSet=task!!.isReminderSet



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

        if(task?.isReminderSet==true && task?.reminderTime!=null)
        {
             selectedDay= task?.reminderTime!![Calendar.DAY_OF_MONTH]
             selectedMonth=task?.reminderTime!![Calendar.MONTH]
            selectedYear=task?.reminderTime!![Calendar.YEAR]
            selectedHour=task?.reminderTime!![Calendar.HOUR_OF_DAY]
            selectedMinute=task?.reminderTime!![Calendar.MINUTE]

            binding.tvTaskRemindTime.text="$selectedDay-${selectedMonth+1}-$selectedYear, $selectedHour:${selectedMinute+1}"
            binding.tvTaskRemindTime.visibility=View.VISIBLE
            binding.switchSetReminder.isChecked=true
        }


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
            val reminderTime=task?.reminderTime
            val taskStatus = Constants.Status from binding.spinnerTaskStatus.selectedItem.toString()
            val taskImageId = task?.imageId


            val taskPriorityEnum = Constants.Priority.values()
            val newTask = Task(
                task!!.id,
                taskTitle,
                taskDescription,
                taskPriority!!,
                isReminderSet,
                myReminderDateTime,
                taskStatus!!,
                taskImageId!!,
                imageBitmap,
                task!!.userId
            )

            mainActivityViewModel.updateTask(newTask, adapterPosition)


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


    private fun chooseDateTime() {
        val selectDateTimeDialog = Dialog(this)
        selectDateTimeDialog.setContentView(com.example.todoapp.R.layout.item_date_time_input)

        val btnSelectDate = selectDateTimeDialog.findViewById<Button>(com.example.todoapp.R.id.btn_date_picker)
        val btnSelectTime = selectDateTimeDialog.findViewById<TextView>(com.example.todoapp.R.id.btn_time_picker)
        val tvDate = selectDateTimeDialog.findViewById<TextView>(com.example.todoapp.R.id.tv_date)
        val tvTime = selectDateTimeDialog.findViewById<TextView>(com.example.todoapp.R.id.tv_time)
        val btnOk = selectDateTimeDialog.findViewById<Button>(com.example.todoapp.R.id.btn_select_data_time_ok)

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
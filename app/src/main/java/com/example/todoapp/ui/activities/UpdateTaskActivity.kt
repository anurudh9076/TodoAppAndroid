package com.example.todoapp.ui.activities

import android.R
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
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
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.CustomApplication
import com.example.todoapp.adapters.RecyclerCategoryAdapter
import com.example.todoapp.constants.Constants
import com.example.todoapp.databinding.ActivityCreateTaskBinding
import com.example.todoapp.databinding.BottomSheetAddCategoryBinding
import com.example.todoapp.models.Category
import com.example.todoapp.models.Task
import com.example.todoapp.sealedClasses.TaskOperation
import com.example.todoapp.viewmodel.MainActivityViewModel
import com.example.todoapp.viewmodel.MainActivityViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

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

    private val categoryList = ArrayList<Pair<Category, Boolean>>()
    private val arrayListSelectedCategories = ArrayList<Category>()
    private val setOfCategoriesIdForTask = HashSet<Long>()


    private var task: Task? = null
    private var adapterPosition = -1


    private val c = Calendar.getInstance()
    private var myReminderDateTime:Calendar? = Calendar.getInstance()
    private var selectedYear = c[Calendar.YEAR]
    private var selectedMonth = c[Calendar.MONTH]
    private var selectedDay = c[Calendar.DAY_OF_MONTH]
    private var selectedHour = c[Calendar.HOUR_OF_DAY]
    private var selectedMinute = c[Calendar.MINUTE]


    private val chooseImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.data?.let {
                val imageUri: Uri = it
                var bitmap:Bitmap
                if(Build.VERSION.SDK_INT < 28) {
                    bitmap= MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                    imageBitmap = bitmap
                } else {
                    val source = ImageDecoder.createSource(this.contentResolver, imageUri)
                    bitmap = ImageDecoder.decodeBitmap(source)
                }

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
        isReminderSet = task!!.isReminderSet



        initView() //initialize all the views with given task values

        val todoRepository = CustomApplication.todoRepository
        mainActivityViewModel = ViewModelProvider(
            contextMainActivity as AppCompatActivity,
            MainActivityViewModelFactory(todoRepository)
        )[MainActivityViewModel::class.java]


        setListeners()
        setObservers()

    }

    private fun initView() {
        binding.btnDeleteTaskActivity.visibility = View.VISIBLE
        binding.spinnerTaskStatus.visibility = View.VISIBLE
        binding.tvTaskStatus.visibility = View.VISIBLE

        binding.btnCreateTask.text = "Update"
        binding.tvCreateTask.text = "Update Task"


        if(imageBitmap!=null)
            binding.ivCreateTask.setImageBitmap(imageBitmap)



        binding.edtTaskTitle.setText(task?.title)
        binding.edtTaskDescription.setText(task?.description)

        if (task?.isReminderSet == true && task?.reminderTime != null) {

            selectedDay = task?.reminderTime!![Calendar.DAY_OF_MONTH]
            selectedMonth = task?.reminderTime!![Calendar.MONTH]
            selectedYear = task?.reminderTime!![Calendar.YEAR]
            selectedHour = task?.reminderTime!![Calendar.HOUR_OF_DAY]
            selectedMinute = task?.reminderTime!![Calendar.MINUTE]


            binding.tvTaskRemindTime.text = "$selectedHour:${selectedMinute }"
            binding.tvTaskRemindDate.text= "$selectedDay-${selectedMonth + 1}-$selectedYear"
            binding.tvTaskRemindTime.visibility = View.VISIBLE
            binding.tvTaskRemindDate.visibility = View.VISIBLE
            binding.tvBarrierDateTime.visibility= View.VISIBLE
            binding.switchSetReminder.isChecked = true

        }

        for (category in task!!.categoriesList)
            setOfCategoriesIdForTask.add(category.id)
        binding.tvCategoryCount.text="${setOfCategoriesIdForTask.size}"


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


    private fun setListeners() {
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
            val reminderTime = task?.reminderTime
            val taskStatus = Constants.Status from binding.spinnerTaskStatus.selectedItem.toString()
            val taskImageId = task?.imageId

            if(isReminderSet)
                myReminderDateTime!!.set(selectedYear,selectedMonth,selectedDay,selectedHour,selectedMinute)
            else
                myReminderDateTime=null
            Log.e(TAG, "setListeners: "+  "$selectedDay-${selectedMonth + 1}-$selectedYear | $selectedHour:$selectedMinute" )

            val taskPriorityEnum = Constants.Priority.values()
            val newTask = Task(
                task!!.id,
                taskTitle,
                taskDescription,
                taskPriority!!,
                arrayListSelectedCategories,
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
            if (isChecked) {
                isReminderSet = true
                binding.tvTaskRemindTime.visibility = View.VISIBLE
                binding.tvTaskRemindDate.visibility=View.VISIBLE
                binding.tvBarrierDateTime.visibility=View.VISIBLE

                val c = Calendar.getInstance()

                selectedYear = c[Calendar.YEAR]
                selectedMonth = c[Calendar.MONTH]
                selectedDay = c[Calendar.DAY_OF_MONTH]
                selectedHour = c[Calendar.HOUR_OF_DAY]
                selectedMinute = c[Calendar.MINUTE]


                binding.tvTaskRemindTime.text="$selectedHour:${selectedMinute+1}"
                binding.tvTaskRemindDate.text="$selectedDay-${selectedMonth+1}-$selectedYear"
                myReminderDateTime!!.set(selectedYear,selectedMonth,selectedDay,selectedHour,selectedMinute)

            } else {
                isReminderSet = false
                binding.tvTaskRemindTime.visibility = View.INVISIBLE
                binding.tvTaskRemindDate.visibility=View.INVISIBLE
                binding.tvBarrierDateTime.visibility=View.INVISIBLE

            }
        }
        binding.tvTaskRemindDate.setOnClickListener {

            // Get Current Date

            val datePickerDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->


                    selectedDay = dayOfMonth
                    selectedYear = year
                    selectedMonth = monthOfYear
                    binding.tvTaskRemindDate.text="$selectedDay-${selectedMonth+1}-$selectedYear"
                }, selectedYear, selectedMonth, selectedDay
            )
            datePickerDialog.show()

        }

        binding.tvTaskRemindTime.setOnClickListener {

            val timePickerDialog = TimePickerDialog(
                this,
                { view, hourOfDay, minute ->
                    selectedMinute = minute
                    selectedHour = hourOfDay
                    binding.tvTaskRemindTime.text="$selectedHour:${selectedMinute}"
                },selectedHour, selectedMinute, true
            )
            timePickerDialog.show()

        }


        binding.btnSelectCategory.setOnClickListener {
            showBottomSheetDialog()
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

                    is TaskOperation.OnSuccessUpdateTask -> {
                        Log.e(TAG, "update Observer update before finish: ")

                        finish()
                        Log.e(TAG, "update Observer update before after: ")

                    }
                    is TaskOperation.OnSuccessDeleteTask -> {
                        finish()
                    }

                    is TaskOperation.OnErrorUpdateTask -> {
                        Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }

        }
        mainActivityViewModel.liveDataCategoryOperation.observe(this)
        {
            when (it) {
                is TaskOperation.OnSuccessFetchAllCategories -> {
                    for (category in it.list) {

                        if (setOfCategoriesIdForTask.contains(category.id))
                            categoryList.add(Pair(category, true))
                        else
                            categoryList.add(Pair(category, false))
                    }

                }
                else -> {}
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

        val btnSelectDate =
            selectDateTimeDialog.findViewById<Button>(com.example.todoapp.R.id.btn_date_picker)
        val btnSelectTime =
            selectDateTimeDialog.findViewById<TextView>(com.example.todoapp.R.id.btn_time_picker)
        val tvDate = selectDateTimeDialog.findViewById<TextView>(com.example.todoapp.R.id.tv_date)
        val tvTime = selectDateTimeDialog.findViewById<TextView>(com.example.todoapp.R.id.tv_time)
        val btnOk =
            selectDateTimeDialog.findViewById<Button>(com.example.todoapp.R.id.btn_select_data_time_ok)

        tvDate.text = "$selectedDay-${selectedMonth + 1}-$selectedYear"
        tvTime.text = "$selectedHour:$selectedMinute"


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
                }, selectedHour, selectedMinute, true
            )
            timePickerDialog.show()

        }
        btnOk.setOnClickListener {

//            myReminderDateTime.set(
//                selectedYear,
//                selectedMonth,
//                selectedDay,
//                selectedHour,
//                selectedMinute
//            )
            binding.tvTaskRemindTime.text =
                "$selectedDay-${selectedMonth + 1}-$selectedYear | $selectedHour:$selectedMinute"
            Log.e(TAG, "choosenDateTime: $myReminderDateTime")
            selectDateTimeDialog.dismiss()
        }

        selectDateTimeDialog.show()
    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bindingBottomSheet = BottomSheetAddCategoryBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bindingBottomSheet.root)


        val categoryAdapter = RecyclerCategoryAdapter(this, categoryList)
        categoryAdapter.set(object : RecyclerCategoryAdapter.OnItemClickListener {
            override fun onItemChecked(category: Category, position: Int) {
                arrayListSelectedCategories.add(category)
                Log.e(TAG, "onItemChecked: size:-> ${arrayListSelectedCategories.size}")
                categoryList[position] = Pair(category, true)

            }

            override fun onItemUnchecked(category: Category, position: Int) {
                arrayListSelectedCategories.remove(category)
                Log.e(TAG, "onItemChecked: size:-> ${arrayListSelectedCategories.size}")
                categoryList[position] = Pair(category, false)

            }

        })
        bottomSheetDialog.setOnDismissListener {

            arrayListSelectedCategories.clear()
            for (category in categoryList) {
                if (category.second) {
                    arrayListSelectedCategories.add(category.first)
                }

            }

            binding.tvCategoryCount.text = "${arrayListSelectedCategories.size}"
            binding.tvCategoryCount.setTextColor(Color.BLUE)


        }
        bindingBottomSheet.recyclerAddCategoryBottomSheet.adapter = categoryAdapter
        bottomSheetDialog.show()

    }


}
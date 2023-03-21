package com.example.todoapp.ui.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.CustomApplication
import com.example.todoapp.adapters.RecyclerCategoryAdapter
import com.example.todoapp.constants.Constants
import com.example.todoapp.databinding.ActivityCreateTaskBinding
import com.example.todoapp.databinding.BottomSheetAddCategoryBinding
import com.example.todoapp.models.Category
import com.example.todoapp.sealedClasses.CategoryOperation
import com.example.todoapp.sealedClasses.TaskOperation
import com.example.todoapp.viewmodel.MainActivityViewModel
import com.example.todoapp.viewmodel.MainActivityViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*


class CreateTaskActivity : AppCompatActivity() {

    private val TAG = "MyTag"


    private val arrayListTaskPriority = ArrayList<String>()
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var taskPrioritySpinner: Spinner
    private lateinit var binding: ActivityCreateTaskBinding
    private var imageBitmap: Bitmap? = null
    private var isReminderSet: Boolean = false
    private val arrayListSelectedCategories = ArrayList<Category>()
    private val categoryList = ArrayList<Pair<Category, Boolean>>()
    private val categoryAdapter = RecyclerCategoryAdapter(this, categoryList)


    private val c = Calendar.getInstance(TimeZone.getTimeZone("IST"))
    private val myReminderDateTime = Calendar.getInstance(TimeZone.getTimeZone("IST"))
    private var selectedYear = c[Calendar.YEAR]
    private var selectedMonth = c[Calendar.MONTH]
    private var selectedDay = c[Calendar.DAY_OF_MONTH]
    private var selectedHour = c[Calendar.HOUR_OF_DAY]
    private var selectedMinute = c[Calendar.MINUTE]

    private val chooseImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.data?.let {
                val imageUri: Uri = it
                var bitmap: Bitmap
                if (Build.VERSION.SDK_INT < 28) {
                    bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                    imageBitmap = bitmap
                } else {
                    val source = ImageDecoder.createSource(this.contentResolver, imageUri)
                    bitmap = ImageDecoder.decodeBitmap(source)
                }

                imageBitmap = bitmap
                binding.ivCreateTask.setImageBitmap(bitmap)
            }
        }

    companion object {
        private var contextMainActivity: Context? = null
        fun setContext(context: FragmentActivity) {
            contextMainActivity = context
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

        mainActivityViewModel.fetchAllCategoriesOfUser()
        setTaskPrioritySpinner()
        setOnClickListeners()
        setObservers()
    }


    private fun setOnClickListeners() {
        binding.ivCreateTask.setOnClickListener {
            chooseImageFromGallery()
        }
        binding.arrowBack.setOnClickListener {
            finish()
        }
        binding.btnCreateTask.setOnClickListener {

            val taskTitle = binding.edtTaskTitle.text.toString()
            val taskDescription = binding.edtTaskDescription.text.toString()
            val taskPriority = binding.spinnerTaskPriority.selectedItem.toString()

            if (isReminderSet) {


                myReminderDateTime.set(
                    selectedYear,
                    selectedMonth,
                    selectedDay,
                    selectedHour,
                    selectedMinute
                )
                mainActivityViewModel.createTask(
                    taskTitle,
                    taskDescription,
                    arrayListSelectedCategories,
                    taskPriority,
                    true,
                    myReminderDateTime,
                    Constants.Status.NOT_STARTED.value,
                    imageBitmap
                )

            } else {
                mainActivityViewModel.createTask(
                    taskTitle,
                    taskDescription,
                    arrayListSelectedCategories,
                    taskPriority,
                    false,
                    null,
                    Constants.Status.NOT_STARTED.value,
                    imageBitmap
                )


            }

//            finish()

        }
        binding.switchSetReminder.setOnCheckedChangeListener { buttonView, isChecked ->
            val switchSetReminder = buttonView as SwitchCompat
            if (isChecked) {
                isReminderSet = true
                binding.tvTaskRemindTime.visibility = View.VISIBLE
                binding.tvTaskRemindDate.visibility = View.VISIBLE
                binding.tvBarrierDateTime.visibility = View.VISIBLE

                val c = Calendar.getInstance(TimeZone.getTimeZone("IST"))

                selectedYear = c[Calendar.YEAR]
                selectedMonth = c[Calendar.MONTH]
                selectedDay = c[Calendar.DAY_OF_MONTH]
                selectedHour = c[Calendar.HOUR_OF_DAY]
                selectedMinute = c[Calendar.MINUTE]


                binding.tvTaskRemindTime.text = "$selectedHour:${selectedMinute + 1}"
                binding.tvTaskRemindDate.text = "$selectedDay-${selectedMonth + 1}-$selectedYear"
                myReminderDateTime.set(
                    selectedYear,
                    selectedMonth,
                    selectedDay,
                    selectedHour,
                    selectedMinute
                )

            } else {
                isReminderSet = false
                binding.tvTaskRemindTime.visibility = View.INVISIBLE
                binding.tvTaskRemindDate.visibility = View.INVISIBLE
                binding.tvBarrierDateTime.visibility = View.INVISIBLE

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
                    binding.tvTaskRemindDate.text =
                        "$selectedDay-${selectedMonth + 1}-$selectedYear"
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
                    binding.tvTaskRemindTime.text = "$selectedHour:${selectedMinute}"
                }, selectedHour, selectedMinute, true
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
            when (it) {


                is TaskOperation.OnSuccessAddTask -> {
                    Log.e(TAG, "create Observer add: ")
                    finish()
                }

                is TaskOperation.OnErrorAddTask -> {
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()

                }

                else -> {}
            }
        }
        mainActivityViewModel.liveDataCategoryOperation.observe(this)
        {
            when (it) {
                is CategoryOperation.OnSuccessFetchAllCategories -> {
                    categoryList.clear()
                    for (category in it.list)
                        categoryList.add(Pair(category, false))
                }
                is CategoryOperation.OnSuccessAddCategory -> {
                    categoryList.add(Pair(it.category, false))
                    categoryAdapter.notifyItemInserted(categoryList.size - 1)
                }
                else -> {


                }
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


    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bindingBottomSheet = BottomSheetAddCategoryBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bindingBottomSheet.root)



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

        bindingBottomSheet.btnCreateNewCategoryBottomSheet.setOnClickListener {

            CreateCategoryActivity.setContextForViewModel(contextMainActivity as AppCompatActivity)
            val intentCreateCategory =
                Intent(bottomSheetDialog.context, CreateCategoryActivity()::class.java)
            startActivity(intentCreateCategory)
        }
        bindingBottomSheet.btnSelectCategoriesBottomSheet.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }


}



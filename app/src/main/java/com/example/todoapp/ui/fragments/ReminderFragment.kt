package com.example.todoapp.ui.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.DbHelper.TodoDBHelper
import com.example.todoapp.adapters.RecyclerTaskAdapter
import com.example.todoapp.constants.Constants
import com.example.todoapp.databinding.FragmentTasksBinding
import com.example.todoapp.models.Task
import com.example.todoapp.repository.TodoRepository
import com.example.todoapp.sealedClasses.TaskOperation
import com.example.todoapp.ui.activities.CreateTaskActivity
import com.example.todoapp.ui.activities.UpdateTaskActivity
import com.example.todoapp.viewmodel.MainActivityViewModel
import com.example.todoapp.viewmodel.MainActivityViewModelFactory
import java.io.ByteArrayOutputStream


class ReminderFragment : Fragment() {

    private val TAG = "MyTag"
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var binding: FragmentTasksBinding
    private var arrayListTask = ArrayList<Task>()
    private lateinit var taskAdapter: RecyclerTaskAdapter
//    private var loggedInUser: User? = CustomApplication.loggedInUser



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")

        val dbHelper = TodoDBHelper.getInstance(requireContext())
        val todoRepository = TodoRepository(dbHelper)
        mainActivityViewModel = ViewModelProvider(
            requireActivity(),
            MainActivityViewModelFactory(todoRepository)
        )[MainActivityViewModel::class.java]
//        if (loggedInUser != null)
//            mainActivityViewModel.fetchTasksOfUser(loggedInUser!!.id)

    }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTasksBinding.inflate(inflater, container, false);
        initViews()
        setObservers()
        setOnCLickListeners()
//        mainActivityViewModel.getLoggedInUser()

        taskAdapter = RecyclerTaskAdapter(requireContext(), arrayListTask)

        taskAdapter.set(object : RecyclerTaskAdapter.OnItemClickListener {
            override fun onItemClick(task: Task,adapterPosition: Int) {
                Log.e("MyTag", "onItemClick: ")
                Log.d("MyTag", "BtnOnClick: ")
                UpdateTaskActivity.setContext(requireActivity())
                val intentUpdateTask = Intent(requireContext(), UpdateTaskActivity()::class.java)

                intentUpdateTask.putExtra(Constants.ADAPTER_POSITION,adapterPosition)

                if(task.imageBitmap!=null)
                {
                    val bStream = ByteArrayOutputStream()
                    task.imageBitmap!!.compress(Bitmap.CompressFormat.PNG, 100,bStream)
                    val byteArray=bStream.toByteArray()
                    intentUpdateTask.putExtra(Constants.IMAGE_BYTE_ARRAY,byteArray)
                    Log.e(TAG, "bitmap sent: ${byteArray.size}" )
                    Log.e(TAG, "bitmap sent: ${byteArray}" )
                    task.imageBitmap=null
                }

                intentUpdateTask.putExtra(Constants.TASK,task)

                startActivity(intentUpdateTask)

            }

            override fun onItemLongCLick(task: Task) {
//                mainActivityViewModel.deleteTask(task,po)
                Log.e("MyTag", "onItemLongCLick: ")
            }

            override fun onClickButtonDelete(task: Task, position: Int) {
                Log.e("MyTag", "onClickButtonDelete: ")
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Delete Task")
                builder.setMessage("are you sure?")
                //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

                builder.setPositiveButton("Ok") { dialog, which ->
                    mainActivityViewModel.deleteTask(task, position)

                }

                builder.setNegativeButton("Cancel") { dialog, which ->

                }
                builder.show()

            }

        })
        binding.taskRecyclerView.adapter = taskAdapter

        Log.d("MyTag", "instance in Fragment: ${mainActivityViewModel.hashCode()}")

        return binding.root;
    }

    private fun initViews()
    {
        binding.tvAllTask.text=Constants.REMINDERS
        binding.btnAddTask.visibility=View.INVISIBLE
        binding.edtSearchTask.visibility=View.GONE
        binding.btnSearchTask.visibility=View.GONE
        binding.btnFilterTask.visibility=View.GONE


    }

    private fun setOnCLickListeners() {

        binding.btnAddTask.setOnClickListener {
//           addTaskDialog()

            Log.d("MyTag", "BtnOnClick: ")
            CreateTaskActivity.setContext(requireActivity())
            val intentCreateTask = Intent(requireContext(), CreateTaskActivity()::class.java)
            startActivity(intentCreateTask)
        }



    }

    private fun setObservers() {

        mainActivityViewModel.liveDataTaskOperation.observe(this)
        {
            when (it) {
                is TaskOperation.OnSuccessFetchAllTasks -> {

                    val arrayReminderList=ArrayList<Task>()

                    for(task in it.list)
                    {
                        if(task.isReminderSet)
                            arrayReminderList.add(task)
                    }

                    taskAdapter.arrayList=arrayReminderList
                    taskAdapter.notifyDataSetChanged()

                }

                is TaskOperation.OnSuccessAddTask -> {
                    taskAdapter.arrayList.add(it.task)
                    taskAdapter.notifyItemInserted(taskAdapter.arrayList.size - 1)
                    Toast.makeText(requireContext(),"Task Created Successfully",Toast.LENGTH_SHORT).show()

                }
                is TaskOperation.OnSuccessDeleteTask -> {
                    taskAdapter.arrayList.removeAt(it.position)
                    taskAdapter.notifyItemRemoved(it.position)
                }

                is TaskOperation.OnSuccessUpdateTask ->
                {
                    if(!it.task.isReminderSet)
                    {
                        taskAdapter.arrayList.removeAt(it.position)
                        taskAdapter.notifyItemRemoved(it.position)
                    }
                    else
                    {
                        taskAdapter.arrayList[it.position] = it.task
                        taskAdapter.notifyItemChanged(it.position)
                    }

                    Toast.makeText(requireContext(),"Task Updated Successfully",Toast.LENGTH_SHORT).show()

                }
                is TaskOperation.OnErrorFetchAllCTask -> {
                    Log.d(TAG, "TaskFetchFailed ")
                }
                is TaskOperation.OnErrorAddTask -> {
                    Toast.makeText(requireContext(),it.error,Toast.LENGTH_SHORT).show()

                }
                is TaskOperation.OnErrorDeleteTask -> {
                    Toast.makeText(requireContext(),it.error,Toast.LENGTH_SHORT).show()

                }

                is TaskOperation.OnErrorUpdateTask -> {
                    Toast.makeText(requireContext(),it.error,Toast.LENGTH_SHORT).show()
                }
                else -> {

                }
            }
        }

    }


}
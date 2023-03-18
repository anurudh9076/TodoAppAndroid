package com.example.todoapp.ui.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.DbHelper.TodoDBHelper
import com.example.todoapp.R
import com.example.todoapp.adapters.RecyclerTaskAdapter
import com.example.todoapp.databinding.FragmentDashboardBinding
import com.example.todoapp.databinding.FragmentTasksBinding
import com.example.todoapp.models.Task
import com.example.todoapp.repository.TodoRepository
import com.example.todoapp.ui.activities.CreateTaskActivity
import com.example.todoapp.viewmodel.MainActivityViewModel
import com.example.todoapp.viewmodel.MainActivityViewModelFactory


class TasksFragment : Fragment() {

    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var binding: FragmentTasksBinding
    private var  arrayListTask=ArrayList<Task>()
    private lateinit var taskAdapter:RecyclerTaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dbHelper = TodoDBHelper.getInstance(requireContext())
        val todoRepository = TodoRepository(dbHelper)
        mainActivityViewModel = ViewModelProvider(this, MainActivityViewModelFactory(todoRepository))[MainActivityViewModel::class.java]




    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTasksBinding.inflate(inflater,container,false);
        setObservers()
        setOnCLickListeners()

        taskAdapter=RecyclerTaskAdapter(requireContext(),arrayListTask)
        taskAdapter.set(object: RecyclerTaskAdapter.OnItemClickListener {
            override fun onItemClick(task: Task) {
                Log.e("MyTag", "onItemClick: " )

            }

        })
        binding.taskRecyclerView.adapter=taskAdapter
        mainActivityViewModel.getLoggedInUser()
        mainActivityViewModel.fetchTasksOfUser(2)

        return binding.root;

    }

    private fun setOnCLickListeners() {

        binding.btnAddTask.setOnClickListener {
            val intentCreateTask=Intent(requireContext(),CreateTaskActivity::class.java)
            startActivity(intentCreateTask)
        }
    }

    private fun setObservers() {
      mainActivityViewModel.liveDataTasksList.observe(this)
      {
          arrayListTask.addAll(it)
          taskAdapter.notifyDataSetChanged()

      }

    }


}
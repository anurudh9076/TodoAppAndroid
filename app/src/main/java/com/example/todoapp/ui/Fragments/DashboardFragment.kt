package com.example.todoapp.ui.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todoapp.CustomApplication
import com.example.todoapp.DbHelper.TodoDBHelper
import com.example.todoapp.R
import com.example.todoapp.adapters.RecyclerTaskAdapter
import com.example.todoapp.constants.Constants
import com.example.todoapp.databinding.FragmentDashboardBinding
import com.example.todoapp.models.Task
import com.example.todoapp.models.User
import com.example.todoapp.repository.TodoRepository
import com.example.todoapp.sealedClasses.TaskOperation
import com.example.todoapp.ui.activities.LoginActivity
import com.example.todoapp.viewmodel.MainActivityViewModel
import com.example.todoapp.viewmodel.MainActivityViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.net.ssl.SSLEngineResult.Status


class DashboardFragment : Fragment() {

    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var binding: FragmentDashboardBinding


    private lateinit var yourTaskAdapter: RecyclerTaskAdapter
    private lateinit var completedTaskAdapter: RecyclerTaskAdapter

//    private var loggedInUser: User? = CustomApplication.loggedInUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dbHelper = TodoDBHelper.getInstance(requireContext())
        val todoRepository = TodoRepository(dbHelper)
        mainActivityViewModel = ViewModelProvider(
            requireActivity(),
            MainActivityViewModelFactory(todoRepository)
        )[MainActivityViewModel::class.java]


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        setObservers()
        setOnCLickListeners()

        mainActivityViewModel.getLoggedInUser()
        yourTaskAdapter = RecyclerTaskAdapter(requireContext(), ArrayList())
        completedTaskAdapter=RecyclerTaskAdapter(requireContext(), ArrayList())

        binding.recyclerDashboardYourTask.adapter=yourTaskAdapter
        binding.recyclerDashboardCompletedTask.adapter=completedTaskAdapter

        return binding.root;
    }

    private fun setObservers() {
        mainActivityViewModel.liveDataLoggedInUser.observe(this) {
//            if(it!=null)
//                mainActivityViewModel.fetchTasksOfUser(it!!.id)
            binding.tvUserNameGreet.text = "Hello! ${it.name}"
            if (it.image_bitmap != null) {
                binding.ivUserDashboard.setImageBitmap(it.image_bitmap)
            } else {
                binding.ivUserDashboard.setImageResource(R.drawable.icon_user_default)
            }
        }
        mainActivityViewModel.liveDataIsUserLoggedIn.observe(this)
        {
            if (it == false) {
                Toast.makeText(requireContext(), "Logged Out Successfully", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
            }
        }


        mainActivityViewModel.liveDataTaskOperation.observe(this)
        {
            when (it) {
                is TaskOperation.onSuccessFetchAllTasks -> {

                    val arrayListCompletedTask=ArrayList<Task>()
                    val arrayListYourTask=ArrayList<Task>()
                    for(task in it.list)
                    {
                        if(task.status==(Constants.Status.COMPLETED))
                            arrayListCompletedTask.add(task)
                        else
                            arrayListYourTask.add(task)
                    }
                    yourTaskAdapter.arrayList=arrayListYourTask
                    completedTaskAdapter.arrayList=arrayListCompletedTask

                    yourTaskAdapter.notifyDataSetChanged()
                    completedTaskAdapter.notifyDataSetChanged()
                }

                else -> {

                }
            }

        }
    }

    private fun setOnCLickListeners() {
        binding.btnLogout.setOnClickListener {
            mainActivityViewModel.logOutUser()
        }
    }


}
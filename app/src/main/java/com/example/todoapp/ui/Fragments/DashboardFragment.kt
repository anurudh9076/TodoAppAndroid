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
import com.example.todoapp.DbHelper.TodoDBHelper
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentDashboardBinding
import com.example.todoapp.repository.TodoRepository
import com.example.todoapp.ui.activities.LoginActivity
import com.example.todoapp.viewmodel.MainActivityViewModel
import com.example.todoapp.viewmodel.MainActivityViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DashboardFragment : Fragment() {

    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var binding:FragmentDashboardBinding

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
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(inflater,container,false);
        setObservers()
        setOnCLickListeners()

        mainActivityViewModel.getLoggedInUser()

        return binding.root;
    }

    private fun setObservers()
    {
        mainActivityViewModel.liveDataLoggedInUser.observe(this) {
            binding.tvUserNameGreet.text = "Hello! ${it.name}"
            if(it.image_bitmap!=null)
            {
                binding.ivUserDashboard.setImageBitmap(it.image_bitmap)
            }
        }
        mainActivityViewModel.liveDataIsUserLoggedIn.observe(this)
        {
            if(it==false)
            {
                Toast.makeText(requireContext(),"Logged Out Successfully",Toast.LENGTH_SHORT).show()
                val intent= Intent(requireContext(),LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
    private fun setOnCLickListeners()
    {
        binding.btnLogout.setOnClickListener {
           mainActivityViewModel.logOutUser()
        }
    }


}
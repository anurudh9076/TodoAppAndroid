package com.example.todoapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.DbHelper.TodoDBHelper
import com.example.todoapp.R
import com.example.todoapp.ui.Fragments.DashboardFragment
import com.example.todoapp.ui.Fragments.MoreFragment
import com.example.todoapp.ui.Fragments.ReminderFragment
import com.example.todoapp.ui.Fragments.TasksFragment
import com.example.todoapp.databinding.ActivityMainBinding
import com.example.todoapp.models.User
import com.example.todoapp.repository.TodoRepository
import com.example.todoapp.viewmodel.LoginViewModel
import com.example.todoapp.viewmodel.LoginViewModelFactory
import com.example.todoapp.viewmodel.MainActivityViewModel
import com.example.todoapp.viewmodel.MainActivityViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private  var loggedInUser: User?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val dbHelper = TodoDBHelper.getInstance(this)
        val todoRepository = TodoRepository(dbHelper)
        mainActivityViewModel = ViewModelProvider(this, MainActivityViewModelFactory(todoRepository))[MainActivityViewModel::class.java]

        initBottomNavigationMenu()
        setObservers()



    }

    private fun initBottomNavigationMenu() {

        val dashboardFragment= DashboardFragment()
        val tasksFragment= TasksFragment()
        val reminderFragment= ReminderFragment()
        val moreFragment= MoreFragment()

        setCurrentFragment(dashboardFragment)
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.dashboard ->setCurrentFragment(dashboardFragment)
                R.id.tasks ->setCurrentFragment(tasksFragment)
                R.id.reminders ->setCurrentFragment(reminderFragment)
                R.id.more ->setCurrentFragment(moreFragment)

            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }
    private fun setObservers()
    {
        mainActivityViewModel.liveDataLoggedInUser.observe(this) {
                loggedInUser=it
        }
    }


}
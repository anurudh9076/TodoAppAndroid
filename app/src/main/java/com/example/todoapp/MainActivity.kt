package com.example.todoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.todoapp.ui.Fragments.DashboardFragment
import com.example.todoapp.ui.Fragments.MoreFragment
import com.example.todoapp.ui.Fragments.ReminderFragment
import com.example.todoapp.ui.Fragments.TasksFragment
import com.example.todoapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val dashboardFragment= DashboardFragment()
        val tasksFragment= TasksFragment()
        val reminderFragment= ReminderFragment()
        val moreFragment= MoreFragment()

        setCurrentFragment(dashboardFragment)


        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.dashboard->setCurrentFragment(dashboardFragment)
                R.id.tasks->setCurrentFragment(tasksFragment)
                R.id.reminders->setCurrentFragment(reminderFragment)
                R.id.more->setCurrentFragment(moreFragment)

            }
            true
        }

    }

    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }
}
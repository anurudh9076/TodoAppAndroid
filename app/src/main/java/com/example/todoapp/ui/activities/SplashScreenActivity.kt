package com.example.todoapp.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.todoapp.R
import com.example.todoapp.constants.Constants
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        GlobalScope.launch {
            delay(2000)
            val pref = getSharedPreferences(Constants.USER_LOGIN_STATE_PREFERENCE, MODE_PRIVATE)
            val check = pref.getBoolean(Constants.IS_USER_LOGGED_IN, false)

            val intent: Intent = if (check) {
                Intent(applicationContext, MainActivity::class.java)
            } else {
                Intent(applicationContext, LoginActivity::class.java)
            }

            startActivity(intent)
            finish()
        }


    }
}
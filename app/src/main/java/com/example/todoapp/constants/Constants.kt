package com.example.todoapp.constants

object Constants {
    const val USER_LOGIN_STATE_PREFERENCE = "user_login_state_preference"
    const val IS_USER_LOGGED_IN = "is_user_logged_in"
    const val USER_ID = "user_id"


    enum class Priority(val id: Int, val priority: String) {
        TASK_PRIORITY_LOW(0, "Low"),
        TASK_PRIORITY_MEDIUM(5, "Medium"),
        TASK_PRIORITY_HIGH(10, "High"),
        TASK_PRIORITY_CRITICAL(15, "Critical")
    }

    enum class Status(val value: String) {
        NOT_STARTED("NotStarted"),
        STARTED("Started"),
        COMPLETED("Completed"),
        CANCELLED("Cancelled")




    }
}
package com.example.todoapp.constants

object Constants {
    const val REMINDERS="Reminders"
    const val IMAGE_BITMAP="image_bitmap"
    const val IMAGE_BYTE_ARRAY="image_byte_array"
    const val ADAPTER_POSITION ="adapter_position"
    const val TASK="task"
    const val USER_LOGIN_STATE_PREFERENCE = "user_login_state_preference"
    const val IS_USER_LOGGED_IN = "is_user_logged_in"
    const val USER_ID = "user_id"


    enum class Priority(val id: Int, val priority: String) {
        TASK_PRIORITY_LOW(0, "Low"),
        TASK_PRIORITY_MEDIUM(5, "Medium"),
        TASK_PRIORITY_HIGH(10, "High"),
        TASK_PRIORITY_CRITICAL(15, "Critical");

        companion object {
            private val map = Priority.values().associateBy { it.priority }
            private val map1 = Priority.values().associateBy { it.id }
            infix fun from(value: String) = map[value]
            infix fun from(id:Int)= map1[id]

        }
    }

    enum class Status(val value: String) {
        NOT_STARTED("NotStarted"),
        STARTED("Started"),
        COMPLETED("Completed"),
        CANCELLED("Cancelled");

        companion object {
            private val map = Status.values().associateBy { it.value }
            infix fun from(value: String) = map[value]
        }




    }
}